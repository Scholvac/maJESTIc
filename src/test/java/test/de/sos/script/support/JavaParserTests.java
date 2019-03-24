package test.de.sos.script.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;

public class JavaParserTests {

	public static void main(String[] args) throws FileNotFoundException {
		CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(new File("c:\\workspace\\CoordinateImpl.java")));
		PackageDeclaration pdecl = cu.getPackageDeclaration().get();
		System.out.println(pdecl);
	}
	@Test
	public void createCU() throws FileNotFoundException {
		CompilationUnit cu = new CompilationUnit();
		ClassOrInterfaceDeclaration myClass = cu.addClass("MyClass");
		myClass.addField(double.class, "foobar", Keyword.PUBLIC, Keyword.STATIC);
		myClass.addField("myTypeSomeWhat", "MyOtherName");
		MethodDeclaration mdec = myClass.addMethod("myMethod", Keyword.PUBLIC);
		
		mdec.addParameter("double", "myDoubleParam");
		mdec.setType(double.class);
		BlockStmt body = new BlockStmt();
		body.addStatement(new ReturnStmt("3.0"));
		mdec.setBody(body);
		
		
		
		FieldDeclaration fbn = myClass.getFieldByName("MyOtherName").get();
		VariableDeclarator varDecl = fbn.getVariable(0);
		Type type = varDecl.getType();
		
		
		System.out.println(cu);
	}
}
