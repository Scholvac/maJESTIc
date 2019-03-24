
function main(){
	var intVar = 42;
	var doubleVar = 42.42;
	StringVar = "Hello World";
	if (intVar < 15){
		print("SmallValue");
	}else{
		var CoordType = Packages.demo.de.sos.script.nativeclasses.Coordinate;
		var coord = new CoordType(doubleVar, 3, 1);
		var value = eval(coord);
		if (value) {
			print("It-Is");
		}else
			log("It-Is-NOT");
	}
	warn("Finish Main");
}

function eval(origin) {
	var FileType = Packages.java.io.File;
	var test = new FileType("HelloWojkhgrld.foo");
	if (false == test.exists()){
		return subFunction1();
	}else{
		return subFunction2();
	}
}
function subFunction1() {
	var a = 42;
	print("SubFunction 1: " + a);
	return true;
}
function subFunction2() {	
	print("SubFunction 2");
	return false; 
}