package domain.antlr.semantic.tfgGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import domain.antlr.gram.tfg.tfgParser;
import domain.antlr.semantic.tfg.EvalVisitor;
import domain.antlr.semantic.tfg.Value;

public class EvalVisitorGUI extends EvalVisitor {
	private int deep;
	private ArrayList<State> states;
	private HashMap<Value,Value> listValues;
	public EvalVisitorGUI(){
		super();
		deep=0;
		listValues=new HashMap<Value, Value>();
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
	
	public State createState(int nline, int ntabs, String sline, Map<String, Value> variablesStack){
		State st=null;
		Value v=null;
		Map<String, Value> var = new HashMap<String, Value>();
		Map<String, ListGraph> varList = new HashMap<String, ListGraph>();
		Set<Entry<String, Value>> variables = variablesStack.entrySet();
		for (Entry<String, Value> e : variables){
			v=e.getValue();
			if (!listValues.containsKey(v)){
				listValues.put(v,new Value(e.getValue()));
			}
			if (v.isList())
				varList.put(e.getKey(), new ListGraph(listValues.get(v).asList()));
			else
				var.put(e.getKey(), listValues.get(v));
		}
		st=new State(nline, ntabs, sline, var,varList);
		return st;
	}
	
	private ParserRuleContext getAssignmentInheritance(ParserRuleContext son){
		ParserRuleContext result;
		ParserRuleContext parent = son.getParent();
			if (parent instanceof tfgParser.Expression_listContext)
				result=null;
			else if (parent instanceof tfgParser.AssignmentStatementContext)
				result=parent;
			else
				result=getAssignmentInheritance(parent);
		return result;
	}
	
	protected void errorHandler(String error, int line) {
		State err = null;
		if (states.size()>0){
			err = states.get(states.size()-1);
			err.setError(error);
		}else{
			err=createState(line, 0, error, variableStack.peek());
			states.add(err);
		}
		throw new ParseCancellationException();
	}
	private void setCellColor(String var, int index){
		if (states.size()>0&&index>=0)
			states.get(states.size()-1).updateList(var, index);
	}
	
	public ArrayList<State> getStates() {
		return states;
	}
	
	public Value visitStart(tfgParser.StartContext ctx) {
		Value val=visitChildren(ctx);
		int nline=1;
		if(ctx.stop!=null) nline=ctx.stop.getLine();
		State st=createState(nline,0,"The exectuion has finished. Total instructions: "+states.size(),variableStack.peek());
		states.add(st);
		st.setFinal(true);
		return val;
	}
	
	public Value visitIfStatement(tfgParser.IfStatementContext ctx) {
		String line = "";
		State st = createState(ctx.start.getLine(),deep++,line,variableStack.peek());
		st.setSline(ctx.IF()+"("+getStringLine(ctx.condition.children)+")");
		states.add(st);
		Value val = null;
		val = super.visitIfStatement(ctx);
		deep--;
		st.setColorBool(val.asBoolean());
		return val;
	}
	
	public Value visitElseIfStatement(tfgParser.ElseIfStatementContext ctx) {
		String line = ctx.ELSE()+" "+ctx.IF()+"("+getStringLine(ctx.condition.children)+")";
		State st = createState(ctx.start.getLine(),deep++,line,variableStack.peek());
		Value val = null;
		states.add(st);
		val = super.visitElseIfStatement(ctx);
		deep--;
		st.setColorBool(val.asBoolean());
		return val;
	}
	
	public Value visitElseStatement(tfgParser.ElseStatementContext ctx) {
		String line = ctx.ELSE().toString();
		State st = createState(ctx.start.getLine(),deep-1,line,variableStack.peek());
		states.add(st);
		Value val = visitChildren(ctx);
		return val;
	}
	
	public Value visitAssignmentStatement(tfgParser.AssignmentStatementContext ctx) {
		String line = "";
		Value value = null;
		State st = null;
		
		if (ctx.getParent() instanceof tfgParser.ForClassicStatementContext){
			super.visitAssignmentStatement(ctx);
		}else{
			st = createState(ctx.start.getLine(), deep,line,variableStack.peek());
			states.add(st);
			line=ctx.l.getText() + "=";
			if (ctx.r.children.get(0) instanceof tfgParser.Function_call_idContext){
				st.setSline(line+getStringLine(ctx.r.children));
				value = super.visitAssignmentStatement(ctx);
				st = createState(ctx.start.getLine(), deep,"",variableStack.peek());
				states.add(st);
			}else
				value = super.visitAssignmentStatement(ctx);
			if (states.size()>=0){
				if (!(ctx.getParent() instanceof tfgParser.ForClassicStatementContext))
					st.setSline(line+value);
			}
		}
		return value;
	}
	
	public Value visitFunction_call_id(tfgParser.Function_call_idContext ctx) {
		String line = getStringLine(ctx.children);
		ParserRuleContext ancestor = getAssignmentInheritance(ctx);
		Value val = null;
		if (ancestor==null)
			states.add(createState(ctx.start.getLine(),deep,line,variableStack.peek()));
		deep++;
		val = super.visitFunction_call_id(ctx);
		deep--;
		return val;
	}
	
	public Value visitReturnStatement(tfgParser.ReturnStatementContext ctx) {
		String line = ctx.RETURN() +" "+ super.visit(ctx.ret).toString();
		states.add(createState(ctx.start.getLine(),deep,line,variableStack.peek()));
		return super.visit(ctx.ret);
	}
	
	public Value visitFunction_call_print(tfgParser.Function_call_printContext ctx) {
		String line = ctx.PRINT()+"("+super.visit(ctx.rvalue())+")";
		states.add(createState(ctx.start.getLine(),deep,line,variableStack.peek()));
		return super.visitFunction_call_print(ctx);
	}

	public Value visitRvalueArrayIndexAssign(tfgParser.RvalueArrayIndexAssignContext ctx) {
		State st = createState(ctx.start.getLine(),deep,"",variableStack.peek());
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
	
	protected boolean checkForCondition(tfgParser.ForClassicStatementContext ctx){
		boolean ret;
		State st = null;
		String line = "";
		st = createState(ctx.start.getLine(),--deep,line,variableStack.peek());
		states.add(st);
		ret=super.checkForCondition(ctx);
		line=ctx.FOR().getText() + "("
				+getStringLine(ctx.leftAssignment.children)+";"
				+getStringLine(ctx.condition.children)+";"
				+getStringLine(ctx.rightAssignment.children)+")";
		st.setSline(line);
		st.setColorBool(ret);
		deep++;
		return ret;
	}
	
	public Value visitForClassicStatement(tfgParser.ForClassicStatementContext ctx) {
		String line="";
		State st = null;
		Value val= Value.VOID;
		super.visit(ctx.leftAssignment);
		st = createState(ctx.start.getLine(),deep++,line,variableStack.peek());
		states.add(st);
		line = ctx.FOR().getText() + "("
				+getStringLine(ctx.leftAssignment.children)+";"
				+getStringLine(ctx.condition.children)+";"
				+getStringLine(ctx.rightAssignment.children)+")";
		st.setSline(line);
		val=super.visitForClassicStatement(ctx);
		deep--;
		st.setColorBool(val.asBoolean());
		return val;
	}
	
	public Value visitWhileStatement(tfgParser.WhileStatementContext ctx) {
		String line="";
		State st =createState(ctx.start.getLine(),deep++,line,variableStack.peek());
		Value ret=Value.VOID;
		states.add(st);
		line = ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
		ret=super.visitWhileStatement(ctx);
		deep--;
		st.setColorBool(ret.asBoolean());
		st.setSline(line);
		return ret;
	}
	protected boolean checkWhileCondition(tfgParser.WhileStatementContext ctx){
		boolean ret;
		State st = null;
		String line = "";
		st = createState(ctx.start.getLine(),--deep,line,variableStack.peek());
		states.add(st);
		ret=super.checkWhileCondition(ctx);
		line=ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
		st.setSline(line);
		st.setColorBool(ret);
		deep++;
		return ret;
	}
	
	public Value visitDoWhileStatement(tfgParser.DoWhileStatementContext ctx) {
		String line=ctx.DO().getText();
		State st = null;
		Value cond = null;
		st=createState(ctx.start.getLine(),deep++,line,variableStack.peek());
		states.add(st);
		cond = super.visitDoWhileStatement(ctx);
		deep--;
		return cond;
	}
	protected boolean checkDoWhileCondition(tfgParser.DoWhileStatementContext ctx){
		boolean ret;
		String line=ctx.WHILE()+"("+getStringLine(ctx.rvalue().children)+")";
		State st = null;
		st=createState(ctx.condition.start.getLine(),--deep,line,variableStack.peek());
		states.add(st);
		ret=super.checkDoWhileCondition(ctx);
		st.setColorBool(ret);
		if (ret){
			line=ctx.DO().getText();
			st=createState(ctx.start.getLine(),deep,line,variableStack.peek());
			states.add(st);
		}
		deep++;
		return ret;
	}
}
