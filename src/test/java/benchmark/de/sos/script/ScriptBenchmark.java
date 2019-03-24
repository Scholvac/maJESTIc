package benchmark.de.sos.script;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import de.sos.script.EntryPoint;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import demo.de.sos.script.nativeclasses.Coordinate;
import demo.de.sos.script.nativeclasses.Distance;


@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ScriptBenchmark {
	
	public static void main(String[] args) throws RunnerException, IOException {
		MyState s = new MyState();
		s.doSetup();
//		
		ScriptBenchmark sb = new ScriptBenchmark();
//		sb.run_Jython(s);
		sb.run_Rhino(s);
//		sb.run_Nashorn(s);
//		
//		org.openjdk.jmh.Main.main(args);
		
		 Options opt = new OptionsBuilder()
	                .include(ScriptBenchmark.class.getSimpleName())
	                .forks(1)
	                .warmupIterations(1)
	                .measurementIterations(1)
	                .addProfiler(StackProfiler.class)
//	                .mode(Mode.Throughput)
//	                .measurementIterations(5)
//	                .warmupIterations(5)
//	                .addProfiler(profiler)
	                .build();

		Runner runner = new Runner(opt);
		runner.run();

	}
	
	
	@State(Scope.Thread)
    public static class MyState {
		public static final Coordinate origin = new Coordinate(1, 1, 1);
		public static final File			nashorn_file = new File("src/test/resources/benchmark/Nashorn_CoordinateDistanceCondition.js");
		public static final File			jython_file = new File("src/test/resources/benchmark/CoordinateDistanceCondition.py");
		public static final File			rhino_file = new File("src/test/resources/benchmark/Rhino_CoordinateDistanceCondition.js");
		
		
		PythonInterpreter 					jythonInterpreter = null;
		PyObject 							jython_function = null;
		boolean 							jython_result = false;
		
		String								rhino_source;
		boolean								rhino_result = false;
		Context 							rhino_cx;
		Scriptable 							rhino_scope;
		Function 							rhino_function;
		
		boolean 							java_result = false;
		
		IScript								rhino_script;
		Boolean 							rhino_script_result;
		
        @Setup(Level.Trial)
        public void doSetup() {
        	try {
	        	System.out.println("Do Setup");
	        	
	    		rhino_source = new String(Files.readAllBytes(rhino_file.toPath()));
	    		rhino_cx = Context.enter();
	    		rhino_cx.setGeneratingSource(false);
	    		rhino_cx.setOptimizationLevel(9);
	    		
	    		rhino_scope = rhino_cx.initStandardObjects();
	    		rhino_function = rhino_cx.compileFunction(rhino_scope, rhino_source, rhino_file.getName(), 1, null);
	    		Object r_result = rhino_function.call(rhino_cx, rhino_scope, rhino_scope, new Object[] {origin});
	        	
	        	
	        	
	        	String 	jython_source = new String(Files.readAllBytes(jython_file.toPath()));	        	
	        	jythonInterpreter = new PythonInterpreter(null, new PySystemState());
	    		PyCode pyCode = jythonInterpreter.compile(jython_source);	    		
	    		PyObject eval = jythonInterpreter.eval(pyCode);
	    		jython_function = jythonInterpreter.get("eval");
	    		
	    		    		
	    		//run both for initialisation purpose (compiling / eval)
	    		PyObject param = Py.java2py(origin);
	    		PyObject res = jython_function.__call__(param);
	    		
	    		File file = new File("src/test/resources/benchmark/Rhino_CoordinateDistanceCondition.js");
	    		rhino_script = IScriptManager.loadScript(file);
	    		rhino_script.setEntryPoint(new EntryPoint("eval", EntryPoint.var().add("origin", origin).get(), null));
	    		
	        	System.out.println("Finish - Setup");
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            System.out.println("Do TearDown");
        }
    }

	
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void run_Jython(MyState state) {
		PyObject param = Py.java2py(state.origin);
		PyObject res = state.jython_function.__call__(param);
		state.jython_result = Py.py2boolean(res);
    }
	
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void run_Java(MyState state) {
		state.java_result = eval(state.origin);
    }
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void run_Rhino(MyState state) {
		Object r_result = state.rhino_function.call(state.rhino_cx, state.rhino_scope, state.rhino_scope, new Object[] {MyState.origin});
		state.rhino_result = (Boolean)r_result;
    }
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void run_RhinoScript(MyState state) {
		state.rhino_script_result = (Boolean) state.rhino_script.execute().getResult();
    }
	
	public static boolean eval(Coordinate origin) {
		final Coordinate test = new Coordinate(0,0,0);
		final Distance dist = test.getDistance(origin);
		return dist.getValue() < 10;
	}
}
