package domain.antlr.semantic.tfgGUI;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import domain.antlr.gram.tfg.tfgParser;
import domain.antlr.gram.tfg.tfgParser.ForClassicStatementContext;
import domain.antlr.semantic.tfg.EvalVisitor;
import domain.antlr.semantic.tfg.Value;

public class EvalVisitorGUI extends EvalVisitor {
	private ArrayList<State> states;
	private int deep;
	public EvalVisitorGUI(){
		super();
		deep=0;
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
	protected void errorHandler(String error, int line) {
		State err = null;
		if (states.size()>0){
			err = states.get(states.size()-1);
			err.setError(error);
		}else{
			err=new State(line, 0, error, variableStack);
			states.add(err);
		}
		throw new ParseCancellationException();
	}
	private void setCellColor(String var, int index){
		if (states.size()>0&&index>=0&&index<states.size())
			states.get(states.size()-1).updateGraph(var, index);
	}
	
	public ArrayList<State> getStates() {
		return states;
	}
	
	public Value visitIfStatement(tfgParser.IfStatementContext ctx) {
		String line = ctx.IF()+"("+getStringLine(ctx.condition.children)+")";
		State st = new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack);
		Value val = null;
		states.add(st);
		val = super.visitIfStatement(ctx);
		st.setColorBool(val.asBoolean());
		return val;
	}
	
	public Value visitElseIfStatement(tfgParser.ElseIfStatementContext ctx) {
		String line = ctx.ELSE()+" "+ctx.IF()+"("+getStringLine(ctx.condition.children)+")";
		State st = new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack);
		Value val = null;
		states.add(st);
		val = super.visitElseIfStatement(ctx);
		st.setColorBool(val.asBoolean());
		return val;
	}
	
	public Value visitElseStatement(tfgParser.ElseStatementContext ctx) {
		String line = ctx.ELSE().toString();
		State st = new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack);
		states.add(st);
		return visitChildren(ctx);
	}
	
	public Value visitAssignmentStatement(tfgParser.AssignmentStatementContext ctx) {
		int charPos = ctx.start.getCharPositionInLine();
		String line = "";
		Value value = null;
		State st = null;
		tfgParser.ForClassicStatementContext ctxFor = null;
		if (ctx.getParent() instanceof tfgParser.ForClassicStatementContext){
			ctxFor = (ForClassicStatementContext) ctx.getParent();
			charPos = ctxFor.start.getCharPositionInLine();
		}
		st = new State (ctx.start.getLine(), deep+charPos,line,variableStack);
		states.add(st);
		
		line=ctx.l.getText() + "=";
		value = super.visitAssignmentStatement(ctx);
		
		if (ctx.r.children.get(0) instanceof tfgParser.Function_call_idContext){
			st.setSline(line+getStringLine(ctx.r.children));
			st = new State (ctx.start.getLine(), deep+charPos,"",variableStack);
			states.add(st);
		}
		
		if (states.size()>=0){
			if (!(ctx.getParent() instanceof tfgParser.ForClassicStatementContext)){
				st.setSline(line+value);
			}else{
				line = ctxFor.FOR().getText() + "("
						+getStringLine(ctxFor.leftAssignment.children)+";"
						+getStringLine(ctxFor.condition.children)+";"
						+getStringLine(ctxFor.rightAssignment.children)+")";
				st.setSline(line);
				Value aux = this.visit(ctxFor.condition);
				if (aux.isBoolean()) st.setColorBool(aux.asBoolean());
			}
		}
		return value;
	}
	
	public Value visitFunction_call_id(tfgParser.Function_call_idContext ctx) {
		String line = getStringLine(ctx.children);
		Value val = null;
		System.out.println(ctx.start.getLine()+" "+(ctx.getParent().getParent() instanceof tfgParser.AssignmentStatementContext));
		if (!(ctx.getParent().getParent() instanceof tfgParser.AssignmentStatementContext))
			states.add(new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack));
		deep+=ctx.start.getCharPositionInLine();
		val = super.visitFunction_call_id(ctx);
		deep-=ctx.start.getCharPositionInLine();
		return val;
	}
	
	public Value visitReturnStatement(tfgParser.ReturnStatementContext ctx) {
		String line = ctx.RETURN() +" "+ super.visit(ctx.ret).toString();
		states.add(new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack));
		return super.visit(ctx.ret);
	}
	
	public Value visitFunction_call_print(tfgParser.Function_call_printContext ctx) {
		String line = ctx.PRINT()+"("+super.visit(ctx.rvalue())+")";
		states.add(new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack));
		return super.visitFunction_call_print(ctx);
	}

	public Value visitRvalueArrayIndexAssign(tfgParser.RvalueArrayIndexAssignContext ctx) {
		State st = new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),"",variableStack);
		String line = "";
		states.add(st);
		line = super.visit(ctx.l)+"["+super.visit(ctx.index)+"]"+ctx.operators().getText()+super.visit(ctx.newValue);
		st.setSline(line);
		Value val = super.visitRvalueArrayIndexAssign(ctx);
		setCellColor(ctx.l.getText(), super.visit(ctx.index).asInteger());
		return val;
	}
	
	public Value visitRvalueArraySelection(tfgParser.RvalueArraySelectionContext ctx) {
		Value val = super.visitRvalueArraySelection(ctx);
		setCellColor(ctx.matrix.getText(), super.visit(ctx.index).asInteger());
		return val;
	}
	
	public Value visitWhileStatement(tfgParser.WhileStatementContext ctx) {
		String line="";
		State st = new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack);
		Value condition = null;
		
		states.add(st);
		
		condition = this.visit(ctx.condition);
		line = ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
		st.setSline(line);
		line="";
		if (condition.isBoolean()){
			st.setColorBool(condition.asBoolean());
			while (condition.asBoolean()){
				this.visit(ctx.exprWhile);
				st = new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack);
				states.add(st);
				condition = this.visit(ctx.condition);
				if (!condition.isBoolean()){
					errorHandler("The condition must be a boolean. Value: "+condition.getValClass(),ctx.start.getLine());
				}
				line = ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
				st.setSline(line);
				st.setColorBool(condition.asBoolean());
			}
		}else{
			errorHandler("The condition must be a boolean. Value: "+condition.getValClass(),ctx.start.getLine());
		}
		return Value.VOID;
	}
	public Value visitDoWhileStatement(tfgParser.DoWhileStatementContext ctx) {
		String line=ctx.DO().getText();
		State st = null;
		Value condition = null;
		do{
			st=new State (ctx.start.getLine(),deep+ctx.start.getCharPositionInLine(),line,variableStack);
			states.add(st);
			this.visit(ctx.expr);
			condition=this.visit(ctx.condition);
			if (!condition.isBoolean())
				errorHandler("The condition must be a boolean. Value: "+condition.getValClass(),ctx.start.getLine());
			st=new State (ctx.stop.getLine(),deep+ctx.start.getCharPositionInLine(),ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")",variableStack);
			st.setColorBool(condition.asBoolean());
			states.add(st);
		}while(condition.asBoolean());
		
		return Value.VOID;
	}
}
