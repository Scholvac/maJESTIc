function eval(origin) {
	var CoordinateType = Packages.demo.de.sos.script.nativeclasses.Coordinate;	
	var test = new CoordinateType(0.0,0.0,0.0);	
	var dist = test.getDistance(origin);	
	return dist.value < 10
}
