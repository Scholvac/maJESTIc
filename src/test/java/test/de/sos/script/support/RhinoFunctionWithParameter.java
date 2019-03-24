package test.de.sos.script.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.INamedElement;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;
import de.sos.script.ast.lang.java.JarManager;
import de.sos.script.ast.util.Scope;



public class RhinoFunctionWithParameter {

	public static void main(String[] args) throws Exception {
		RhinoFunctionWithParameter rfnp = new RhinoFunctionWithParameter();
		rfnp.test_convert_benchmark_JS();
	}



	void setup() {
	
	}
	
	@Test
	public void test_convert_benchmark_JS() throws IOException {
		setup();
		File f = new File("src/test/resources/benchmark/Rhino_CoordinateDistanceCondition.js");
		IScript script = IScriptManager.loadScript(f);
		CompilationUnit rootNode = script.getCompilationUnit();

		Scope scope = rootNode.getScopeForIndex(184);
		System.out.println(scope);
		
		INamedElement test_ne = scope.getNamedElement("test");//should be of type Coordinate
		assertNotNull(test_ne);
		assertTrue(test_ne instanceof ITypeResolver);
		IType type = ((ITypeResolver)test_ne).getType(scope);
		System.out.println(type);
		assertEquals("Coordinate", type.getName());
		
	}
	
	


}
