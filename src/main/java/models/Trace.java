package models;

import java.awt.*;
import java.awt.geom.Point2D;

public class Trace {
    public Point start;
    public Point2D.Double startValue;
    public Point end;
    public Point2D.Double endValue;

    public Trace(Point start, Point2D.Double startValue) {
        this.start = start;
        this.end = start;
        this.startValue = startValue;
        this.endValue = startValue;
    }
}
