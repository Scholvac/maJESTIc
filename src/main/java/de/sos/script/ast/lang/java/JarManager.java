package de.sos.script.ast.lang.java;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.checkerframework.checker.units.qual.s;

import de.sos.script.ast.IType;
import de.sos.script.impl.QualifiedName;



public class JarManager {

	public static void main(String arg[]) throws IOException {
		JarManager jmgr = new JarManager();
		jmgr.addJREClasspath();
		JavaType cu = jmgr.getType("java.io.File");
		System.out.println(cu);
	}
	
	
	
	
	
	private static JarManager theInstance;
	public static JarManager get() {
		if (theInstance == null)
			theInstance = init();
		return theInstance;
	}
	
	private static JarManager init() {
		JarManager jmgr = new JarManager();
		jmgr.addJREClasspath();
		return jmgr;
	}
	
	
	
	

	private Map<String, Package> 		mPackages = new HashMap<>();
	private Set<URL>					mLoadedURLS = new HashSet<>();
		
	private JarManager() {
	}
	
	public void addJREClasspath() {
		File jreHome = new File(System.getProperty("java.home"));
		File mainJar = new File(jreHome, "lib/rt.jar"); // Sun JRE's till 1.8
		if (mainJar.exists()) {
			loadJarFile(mainJar);
		}else {
			
		}
	}

	public void addSourceDirectory(File file) {
		addSourceDirectory(file, "");
	}
	private void addSourceDirectory(File file, String fqn) {
		if (!file.exists())
			return ;
		File[] subFiles = file.listFiles();
		for (File sub : subFiles) {
			if (sub.isDirectory()) {
				addSourceDirectory(sub, fqn+sub.getName()+".");
			}else if (sub.getName().endsWith(".java")) {
				String name = sub.getName().substring(0, sub.getName().length()-5); //remove ".java"
				try {
					addSourceClass(QualifiedName.createWithRegEx(fqn+name, "\\."), sub.toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	

	public void loadJarFile(File jarFile) {
		JarFile jar = null;
		try {
			URL jarURL = jarFile.toURI().toURL();
			if (mLoadedURLS.contains(jarURL))
				return ;
			
			
			jar = new JarFile(jarFile);
			Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				
				String entryName = entry.getName();
				if (entryName.endsWith(".class") && entryName.contains("$") == false) {
					
					QualifiedName fqn = QualifiedName.createWithRegEx(entryName.substring(0, entryName.length()-6), "/");
					addBinaryClass(fqn);
				}else if (entryName.endsWith(".java") && entryName.contains("$") == false) {
					URL url = new URL("jar:" + jarURL + "!" + entryName);
					QualifiedName fqn = QualifiedName.createWithRegEx(entryName.substring(0, entryName.length()-6), "/");
					addSourceClass(fqn, url);
				}
			}
			
			mLoadedURLS.add(jarURL);
			
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			if (jar != null)
				try {
					jar.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}	
	}


	public void addBinaryClass(QualifiedName fqn) {
		String cn = fqn.lastSegment();
		Package p = getPackage(fqn.removeSegmentsFromEnd(1));
		if (p == null) 
			return ; //TODO: for now maybe we add the default package?
		p.addProxyType(cn, null);
	}


	public void addSourceClass(QualifiedName fqn, URL sourceURL) {
		String cn = fqn.lastSegment();
		Package p = getPackage(fqn.removeSegmentsFromEnd(1));
		if (p == null) 
			return ; //TODO: for now maybe we add the default package?
		p.addProxyType(cn, sourceURL);		
	}
	
	
	
	public Package getPackage(String fqn) {
		QualifiedName qn = QualifiedName.createWithRegEx(fqn, "\\.");
		return getPackage(qn);
	}


	public Package getPackage(QualifiedName qn) {
		if (qn == null || qn.numSegments() == 0) 
			return null;
		String n = qn.firstSegment();
		qn = qn.removeSegmentsFromStart(1);
		Package p = _getPackage(n, null);
		while(qn.numSegments() > 0) {
			p = p.getOrCreateSubPackage(qn.firstSegment());
			qn.localRemoveSegmentsFromStart(1);
		}
		return p;
	}
	
	private Package _getPackage(String n, Package parent) {
		if (parent == null) {
			Package p = mPackages.get(n);
			if (p == null) {
				mPackages.put(n, p = new Package(n, null));
			}
			return p;
		}
		return parent.getOrCreateSubPackage(n);
	}
	
	
	
	public JavaType getType(String fqn) {
		return getType(QualifiedName.createWithRegEx(fqn, "\\."));
	}


	public JavaType getType(QualifiedName fqn) {
		String className = fqn.lastSegment();
		Package p = getPackage(fqn.removeSegmentsFromEnd(1));
		if (p == null)
			return null;
		return p.getType(className);
	}
	
	public Collection<Package> getAllPackages() {
		return Collections.unmodifiableCollection(mPackages.values());
	}
	
	public IType searchSimpleName(String className) {
		for (Package p : mPackages.values()) {
			IType t = p.searchSimpleType(className);
			if (t != null)
				return t;
		}
		return null;
	}


}
