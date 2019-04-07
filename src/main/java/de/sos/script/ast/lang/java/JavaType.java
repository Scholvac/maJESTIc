package de.sos.script.ast.lang.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import de.sos.script.ast.ASTFuncDecl;
import de.sos.script.ast.ASTParamDecl;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;
import de.sos.script.ast.TypeManager;
import de.sos.script.impl.QualifiedName;
import de.sos.script.support.completions.ICompletion;
import de.sos.script.support.completions.JavaFieldCompletion;
import de.sos.script.support.completions.JavaMethodCompletion;

public class JavaType implements IType {

	private final CompilationUnit 		mCompilationUnit;
	private final String				mName;
	private ClassOrInterfaceDeclaration mClassifier;
	private Class<?> 					mClazz;
	private ArrayList<String> 			mTypeArguments;

	public JavaType(final String name, final CompilationUnit cu) {
		mCompilationUnit = cu;
		mName = name;
		if (mCompilationUnit != null) {
			Optional<ClassOrInterfaceDeclaration> pt = mCompilationUnit.getClassByName(name);
			if (pt.isPresent())
				mClassifier = pt.get();
			if (mClassifier == null) {
				pt = mCompilationUnit.getInterfaceByName(name);
				if (pt.isPresent())
					mClassifier = pt.get();
			}
		}
		TypeManager.get().registerType(this);
	}
	
	@Override
	public String getName() {
		return mName;
	}
	
	public void setArguments(ArrayList<String> typeArguments) {
		mTypeArguments = typeArguments;		
	}

	@Override
	public ITypeResolver getTypeResolver(String name, IType... parameterTypes) {
		if (mClassifier == null)
			return null;
		boolean isMethod = parameterTypes != null;
		if (!isMethod) {
			Optional<FieldDeclaration> field = mClassifier.getFieldByName(name);
			if (field.isPresent()) {
				FieldDeclaration fd = field.get();
				return TypeManager.get().getType(getFullQualifiedType(fd.getElementType().asString()));
			}
		}
		List<MethodDeclaration> methods = mClassifier.getMethodsByName(name);
		return findMethod(methods, parameterTypes, 0);
	}
	
	private ITypeResolver findMethod(List<MethodDeclaration> methods, IType[] parameterTypes, int i) {
		int pc = parameterTypes !=  null ? parameterTypes.length : 0;
		List<MethodDeclaration> canidates = new ArrayList<>();
		for (MethodDeclaration md : methods) {
			if (md.getParameters().size() == pc)
				canidates.add(md);
		}
		if (canidates.size() == 1) {
			MethodDeclaration md = canidates.get(0);
			return TypeManager.get().getType(getFullQualifiedType(md.getTypeAsString()));
			
		}
		return null;		
	}

	private QualifiedName getFullQualifiedType(String typeAsString) {
		int idx = typeAsString.indexOf('.');
		if (idx > 0)
			return QualifiedName.createWithRegEx(typeAsString, "\\.");
		for (ImportDeclaration imp : mCompilationUnit.getImports()) {
			if (imp.getNameAsString().endsWith(typeAsString))
				return QualifiedName.createWithRegEx(imp.getNameAsString(), "\\.");
		}
		if (typeAsString.equals("double")) {
			return QualifiedName.createWithRegEx("java.lang.Double", "\\.");
		}else if (typeAsString.equals("float")) return QualifiedName.createWithRegEx("java.lang.Float", "\\.");
		else if (typeAsString.equals("int")) return QualifiedName.createWithRegEx("java.lang.Integer", "\\.");
		else if (typeAsString.equals("long")) return QualifiedName.createWithRegEx("java.lang.Long", "\\.");
		else if (typeAsString.equals("byte")) return QualifiedName.createWithRegEx("java.lang.Byte", "\\.");
		else if (typeAsString.equals("short")) return QualifiedName.createWithRegEx("java.lang.Short", "\\.");
		else if (typeAsString.equals("char")) return QualifiedName.createWithRegEx("java.lang.Character", "\\.");
		else if (typeAsString.equals("boolean")) return QualifiedName.createWithRegEx("java.lang.Boolean", "\\.");
		return QualifiedName.createWithRegEx(typeAsString, "\\.");
	}

	@Override
	public String toString() {
		return "JavaType {" + mName + "}";
	}

	@Override
	public String getFullQualifiedName() {
		if (mCompilationUnit != null) {
			Optional<PackageDeclaration> pd = mCompilationUnit.getPackageDeclaration();
			if (pd.isPresent())
				return pd.get().getNameAsString() + "." + mName;
		}
		return mName;
	}

