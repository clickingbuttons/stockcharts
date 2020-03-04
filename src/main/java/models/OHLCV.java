package models;

import java.time.Instant;

public class OHLCV implements Comparable<OHLCV> {
    public double open = 0;
    public double high = 0;
    public double low = 0;
    public double close = 0;
    public long volume = 0;
    public long timeMicros;

    public OHLCV(long t) {
        timeMicros = t;
    }

    public String toString() {
        return String.format("%d (%s) o: %4.3f h: %4.3f l: %4.3f c: %4.3f v: %d",
                timeMicros,
                Instant.ofEpochMilli(timeMicros / 1000).toString(),
                open,
                high,
                low,
                close,
                volume);
    }

    @Override
    public int compareTo(OHLCV o2) {
        // Can't return 0 if equal because used in Set
        if (o2.timeMicros >= timeMicros) {
            return -1;
        }
        return 1;
    }
}
