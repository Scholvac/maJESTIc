package de.sos.script.ast.lang.js;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IdeErrorReporter;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import de.sos.script.IScriptSource;
import de.sos.script.ast.ASTAssign;
import de.sos.script.ast.ASTBlock;
import de.sos.script.ast.ASTFuncCall;
import de.sos.script.ast.ASTFuncDecl;
import de.sos.script.ast.ASTLiteral;
import de.sos.script.ast.ASTName;
import de.sos.script.ast.ASTNode;
import de.sos.script.ast.ASTParamDecl;
import de.sos.script.ast.ASTPropAccess;
import de.sos.script.ast.ASTReturn;
import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.UnknownStatement;
import de.sos.script.ast.lang.IASTConverter;
import de.sos.script.ast.lang.java.JarManager;

public class JSASTConverter implements IASTConverter, NodeVisitor {

	private static class JSErrorCollector implements IdeErrorReporter{
		
		private final CompilationUnit		mCU;
		private final String				mContent;
		
		public JSErrorCollector(final CompilationUnit cu, final String content) {
			mCU = cu;
			mContent = content;
		}
		
		@Override
		public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
			throw new UnsupportedOperationException();
		}
		@Override
		public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void warning(String message, String sourceName, int offset, int length) {
			mCU.addWarning(message, getLine(offset, length));
		}

		@Override
		public void error(String message, String sourceName, int offset, int length) {
			mCU.addError(message, getLine(offset, length));
		}

