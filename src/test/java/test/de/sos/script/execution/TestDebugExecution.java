package test.de.sos.script.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.sos.script.EntryPoint;
import de.sos.script.ExecutionResult;
import de.sos.script.IEntryPoint;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.IScriptVariable;
import de.sos.script.ScriptManager;
import de.sos.script.ScriptVariable;
import de.sos.script.ScriptVariable.VariableDirection;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.dbg.BreakPoint;
import de.sos.script.run.dbg.DebugContext;
import de.sos.script.run.dbg.DebuggerCallback;
import de.sos.script.run.dbg.DebuggerCallback.NextAction;
import demo.de.sos.script.nativeclasses.Coordinate;

public class TestDebugExecution {

	
	public static void main(String[] args) {
		TestDebugExecution tre = new TestDebugExecution();
//		tre.test_js_benachmarkScript_STEP_OVER();
//		tre.test_js_benachmarkScript_STEP_INTO();
//		tre.test_js_benachmarkScript_STEP_OUT();
//		tre.test_js_benachmarkScript_STEP_CONTINUE();
		
//		tre.test_js_benachmarkScript_CHECK_21();
		tre.test_js_benachmarkScript_CHECK_13();
		
//		tre.test_py_benachmarkScript();
	}
	@Test
	public void test_js_benachmarkScript_STEP_OVER() {
		File file = new File("src/test/resources/dbg/Rhino.js");
		IScriptManager.theInstance.clearAllBreakPoints();
		IScriptManager.theInstance.addBreakPoint(file, 3); //src = 	var test = new FileType("HelloWojkhgrld.foo");
		doTheBenchmarkTest(file, 2, NextAction.STEP_OVER);
	}
	
	@Test
	public void test_js_benachmarkScript_STEP_INTO() {
		File file = new File("src/test/resources/dbg/Rhino.js");
		IScriptManager.theInstance.clearAllBreakPoints();
		IScriptManager.theInstance.addBreakPoint(file, 3); //src = 	var test = new FileType("HelloWojkhgrld.foo");
		doTheBenchmarkTest(file, 6, NextAction.STEP_INTO);
	}
	@Test
	public void test_js_benachmarkScript_STEP_OUT() {
		File file = new File("src/test/resources/dbg/Rhino.js");
		IScriptManager.theInstance.clearAllBreakPoints();
		IScriptManager.theInstance.addBreakPoint(file, 3); //src = 	var test = new FileType("HelloWojkhgrld.foo");
		doTheBenchmarkTest(file, 1, NextAction.STEP_OUT);
	}
	
	@Test
	public void test_js_benachmarkScript_STEP_CONTINUE() {
		File file = new File("src/test/resources/dbg/Rhino.js");
		IScriptManager.theInstance.clearAllBreakPoints();
		IScriptManager.theInstance.addBreakPoint(file, 12); //src = 	var test = new FileType("HelloWojkhgrld.foo");
		doTheBenchmarkTest(file, 1, NextAction.CONTINUE);
	}
	
	

	
	
//	@Test
//	public void test_py_benachmarkScript() {
//		File file = new File("src/test/resources/benchmark/CoordinateDistanceCondition.py");
//		IScriptManager.theInstance.clearAllBreakPoints();
//		IScriptManager.theInstance.addBreakPoint(file, 5); //src =     dist = test.getDistance(origin, None) 
//		doTheBenchmarkTest(file, 0, NextAction.STEP_OVER);
//	}
	
	
	private void doTheBenchmarkTest(final File file, int expectedSteps, final NextAction nextAction) {
		final int[] counter = new int[] {0};
		DebuggerCallback dbgc = new DebuggerCallback() {
			@Override
			public NextAction interrupt(DebugContext context, BreakPoint bp) {
				System.out.println("Interrupted on: " + bp);
				counter[0] = counter[0] + 1;
				return nextAction;
			}
		};
		IScript script = IScriptManager.loadScript(file);
		assertNotNull(script);
		IEntryPoint ep = new EntryPoint("eval", EntryPoint.var().add("origin", new Coordinate(10, 10, 10)).get(), null);
		IScriptDebugExecutor executor = IScriptManager.theInstance.createDebugExecutor(script);
		assertNotNull(executor);
		executor.setDebugCallback(dbgc);
		
		ExecutionResult result = executor.executeScript(ep);
		System.out.println(result);
		
		assertEquals(expectedSteps, counter[0]);
		
	}
	
	
	
	
	@Test
	public void test_js_benachmarkScript_CHECK_21() {
		File file = new File("src/test/resources/dbg/Rhino.js");
		IScriptManager.theInstance.clearAllBreakPoints();
		IScriptManager.theInstance.addBreakPoint(file, 22); //src = 	var test = new FileType("HelloWojkhgrld.foo");
		ScriptVariable v_a = new ScriptVariable("a", 2, Number.class, VariableDirection.INOUT);
		doCheckVariableAccess(file, Arrays.asList(v_a), NextAction.CONTINUE);
	}
	
	@Test
	public void test_js_benachmarkScript_CHECK_13() {
		File file = new File("src/test/resources/dbg/Rhino.js");
		IScriptManager.theInstance.clearAllBreakPoints();
		IScriptManager.theInstance.addBreakPoint(file, 14); //src = 	var test = new FileType("HelloWojkhgrld.foo");
		ScriptVariable v_a = new ScriptVariable("test", File.class);
		doCheckVariableAccess(file, Arrays.asList(v_a), NextAction.CONTINUE);
	}
	
	private void doCheckVariableAccess(final File file, List<ScriptVariable> list, final NextAction nextAction) {
		final int[] counter = new int[] {0};
		DebuggerCallback dbgc = new DebuggerCallback() {
			@Override
			public NextAction interrupt(DebugContext context, BreakPoint bp) {
				System.out.println("Interrupted on: " + bp);
				List<ScriptVariable> variables = context.getAccessableVariables();
				for (int i = 0; i < list.size(); i++) {
					String n = list.get(i).getName();
					boolean found = false;
					for (int j = 0; j < variables.size(); j++) {
						IScriptVariable sv = variables.get(j);
						if (sv.getName().equals(n)) {
							found = true; break;
						}
					}
					assertTrue(found);
				}
				counter[0] = counter[0] + 1;
				return nextAction;
			}
		};
		IScript script = IScriptManager.loadScript(file);
		assertNotNull(script);
		IEntryPoint ep = new EntryPoint("eval", EntryPoint.var().add("origin", new Coordinate(10, 10, 10)).get(), null);
		IScriptDebugExecutor executor = IScriptManager.theInstance.createDebugExecutor(script);
		assertNotNull(executor);
		executor.setDebugCallback(dbgc);
		
		ExecutionResult result = executor.executeScript(ep);		
	}

}
