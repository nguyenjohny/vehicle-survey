package aconex.input;

import java.util.Objects;

public class PneumaticImpact {
    private final String id;
    private final long millisOnDay;

    public PneumaticImpact(String id, long millisOnDay) {
        this.id = id;
        this.millisOnDay = millisOnDay;
    }

    public String getId() {
        return id;
    }

    public long getMillisOnDay() {
        return millisOnDay;
    }

    @Override
    public String toString() {
        return "PnuematicHose{" +
                "id='" + id + '\'' +
                ", millisOnDay=" + millisOnDay +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PneumaticImpact)) return false;
        PneumaticImpact pneumaticImpact = (PneumaticImpact) o;
        return millisOnDay == pneumaticImpact.millisOnDay &&
                Objects.equals(id, pneumaticImpact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, millisOnDay);
    }
}
