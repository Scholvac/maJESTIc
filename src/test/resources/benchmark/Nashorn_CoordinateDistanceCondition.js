
function eval(origin) {
	var CoordinateType = Java.type("demo.de.sos.script.nativeclasses.Coordinate")
	test = new CoordinateType(0,0,0)
	dist = test.getDistance(origin)
	return dist.value < 10
}
