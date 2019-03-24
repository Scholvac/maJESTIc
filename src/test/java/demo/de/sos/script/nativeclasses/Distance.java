package demo.de.sos.script.nativeclasses;

/** Class containing a distance value and the used unit */
public class Distance {
		
	/* the unit of the distance */
	private DistanceUnit	unit;
	// quantity of the distance
	private double			value;
	
	/**
	 * Main - Constructor
	 * @param value
	 * @param unit
	 */
	public Distance(final double value, final DistanceUnit unit) {
		this.value = value; this.unit = unit;
	}
	
	/** returns the unit of this distance */
	public DistanceUnit getUnit() { return unit; }
	/** returns the quantity of this distance */
	public double getValue() { return value; }
	
	/** performs a conversion into another unit and returns the distance of this 
	 * with the given unit
	 * @param unit unit to express the distance in
	 * @return quantity of this distance with given unit
	 */
	public double getAs(DistanceUnit unit) {
		if (this.unit == unit) return value;
		switch(this.unit) {
		case METER:
			switch(unit) {
			case METER: return value;
			case KILOMETER : return value / 1000.0;
			case CENTIMETER : return value * 100;
			}
		case KILOMETER:
			switch(unit) {
			case METER: return value / 1000.0;
			case KILOMETER : return value;
			case CENTIMETER : return value * 100 * 1000;
		}
		case CENTIMETER: 
			switch(unit) {
			case METER: return value / 100.;
			case KILOMETER : return value / (100* 1000.0);
			case CENTIMETER : return value;
			}
		}
		throw new IllegalArgumentException();
	}
}