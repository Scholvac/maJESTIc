package test.de.sos.script.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.List;

import org.junit.Test;

import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.ScriptSource;
import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.INamedElement;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;
import de.sos.script.ast.lang.java.JarManager;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.ScriptCompletionProvider;
import de.sos.script.support.completions.ICompletion;



public class RhinoFunctionNoParameter {

	public static void main(String[] args) throws Exception {
		RhinoFunctionNoParameter rfnp = new RhinoFunctionNoParameter();
		rfnp.test_completions();
	}

	
	public void setup() {
		JarManager.get().addSourceDirectory(new File("src/test/java"));
	}
	
	@Test
	public void test_completions() throws IOException {
		setup();
		File f = new File("src/test/resources/completion/RhinoFunctionNoParameter.js");
		IScript script = IScriptManager.loadScript(f);
		CompilationUnit rootNode = script.getCompilationUnit();
		System.out.println(rootNode.debugPrint());
		
		int pos = 325;
		
		ScriptCompletionProvider cp = new ScriptCompletionProvider();
		
		String[] input = new String[] {
		/*0*/	"",															
		/*1*/	"myC",														
		/*2*/	"myCoord",													 
		/*3*/	"myCoord.",													
		/*4*/	"myCoord.get",												
		/*5*/	"myCoord.getDistance(",										 
		/*6*/	"myCoord.getDistance(myOtherCoord.",						 
		/*7*/	"myCoord.getDistance(myOtherCoord.a",						 
		/*7*/	"myCoord.getDistance(myOtherCoord.get(crs)",				
		/*8*/	"myCoord.getDistance(myOtherCoord.get(crs), ",				
		/*10*/	"myCoord.getDistance(myOtherCoord.get(crs), ds",			
		/*11*/	"myCoord.getDistance(myOtherCoord.get(crs), ds.get(",		
				
		/*12*/	"myCoord.getDistance(myOtherCoord.get(crs)).",		
		/*13*/	"myCoord.getDistance(myOtherCoord.get(crs)).get",		

		/*14*/	"myCoord = ",												
		/*15*/	"myCoord = myOtherCoord",
		};
		
		String[] [] expected = new String[][] {
		/*0*/	{"myCoord", "FileType", "file", "myCoord"},
		/*1*/	{"myCoord"},
		/*2*/	{"."},
		/*3*/	{"get(int)","getDistance(Coordinate)","getX()","getY()","getZ()","set(double, double, double)","setX(double)","setY(double)","setZ(double)","x","y","z"},
		/*4*/	{"get(int)","getDistance(Coordinate)","getX()","getY()","getZ()"},
		/*5*/	{"myCoord", "myOtherCoord"},
		/*6*/	{"add(Coordinate)"},
		/*7*/	{"add(Coordinate)"},
		/*8*/	{}, //")"
		/*9*/	{"myCoord", "FileType", "file", "myCoord"},
		/*10*/	{},
		/*11*/	{"myCoord", "FileType", "file", "myCoord"},
		/*12*/	{"getAs(DistanceUnit)","getUnit()","getValue()","unit","value"},
		/*13*/	{"getAs(DistanceUnit)", "getUnit()", "getValue()"},
		/*14*/	{"myCoord", "FileType", "file", "myCoord"},
		/*15*/	{"."}
		};
		
		assertEquals(input.length, expected.length);
		
		for (int i = 0; i < input.length; i++) {
			String str = input[i];
			System.out.println(i + "[" + str + "]");
			Object[] toTest = modifyScript(script, pos, str);
			IScript nScript = (IScript)toTest[0];
			int nPos = (Integer)toTest[1];
			List<ICompletion> completions = cp.getCompletions(nScript, nPos, nScript.getContent().substring(0, nPos));
			assertContains(str, completions, expected[i]);
		}
	}
	
	
	private Object[] modifyScript(IScript script, int pos, String string) {
		String content = script.getContent();
		String content1 = content.substring(0, pos);
		String content2 = content.substring(pos+1, content.length());
		content = content1 + string + content2;
		return new Object[] {script.getManager().loadScript(new ScriptSource.StringSource("test", content, script.getLanguage())), pos + string.length()};
	}


	
	public static void assertContains(final String input, final List<ICompletion> completions, final String... expected) {
		if (expected.length == 0)
			return ;
		for (String exp : expected)
			assertContains(input, completions, exp);
	}
	public static void assertContains(final String input, final List<ICompletion> completions, final String expected) {
		for (ICompletion c : completions) {
			final String cstr = c.getFeatureString();
			if (cstr.equals(expected))
				return ;
		}
		System.out.println(toString(completions));
		fail("Missing completion String: [" + expected + "] for input: [" + input + "] List: " + toString(completions));
	}
	
	

	private static String toString(List<ICompletion> completions) {
		String comp = ""; 
		for (ICompletion c : completions) 
			comp += "\"" + c.getFeatureString()+"\",";
		if (comp.length() < 2)
			return comp;
		comp = comp.substring(0, comp.length()-2);
		return comp;
	}

}
