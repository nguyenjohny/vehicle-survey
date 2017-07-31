package aconex;

import aconex.input.ImpactParser;
import aconex.input.Parser;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static aconex.TwoAxleVehicleSample.Direction.NORTH_BOUND;
import static aconex.TwoAxleVehicleSample.Direction.SOUTH_BOUND;
import static java.lang.System.exit;

public class DisplayQuery {

    private static final int HOURS_IN_SESSION = 24 * 5;
    private static final int NUM_30MIN_IN_SESSION = HOURS_IN_SESSION * 2;
    private static final int NUM_20MIN_IN_SESSION = HOURS_IN_SESSION * 3;
    private static final int NUM_15MIN_IN_SESSION = HOURS_IN_SESSION * 4;

    /**
     * Prints peak Volume times for the whole session.
     */
    public static void printPeakVolumeTime(VehicleSurveyor vehicleSurveyor) {
        // intervalMs is a hours worth of ms for the threshold check.
        long intervalMs = TimeUnit.HOURS.toMillis(1);

        System.out.println("\nPeak time report:");
        for (int i = 0; i < 5; i++ ) {
            Optional<String> peakHours = vehicleSurveyor.getPeakHoursOfDay(i, intervalMs).entrySet().stream()
                    .max((o1, o2) -> o1.getValue() - o2.getValue())
                    .map(es -> {
                        return es.getValue() + " vehicles at " + es.getKey() + "th hour.";
                    });

            System.out.println("\t ** Peak - day " + (i + 1) + " ** " + peakHours);
        }
    }

    public static void printReport(VehicleSurveyor vehicleSurveyor) {
        System.out.println("\nTotal vehicles direction:");

        System.out.println("\t ** A (session) ** ");
        printDirectionStats(vehicleSurveyor.getSessionSamples(NORTH_BOUND));

        System.out.println("\t ** B (session) ** ");
        printDirectionStats(vehicleSurveyor.getSessionSamples(SOUTH_BOUND));

        System.out.println("\nTotal vehicles direction (DAYS):");
        for (int i = 0; i < 5; i++ ) {
            System.out.println("\t ** A day " + (i + 1) + " ** ");
            printDirectionStats(vehicleSurveyor.getDaySamples(i, NORTH_BOUND));
        }
        for (int i = 0; i < 5; i++ ) {
            System.out.println("\t ** B day " + (i + 1) + " ** ");
            printDirectionStats(vehicleSurveyor.getDaySamples(i, SOUTH_BOUND));
        }

        printPeakVolumeTime(vehicleSurveyor);

        final List<TwoAxleVehicleSample> flatSessionSamples = vehicleSurveyor.getSessionSamples();
        System.out.println("Average speed distribution of traffic (km/h): " + vehicleSurveyor.speedDistributionOfTraffic(flatSessionSamples));
        System.out.println("Rough distance between vehicles (meters): " + vehicleSurveyor.roughDistanceBetweenCars(flatSessionSamples));
    }

    private static void printDirectionStats(List<TwoAxleVehicleSample> samples) {
        System.out.println("\t\t- total: " + samples.size());
        System.out.println("\t\t- morning vs evening: " + samples.stream().filter(TwoAxleVehicleSample::isMorning).count() + " vs "
                                                         + samples.stream().filter(TwoAxleVehicleSample::isEvening).count());
        System.out.println("\t\t- per hr: " + (double)samples.size() / HOURS_IN_SESSION);
        System.out.println("\t\t- per 1/2hr: " + (double)samples.size() / NUM_30MIN_IN_SESSION);
        System.out.println("\t\t- per 20mins " + (double)samples.size() / NUM_20MIN_IN_SESSION);
        System.out.println("\t\t- per 15mins " + (double)samples.size() / NUM_15MIN_IN_SESSION);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Missing input arg: input data file.");
            exit(1);
        }

        final Parser parser = new ImpactParser();
        final VehicleSurveyor vehicleSurveyor = new VehicleSurveyor(parser);

        vehicleSurveyor.readData(args[0]);

        printReport(vehicleSurveyor);
    }

}
