package utils;
import java.util.Random;

public class Arrays {

	public static void swap(int[] a, int i, int j) {
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	public static int[] shuffle(int[] a) {
		for (int i = 0; i < a.length; i++) {
			int r = Math.abs(new Random().nextInt());
			swap(a, i, i + r % (a.length - i));
		}
		return a;
	}

	public static int[] copy(int[] a) {
		int[] b = new int[a.length];
		for (int i = 0; i < b.length; i++)
			b[i] = a[i];
		return b;
	}

	public static String toString(int[] a) {
		String s = "";
		if (a.length == 0)
			s += "empty";
		else {
			s += "{ " + a[0];
			for (int i = 1; i < a.length; i++)
				s += ", " + a[i];
			s += " }";
		}
		return s;
	}

	public static String toString(int[][] a) {
		String s = "";
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++)
				s += a[i][j] + " ";
			s += "\n";
		}
		return s;
	}

	public static void main(String[] args) {
		int[] a = new int[] { 1, 2, 3, 4, 5 };
		System.out.println(toString(a));
		System.out.println(toString(shuffle(a)));
	}
}
