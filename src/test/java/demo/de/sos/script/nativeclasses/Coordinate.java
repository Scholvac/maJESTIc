package demo.de.sos.script.nativeclasses;


public class Coordinate {
	
	
	private double x;
	private double y;
	private double z;
	
	public Coordinate(final double x, final double y, final double z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	public int dimension() { return 3; }
	
	public double getX() { return x;}
	public double getY() { return y;}
	public double getZ() { return z;}
	
	public void setX(double xx) { x = xx;}
	public void setY(double yy) { y = yy;}
	public void setZ(double zz) { z = zz;}
	
	public Coordinate add(final Coordinate o) {
		return new Coordinate(x + o.x, y + o.y, z+o.z);
	}
	
	public Distance getDistance(final Coordinate o) {
		double dx = x - o.x, dy = y - o.y, dz = z-o.z;
		return new Distance(Math.sqrt(dx*dx+dy*dy+dz*dz), DistanceUnit.METER);
	}

	public void set(double xx, double yy, double zz) {
		x = xx; y = yy; z = zz;
	}
	
	public double get(final int idx) {
		switch(idx) {
		case 0: return x;
		case 1: return y;
		case 2: return z;
		}
		throw new IllegalArgumentException();
	}
}
