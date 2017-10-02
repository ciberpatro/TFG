package semantic.tfgGUI;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import gram.tfg.tfgParser;
import gram.tfg.tfgParser.ForClassicStatementContext;
import semantic.tfg.EvalVisitor;
import semantic.tfg.Value;

public class EvalVisitorGUI extends EvalVisitor {
	private ArrayList<State> states;
	public EvalVisitorGUI(){
		super();
		states = new ArrayList<State>();
	}
	
	private String getStringLine(List<ParseTree> children){
		int nChilds=-1;
		String rvalue="";
		for (ParseTree c : children){

			nChilds=c.getChildCount();
			if (nChilds>=3){
				List<ParseTree> aux =new ArrayList<>();
				for (int i=0;i<nChilds;i++){
					aux.add(c.getChild(i));
				}
				rvalue+=getStringLine(aux);
			}else if (nChilds==1||nChilds==2){
				Value val = super.visit(c);
				if (val!=null&&val!=Value.VOID){
					rvalue+=super.visit(c);
				}else
					rvalue+=c.getText();
			}else if (nChilds==0)
				rvalue+=c.getText();
		}
		return rvalue;
	}
	
	private String parseForIter(tfgParser.ForClassicStatementContext ctx){
		String line = ctx.FOR().getText() + "("
				+getStringLine(ctx.leftAssignment.children)+";"
				+getStringLine(ctx.condition.children)+";"
				+getStringLine(ctx.rightAssignment.children)+")";
		return line;
	}
	
	public ArrayList<State> getStates() {
		return states;
	}

	public Value visitIfStatement(tfgParser.IfStatementContext ctx) {
		String line = ctx.IF()+"("+getStringLine(ctx.condition.children)+")";
		states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
		return super.visitIfStatement(ctx);
	}
	
	public Value visitAssignmentStatement(tfgParser.AssignmentStatementContext ctx) {
		String line = ctx.l.getText() + ctx.op.getText() + super.visit(ctx.r);
		Value test= super.visitAssignmentStatement(ctx);
		if (states.size()>=0){
			if (!(ctx.getParent() instanceof tfgParser.ForClassicStatementContext)){
				states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
			}else{
				tfgParser.ForClassicStatementContext ctxFor= (ForClassicStatementContext) ctx.getParent();
				line = ctxFor.FOR().getText() + "("
						+getStringLine(ctxFor.leftAssignment.children)+";"
						+getStringLine(ctxFor.condition.children)+";"
						+getStringLine(ctxFor.rightAssignment.children)+")";
				states.add(new State (ctx.start.getLine(),ctx.getParent().start.getCharPositionInLine(),line,variableStack.peek()));
			}
		}
		return test;
	}
	
	public Value visitFunction_call_id(tfgParser.Function_call_idContext ctx) {
		String line = getStringLine(ctx.children);
		states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
		return super.visitFunction_call_id(ctx);
	}
	
	public Value visitElseIfStatement(tfgParser.ElseIfStatementContext ctx) {
		String line = ctx.IF()+"("+getStringLine(ctx.condition.children)+")";
		states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
		return super.visitElseIfStatement(ctx);
	}
	
	public Value visitFunction_call_print(tfgParser.Function_call_printContext ctx) {
		String line = ctx.PRINT()+"("+super.visit(ctx.rvalue())+")";
		states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
		return super.visitFunction_call_print(ctx);
	}

	public Value visitRvalueArrayIndexAssign(tfgParser.RvalueArrayIndexAssignContext ctx) {
		String line = super.visit(ctx.l)+"["+super.visit(ctx.index)+"]"+ctx.operators().getText()+super.visit(ctx.newValue);
		states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
		return super.visitRvalueArrayIndexAssign(ctx);
	}
	
	public Value visitWhileStatement(tfgParser.WhileStatementContext ctx) {
		
		Value condition = this.visit(ctx.condition);
		if (condition.isBoolean()){
			String line = ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
			states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
			while (condition.asBoolean()){
				this.visit(ctx.exprWhile);
				condition = this.visit(ctx.condition);
				line = ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
				states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
			}
		}
		return Value.VOID;
	}
}