	public String getDescription() {
		if (mClassifier != null) {
			Optional<Comment> comment = mClassifier.getComment();
			if (comment.isPresent()) {
				return comment.get().toString();
			}
		}
		return "No documentation available";
	}

	
	
	
	
	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		if (mClassifier != null) {
			for (FieldDeclaration fd : mClassifier.getFields()) {
				for (VariableDeclarator v : fd.getVariables()) {
					completions.add(new JavaFieldCompletion(v, fd, this));	
				}				
			}
			for (MethodDeclaration md : mClassifier.getMethods()) {
				JavaMethodCompletion jmc = new JavaMethodCompletion(md, this);
				String jmc_sig = jmc.getFeatureString();
				int jmc_hash = jmc_sig.hashCode();
				boolean found = false;
				for (ICompletion c : completions) {
					String c_sig = c.getFeatureString();
					int c_hash = c_sig.hashCode();
					if (c_hash == jmc_hash) {
						found = true;
						break;
					}
					if (c_sig.equals(jmc_sig)) {
						found = true;
						break;
					}
				}
//				if (!found)
					completions.add(jmc);
			}
			
			HashSet<ClassOrInterfaceType> allParents = new HashSet<>(mClassifier.getExtendedTypes());
			allParents.addAll(mClassifier.getImplementedTypes());
			
			for (ClassOrInterfaceType sup : allParents) {
				QualifiedName super_type_name = getFullQualifiedType(sup.getNameAsString());
				if (super_type_name != null) {
					IType super_type = TypeManager.get().getType(super_type_name);
					if (super_type != null)
						super_type.insertCompletions(completions);
				}
			}
		}
	}
	
	
	private IType getTypeForName(String name) {
		QualifiedName fqn = getFullQualifiedType(name);
		return TypeManager.get().getType(fqn);
	}
	
	
	private Collection<ClassOrInterfaceType> 	mAllParents;
	private List<ASTFuncDecl> 					mConstructors;
	private List<ASTFuncDecl> 					mMethods;
	
	public Collection<ClassOrInterfaceType> getAllParents(){
		if (mAllParents == null) {
			mAllParents = new HashSet<>(mClassifier.getExtendedTypes());
			mAllParents.addAll(mClassifier.getImplementedTypes());			
		}
		return mAllParents;
	}
	
	@Override
	public List<ASTFuncDecl> getConstructors() {
		if (mConstructors == null && mClassifier != null) {
			List<ASTFuncDecl> out = new ArrayList<>();
			for (ConstructorDeclaration ct : mClassifier.getConstructors()) {
				ASTFuncDecl fd = new ASTFuncDecl(null, ct.getNameAsString(), -1, -1);
				List<ASTParamDecl> params = new ArrayList<>();
				for (Parameter param : ct.getParameters()) {
					IType pt = getTypeForName(param.getTypeAsString());
					if (pt != null)
						params.add(new ASTParamDecl(fd, param.getNameAsString(), pt, -1, -1));
					else
						params.add(new ASTParamDecl(fd, param.getNameAsString(), param.getTypeAsString(), -1, -1));
				}
				fd.setParameters(params);
				fd.setReturnType(this);
				out.add(fd);
			}
			out.sort(new Comparator<ASTFuncDecl>() {
				@Override
				public int compare(ASTFuncDecl o1, ASTFuncDecl o2) {
					return Integer.compare(o1.getParameterCount(), o2.getParameterCount());
				}
			});
			mConstructors = out;
		}
		return mConstructors;
	}

	@Override
	public List<ASTFuncDecl> getAllFunctionDeclarations() {
		if (mMethods == null && mClassifier != null) {
			Set<ASTFuncDecl> out = new TreeSet<>(new Comparator<ASTFuncDecl>() {
				@Override
				public int compare(ASTFuncDecl o1, ASTFuncDecl o2) {
					return o1.getSignature().compareToIgnoreCase(o2.getSignature());
				}
			});
			
			for (MethodDeclaration md : mClassifier.getMethods()) {
				ASTFuncDecl fd = new ASTFuncDecl(null, md.getNameAsString(), -1, -1);
				List<ASTParamDecl> params = new ArrayList<>();
				for (Parameter param : md.getParameters()) {
					IType pt = getTypeForName(param.getTypeAsString());
					if (pt != null)
						params.add(new ASTParamDecl(fd, param.getNameAsString(), pt, -1, -1));
					else
						params.add(new ASTParamDecl(fd, param.getNameAsString(), param.getTypeAsString(), -1, -1));
				}
				fd.setParameters(params);
				IType rt = getTypeForName(md.getTypeAsString());
				if (rt == null && mTypeArguments != null) {
					if (mTypeArguments.size() == 1)
						rt = getTypeForName(mTypeArguments.get(0));
				}
				fd.setReturnType(rt);
				out.add(fd);
			}
			for (ClassOrInterfaceType sup : getAllParents()) {
				QualifiedName super_type_name = getFullQualifiedType(sup.getNameAsString());
				if (super_type_name != null) {
					Optional<NodeList<Type>> args = sup.getTypeArguments();
					ArrayList<String> typeArguments = new ArrayList<>();
					if (args.isPresent()) {
						for (Type t : args.get()) {
							typeArguments.add(t.asString());
						}
					}
					IType super_type = TypeManager.get().getType(super_type_name, typeArguments);
					if (super_type != null) {
						List<ASTFuncDecl> tmp = super_type.getAllFunctionDeclarations();
						if (tmp != null)
							out.addAll(tmp);
					}
				}
			}
			ArrayList<ASTFuncDecl> decls = new ArrayList<>(out);
			decls.sort(new Comparator<ASTFuncDecl>() {
				@Override
				public int compare(ASTFuncDecl o1, ASTFuncDecl o2) {
					String s1 = o1.getName() + ":" + o1.getParameterCount();
					String s2 = o2.getName() + ":" + o2.getParameterCount();
					return s1.compareTo(s2);
				}
			});
			mMethods = decls;
		}
		return mMethods;
	}	

	

	
	Class<?> getRepresentedClass(){ 
		if (mClazz == null) {
			final String fqn = getFullQualifiedName();
			try {
				mClazz = Class.forName(fqn);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return mClazz;
	}
	
	@Override
	public boolean inherits(IType pt) {
//		if (pt == this)
//			return true;
		if (pt instanceof JavaType == false)
			return false; //FIXME: its possible to inherit with JS or Python
		JavaType jt = (JavaType)pt;
		
		Class<?> this_c = getRepresentedClass();
		Class<?> pt_c = jt.getRepresentedClass();
		if (TypeManager.inherits(this_c, pt_c))
			return true;
		return false;
	}





}
