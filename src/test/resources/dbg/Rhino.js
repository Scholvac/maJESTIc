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
	var a = 2;
	print("SubFunction1: " + a);
	return true;
}
function subFunction2() {	
	print("SubFunction2");
	return false; 
}