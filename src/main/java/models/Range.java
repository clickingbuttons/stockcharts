package models;

public class Range<T extends Number> {
    public T min;
    public T max;

    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public void setMin(T newMin) {
        this.min = newMin;
    }

    public void setMax(T newMax) {
        this.max = newMax;
    }

    public double getRange() {
        return this.max.doubleValue() - this.min.doubleValue();
    }
}
