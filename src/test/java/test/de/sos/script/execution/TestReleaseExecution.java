package test.de.sos.script.execution;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.sos.script.EntryPoint;
import de.sos.script.ExecutionResult;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.ast.lang.java.JarManager;
import de.sos.script.run.IScriptExecuter;
import demo.de.sos.script.nativeclasses.Coordinate;

public class TestReleaseExecution {

	
	public static void main(String[] args) {
		TestReleaseExecution tre = new TestReleaseExecution();
		tre.test_js_benachmarkScript();
//		tre.test_py_benachmarkScript();
	}
	
	
	
	
	@Test
	public void test_js_benachmarkScript() {
		File file = new File("src/test/resources/benchmark/Rhino_CoordinateDistanceCondition.js");
		doTheTest(file);
	}
//	@Test
//	public void test_py_benachmarkScript() {
//		File file = new File("src/test/resources/benchmark/CoordinateDistanceCondition.py");
//		doTheTest(file);
//	}
	
	public void doTheTest(final File file) {
		assertTrue(file.exists());
		IScript script = IScriptManager.loadScript(file);
		assertNotNull(script);
		EntryPoint ep = new EntryPoint("eval", EntryPoint.var().add("origin", new Coordinate(10, 10, 10)).get(), null);
		IScriptExecuter executor = IScriptManager.theInstance.createExecutor(script);
		assertNotNull(executor);
		
		ExecutionResult result = executor.executeScript(ep);
		assertNotNull(result);
		assertNull(result.getError());
		assertTrue(result.getResult() instanceof Boolean);
		assertFalse((Boolean)result.getResult());
		
		//execute the same script but with other input, again
		Coordinate c = ep.getArgumentValue("origin");
		c.set(1, 1, 1);
		result = executor.executeScript(ep);
		assertNotNull(result);
		assertNull(result.getError());
		assertTrue(result.getResult() instanceof Boolean);
		assertTrue((Boolean)result.getResult()); //here is the change (false->true)
	}

}
