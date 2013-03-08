package utils;

public class Point {

	public double x, y;

	public Point(double _x, double _y) {
		this.x = _x;
		this.y = _y;
	}	

	public String toString() {
		return x + "," + y;
	}

	public double sqrDist(Point pos) {
		return (pos.x - x) * (pos.x - x) + (pos.y - y) * (pos.y - y);
	}
}
