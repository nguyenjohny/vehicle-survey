package aconex;

import aconex.input.ImpactParser;
import aconex.input.Parser;
import aconex.input.PneumaticImpact;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static aconex.TwoAxleVehicleSample.Direction.NORTH_BOUND;
import static aconex.TwoAxleVehicleSample.Direction.SOUTH_BOUND;
import static java.lang.System.exit;

public class VehicleSurveyor {
    private static final int AVERAGE_MAX_SPEED = 60; // 60kph

    final private Parser dataParser;
    private Map<Integer, List<TwoAxleVehicleSample>> sample;

    public VehicleSurveyor(Parser dataParser) {
        this.dataParser = dataParser;
    }

    public void readData(String inputFile) {
        Map<Integer, Vector<PneumaticImpact>> data = dataParser.readFilePath(inputFile);
        sample = translateSamples(data);
    }

    public List<TwoAxleVehicleSample> getDaySamples(Integer dayIndex, TwoAxleVehicleSample.Direction direction) {
        return sample.get(dayIndex).stream()
                .filter(i -> i.getDirection() == direction)
                .collect(Collectors.toList());
    }

    public List<TwoAxleVehicleSample> getSessionSamples() {
        final List<TwoAxleVehicleSample> list = new ArrayList<>();
        for (List<TwoAxleVehicleSample> value : sample.values()) {
            list.addAll(value);
        }
        return list;
    }

    public List<TwoAxleVehicleSample> getSessionSamples(TwoAxleVehicleSample.Direction direction) {
        return getSessionSamples().stream().filter(i -> i.getDirection() == direction).collect(Collectors.toList());
    }

    /**
     * There are only two hoses, the long one spanning both directions and then a second
     * hose which works as a method of determining the other direction
     */
    private Map<Integer, List<TwoAxleVehicleSample>> translateSamples(Map<Integer, Vector<PneumaticImpact>> data)  {
        Map<Integer, List<TwoAxleVehicleSample>> map = new HashMap<>();
        // INVARIANT: abs(distance(A) - distance(B)) <  abs(distance(axle1) - distance(axle2))
        // this means you will be able to discern between the two directions where B will always

        data.entrySet().stream().forEach(
                es -> {
                    final List<TwoAxleVehicleSample> daySampleOut = new ArrayList<>();
                    final List<PneumaticImpact> daySampleIn = es.getValue();
                    int index = 1;

                    Long triggeredDualSampleMs = null;
                    while (index < daySampleIn.size()) {
                        final PneumaticImpact prev = daySampleIn.get(index - 1), curr = daySampleIn.get(index);

                        // INVARIANT: you will never have two sequential B as the car cant hop over A.
                        if (Objects.equals(curr.getId(), prev.getId())) {
                            daySampleOut.add(new TwoAxleVehicleSample(NORTH_BOUND, prev.getMillisOnDay(), curr.getMillisOnDay()));
                        } else {
                            if (triggeredDualSampleMs == null) {
                                triggeredDualSampleMs = curr.getMillisOnDay();
                            } else {  // close Sample-B
                                daySampleOut.add(new TwoAxleVehicleSample(SOUTH_BOUND, triggeredDualSampleMs, curr.getMillisOnDay()));
                                triggeredDualSampleMs = null;
                            }
                        }
                        index += 2; // always two axles.
                    }

                    map.put(es.getKey(), daySampleOut);
                }
        );

        return map;
    }

    public Map<Integer, Integer> getPeakHoursOfDay(int dayIndex, long intervalMs) {
        final List<TwoAxleVehicleSample> input = sample.get(dayIndex);
        final Map<Integer, Integer> distribution = new HashMap<>();

        int lastIntervalIndex = 1;
        for (TwoAxleVehicleSample vehicle : input) {

            long startIntervalMs = (lastIntervalIndex - 1) * intervalMs;
            long endIntervalMs = lastIntervalIndex * intervalMs;

            // INVARIANT: the data is always sorted in the input file by ascending time.
            if (vehicle.getStartMs() > endIntervalMs) { // bump up the interval
                lastIntervalIndex = (int) Math.ceil((double)vehicle.getStartMs() / intervalMs);
                endIntervalMs = lastIntervalIndex * intervalMs;
                startIntervalMs = endIntervalMs - intervalMs;
            }

            if (vehicle.getStartMs() > startIntervalMs && vehicle.getStartMs() < endIntervalMs) {
                distribution.compute(lastIntervalIndex, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer interval, Integer count) {
                        return count == null ? 1 : count + 1;
                    }
                });
            }
        }

        return distribution;
    }

    /**
     * Calculates the (rough) speed distribution of traffic.
     * @param entries selected samples to process.
     * @return speed in km/h
     */
    public double speedDistributionOfTraffic(List<TwoAxleVehicleSample> entries) {
        double sum = entries.stream().mapToDouble(TwoAxleVehicleSample::calculateSpeed).sum();
        return sum / entries.size();
    }

    /**
     * Rough distance between cars during various periods
     * @param entries selected samples to process.
     * @return distance in meters
     */
    public double roughDistanceBetweenCars(List<TwoAxleVehicleSample> entries) {
        if (!entries.isEmpty()) {
            double avgTimeMsBetween = (entries.get(entries.size() - 1).getStartMs() - entries.get(0).getStartMs()) / entries.size();
            return AVERAGE_MAX_SPEED * 1000 * (avgTimeMsBetween / TimeUnit.HOURS.toMillis(1));
        }
        return 0;
    }

}
