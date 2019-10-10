package org.neo4j.spatial.algo.wgs84;

import org.neo4j.spatial.core.CRS;
import org.neo4j.spatial.core.LineSegment;
import org.neo4j.spatial.core.Point;
import org.neo4j.spatial.core.Polygon;

import java.util.Arrays;

public class WGS84Within {
    public static boolean within(Polygon polygon, Point point) {
        long withinShells = Arrays.stream(polygon.getShells()).filter(s -> within(s, point)).count();
        long withinHoles = Arrays.stream(polygon.getHoles()).filter(h -> within(h, point)).count();
        return withinShells > withinHoles;
    }

    public static boolean within(Polygon.SimplePolygon polygon, Point point) {
        Point[] points = polygon.getPoints();

        double courseDelta = WGSUtil.courseDelta(polygon.getPoints());

        if (courseDelta > 270 || courseDelta < -270) {
            //The polygon does not contain a pole
            boolean result = false;
            for (int i = 0; i < points.length - 1; i++) {
                Point a = points[i];
                Point b = points[i+1];

                if (WGSUtil.intersect(LineSegment.lineSegment(a, b), LineSegment.lineSegment(point, Point.point(CRS.WGS84, point.getCoordinate()[0], 90))) != null) {
                    result = !result;
                }
            }
            return result;
        } else {
            //The polygon contains at least one pole
            throw new IllegalArgumentException("Polygon contains at least one pole");
        }
    }
}
