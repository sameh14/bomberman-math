package utils;

public class Values {

	public static boolean isBounded(double v, double l, double r) {
		return v >= l && v <= r;
	}

	public static double bound(double v, double l, double r) {
		return (v < l) ? l : ((v > r) ? r : v);
	}

	public static int floor(double v, int r) {
		return (int) (v / r) * r;
	}

	public static int round(double v, int r) {
		return (int) ((v + r / 2) / r) * r;
	}

	public static int ceil(double v, int r) {
		return (int) ((v + r) / r) * r;
	}

	public static int sign(double v) {
		return (v > 0) ? 1 : ((v < 0) ? -1 : 0);
	}
}
