package semantic.tfgGUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.antlr.v4.runtime.misc.ParseCancellationException;
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
	protected void handleError(String error, int line) {
		JOptionPane.showMessageDialog(null,
			    error + "\nLine: "+line,
			    "Error",
			    JOptionPane.ERROR_MESSAGE);
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
		states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
		return super.visitIfStatement(ctx);
	}
	
	public Value visitAssignmentStatement(tfgParser.AssignmentStatementContext ctx) {
		String line = ctx.l.getText() + ctx.op.getText() + super.visit(ctx.r);
		Value value= super.visitAssignmentStatement(ctx);
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
		return value;
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
		Value condition = this.visit(ctx.condition);
		if (condition.isBoolean()){
			String line = ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
			states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
			while (condition.asBoolean()){
				this.visit(ctx.exprWhile);
				condition = this.visit(ctx.condition);
				if (!condition.isBoolean()){
					handleError("The condition must be a boolean. Value: "+condition.getValClass(),ctx.start.getLine());
				}
				line = ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
				states.add(new State (ctx.start.getLine(),ctx.start.getCharPositionInLine(),line,variableStack.peek()));
			}
		}else{
			handleError("The condition must be a boolean. Value: "+condition.getValClass(),ctx.start.getLine());
		}
		return Value.VOID;
	}
}