		private int getLine(int offset, int length) {
			//return the first line, by counting the line-breaks in content
			int p = 0;
			int sum = 0;
			while(p >= 0 && p <= offset) {
				p = mContent.indexOf('\n', p+1);
				if (p >= 0)
					sum++;
			}
			return sum;
		}
		
	}

	private ASTNode		 			mASTNode;
	private final JarManager 		mJarManager;
	private final JSSystemScope 	mSystemScope;
	
	
	public JSASTConverter(JarManager jmgr, JSSystemScope jsSystemScope) {
		mJarManager = jmgr;
		mSystemScope = jsSystemScope;
	}


	protected JSSystemScope getSystemScope() {
		return mSystemScope;
	}
	
	
	protected AstRoot getRoot(String sourceString, String sourceURI, int lineno, JSErrorCollector errorCollector) {
		CompilerEnvirons env = CompilerEnvirons.ideEnvirons();
		Parser p = new Parser(env, errorCollector);
		return p.parse(sourceString, sourceURI, lineno);
	}
	@Override
	public CompilationUnit convert(final IScriptSource source, int start, int end) {
		final String identifier = source.getIdentifier();
		final String content = source.getContentAsString();
		if (end < 0) end = content.length();
		
		CompilationUnit cu = new CompilationUnit(identifier, start, end);
		
		AstRoot src_ast = getRoot(content, identifier, start, new JSErrorCollector(cu, content));
		
		cu.setSystemScope(getSystemScope());
		mASTNode = cu;
		src_ast.visit(this);		
		return cu;
	}
	@Override
	public boolean visit(AstNode node) {
		switch(node.getType()) {
		case Token.FUNCTION:
			return handleFunction((FunctionNode)node);
		case Token.BLOCK:
			return handleBlock(node);
		case Token.ASSIGN:
			return handleAssign((Assignment)node);
		case Token.GETPROP:
			return handlePropAccess((PropertyGet)node);
		case Token.NAME:
			return handleName((Name)node);
		case Token.VAR:
			return handleVariableDeclaration((VariableDeclaration)node);
		case Token.NEW:
			return handleNew((NewExpression)node);
		case Token.EXPR_RESULT:
		case Token.EXPR_VOID:
			return handleExpression((ExpressionStatement)node);
		case Token.CALL:
			return handleCall((FunctionCall)node);
		case Token.RETURN:
			return handleReturn((ReturnStatement)node);
		case Token.NUMBER:
			return handleNumber(node);
		case Token.STRING:
			return handleString(node);
		case Token.TRUE:
		case Token.FALSE:
			return handleBoolean(node);
		case Token.NULL:
			return handleNull(node);
		}
		if (node instanceof InfixExpression) {
			return handleBinaryOperator((InfixExpression)node);
		}
		if (node instanceof AstRoot)
			return true; //do nothing but also do not add an unknown statement
		return handleUnknownStatement(node);
	}
	
	private boolean handleNull(AstNode node) {
		ASTLiteral lit = new ASTLiteral(mASTNode, "VOID", node.toSource(), start(node), end(node));
		return false;
	}
	private boolean handleBoolean(AstNode node) {
		ASTLiteral lit = new ASTLiteral(mASTNode, "Boolean", node.toSource(), start(node), end(node));
		return false;
	}
	private boolean handleString(AstNode node) {
		ASTLiteral lit = new ASTLiteral(mASTNode, "String", node.toSource(), start(node), end(node));
		return false;
	}
	private boolean handleNumber(AstNode node) {
		ASTLiteral lit = new ASTLiteral(mASTNode, "Number", node.toSource(), start(node), end(node));
		return false;
	}
	private boolean handleUnknownStatement(AstNode node) {
		UnknownStatement ukstmt = new UnknownStatement(mASTNode, start(node), end(node));
		ukstmt.setSource(node.toSource());
		ukstmt.setType(node.getClass().getSimpleName());
		mASTNode = ukstmt;
		
		node.visit(new NodeVisitor() {			
			@Override
			public boolean visit(AstNode node2) {
				if (node2 == node)
					return true;
				return JSASTConverter.this.visit(node2);
			}
		});		
		
		mASTNode = ukstmt.getParent();
		return false;
	}
	private boolean handleBinaryOperator(InfixExpression node) {
		node.getLeft();
		node.getRight();
		int op = node.getOperator();
		return false;
	}
	private boolean handleReturn(ReturnStatement node) {
		ASTReturn ret = new ASTReturn(mASTNode, start(node), end(node));
		mASTNode = ret;
		node.getReturnValue().visit(this);
		mASTNode = ret.getParent();
		return false;
	}
	private boolean handleCall(FunctionCall node) {
		List<AstNode> arguments = node.getArguments();
		AstNode target = node.getTarget();
		String funcName = null;
		ASTFuncCall call = null;
		ASTNode callOn = null;
		int cidx = 0;
		if (target.getType() == Token.GETPROP) {
			PropertyGet pg = (PropertyGet)target;
			call = new ASTFuncCall(mASTNode, name(pg.getRight()), start(node), end(node));
			mASTNode = call;
			
			pg.getLeft().visit(this);
			
			mASTNode = call.getParent();
			callOn = call.getChildren().get(0);
			cidx = 1;
		}else if (target.getType() == Token.NAME) {
			funcName = name(target);
			call = new ASTFuncCall(mASTNode, funcName, start(node), end(node));
			callOn = null;
		}
		if (call != null)
			call.setCallOn(callOn);
		else {
			return handleUnknownStatement(node);
		}
		
		if (arguments != null && arguments.isEmpty() == false) {
			mASTNode = call;			
			for (AstNode arg : arguments) {
				arg.visit(this);
			}
			mASTNode = call.getParent();
			List<ASTNode> children = call.getChildren();
			if (children != null) {
				call.setArguments(children.subList(cidx, children.size()));
			}
		}
		
		return false;
	}
	private boolean handleExpression(ExpressionStatement node) {
		node.getExpression().visit(this);
		return false;
	}
	private boolean handleNew(NewExpression node) {
		// TODO We handle the new-Expression as a call but mark the call as a constructor call
		handleCall(node);
		//mASTNode.getChildren().get(mASTNode.getChildren().size()-1);
		return false;
	}
	private boolean handleVariableDeclaration(VariableDeclaration node) {
		//note: we create an assignment statement for each declared variable, thus we can reduce the number
		//of AST - node (types) but lose the information that it actually is a declaration, e.g. the first 
		//assignment for this variable
		VariableDeclaration vd = node;
		
		for (VariableInitializer vi : vd.getVariables()) {
			AstNode initializer = vi.getInitializer();
			String name = name(vi.getTarget());
			
			ASTAssign assignNode = new ASTAssign(mASTNode, name, start(vi), end(vi));
			mASTNode = assignNode;
			
			initializer.visit(this);
			
			mASTNode = assignNode.getParent();			
		}
		return false;
	}
	private boolean handleName(Name node) {
		ASTName n = new ASTName(mASTNode, name(node), start(node), end(node)); //side-effect: this is added as child to mASTNode
		return false;
	}
	private boolean handlePropAccess(PropertyGet node) {
		String propName = name(node.getRight());
		ASTPropAccess propAccess = new ASTPropAccess(mASTNode, propName, start(node), end(node));
		mASTNode = propAccess;
		node.getLeft().visit(this);
		mASTNode = propAccess.getParent();
		return false;
	}
	private boolean handleAssign(Assignment node) {
		String assignTargetName = name(node.getLeft());
		ASTAssign assignNode = new ASTAssign(mASTNode, assignTargetName, start(node), end(node));
		mASTNode = assignNode;
		
		node.getRight().visit(this);
		
		mASTNode = assignNode.getParent();
		return false;
	}
	private boolean handleBlock(AstNode node) {
		ASTBlock block = new ASTBlock(mASTNode, start(node), end(node));
		mASTNode = block;
		
		node.visit(new NodeVisitor() {			
			@Override
			public boolean visit(AstNode node2) {
				if (node2 == node)
					return true;
				return JSASTConverter.this.visit(node2);
			}
		});		
		
		mASTNode = block.getParent();
		return false;
	}
	private boolean handleFunction(FunctionNode node) {
		String func_name = node.getName();
		
		ASTFuncDecl func = new ASTFuncDecl(mASTNode, func_name, start(node), end(node));
		List<AstNode> paramNodes = node.getParams();
		List<ASTParamDecl> paramDecls = new ArrayList<>();
		for (AstNode paramNode : paramNodes) {
			//at this point, we just extract the name, as JS does not have any type-hint system
			//those parameters may later be defined more accurate - for now we assign the type Object / Any
			String pName = name(paramNode);
			paramDecls.add(new ASTParamDecl(func, pName, (String)null, start(paramNode), end(paramNode)));
		}
		func.setParameters(paramDecls);
		mASTNode = func;
		
		
		node.getBody().visit(this);
		
		mASTNode = func.getParent();
		return false;
	}

	
	
	private String name(AstNode ln) {
		if (ln.getType() == Token.NAME)
			return ((Name)ln).getString();
		return null;
	}
	private int start(AstNode node) {
		return node.getAbsolutePosition();
	}
	private int end(AstNode node) {
		return start(node) + node.getLength();
	}
}
