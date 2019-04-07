package test.de.sos.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EngineTest {

	
	public static void main(String[] args) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine rhino = sem.getEngineByName("rhino");
		ScriptEngine nashorn = sem.getEngineByName("nashorn");
		ScriptEngine jython = sem.getEngineByName("jython");
		ScriptEngine python = sem.getEngineByName("python");
		System.out.println();
	}
}
