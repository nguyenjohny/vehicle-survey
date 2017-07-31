package aconex;

import java.util.concurrent.TimeUnit;

class TwoAxleVehicleSample {

    private static final double METERS_BETWEEN_AXLES = 2.5;

    enum Direction { NORTH_BOUND, SOUTH_BOUND }

    private final Direction direction;
    private final Long startMs;
    private final Long endMs;

    TwoAxleVehicleSample(Direction direction, Long startMs, Long endMs) {
        this.direction = direction;
        this.startMs = startMs;
        this.endMs = endMs;
    }

    public Direction getDirection() {
        return direction;
    }

    public Long getStartMs() {
        return startMs;
    }

    public Long getEndMs() {
        return endMs;
    }

    public boolean isMorning() {
        return getStartMs() < TimeUnit.HOURS.toMillis(12);
    }

    public boolean isEvening() {
       return !isMorning();
    }

    /**
     * @return speed in km/h
     */
    public double calculateSpeed() {
        double metersPerHour = (METERS_BETWEEN_AXLES / (endMs - startMs)) * TimeUnit.HOURS.toMillis(1);
        return metersPerHour / 1000;
    }
}
