package domain.antlr.semantic.tfg;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import domain.antlr.gram.tfg.*;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.*;

public class EvalVisitor extends tfgBaseVisitor<Value> {
	private Stack<Map<String,tfgParser.Function_definitionContext>> functions;
	protected Stack<Map<String, Value>> variableStack;
	
	public EvalVisitor(){
		functions = new Stack<Map<String,tfgParser.Function_definitionContext>>();
		functions.push(new HashMap<String,tfgParser.Function_definitionContext>());
		variableStack = new Stack<Map<String, Value>>();
		variableStack.push(new HashMap<String, Value>());
	}
	
	protected void errorHandler(String error, int line){
		System.out.println(error+"\nLine: "+(line+1));
		throw new ParseCancellationException();
	}

	public Value visitAssignmentStatement(tfgParser.AssignmentStatementContext ctx) {
		String id = this.visit(ctx.lvalue()).asString();
		String op = ctx.op.getText();
		Value rvalue = this.visit(ctx.rvalue());
		Map<String, Value> variables = variableStack.peek();
		if (rvalue!=Value.VOID){
			if (variables.containsKey(id)){
				if (!op.equals("="))
					rvalue=doOperations(variables.get(id), rvalue, op.split("=")[0], ctx.start.getLine());
			}
			variables.put(id, rvalue);
		}else{
			errorHandler("The function returned void value to an assignment",ctx.start.getLine());
		}
		return rvalue;
	}
	/*Lvalue*/
	public Value visitLvaluelocal(tfgParser.LvaluelocalContext ctx) {
		return new Value(ctx.getText());
	}
	/*Rvalue*/
	/*Rvalue Entero*/
	public Value visitRvalueEntero(tfgParser.RvalueEnteroContext ctx) {
		Value val = null;
		try{
			val=new Value(Integer.valueOf(ctx.getText()));
		}catch (NumberFormatException e){
			errorHandler("The number "+ctx.getText()+" is out of bounds.\n"+Integer.MIN_VALUE+" / "+Integer.MAX_VALUE, ctx.start.getLine());
		}
		return val;
	}
	/*Rvalue Float*/
	public Value visitRvalueFloat(tfgParser.RvalueFloatContext ctx) {
		Value val = null;
		try{
			val=new Value(Double.valueOf(ctx.getText()));
		}catch (NumberFormatException e){
			errorHandler("The number "+ctx.getText()+" is out of bounds.\n"+Double.MIN_VALUE+" / "+Double.MAX_VALUE, ctx.start.getLine());
		}
		return val;
	}
	/*Rvalue Boolean*/
	public Value visitRvalueBoolean(tfgParser.RvalueBooleanContext ctx) {
		return new Value(Boolean.valueOf(ctx.getText()));
	}
	/*Rvalue Cadena*/
	public Value visitRvalueCadena(tfgParser.RvalueCadenaContext ctx) {
		return new Value(ctx.getText());
	}
	/*Rvalue Lvalue*/
	public Value visitRvalueLvalue(tfgParser.RvalueLvalueContext ctx) {
		String id = this.visit(ctx.lvalue()).asString();
		Value value = getVariableValue(id);
		if (value.equals(Value.VOID)){
			errorHandler("The variable "+id+" must be initialized.",ctx.start.getLine());
		}
		return value;
	}

	public Value getVariableValue(String id){
		Value val=Value.VOID;
		Map<String, Value> variables = variableStack.peek();
		if (variables.containsKey(id)){
			val = variables.get(id);
		}
		return val;
	}

	/*Rvalue Parenthesis*/
	public Value visitRvalueParenthesis(tfgParser.RvalueParenthesisContext ctx) {
	   	return this.visit(ctx.val); 
	}
	/*Rvalue operations*/
	public Value visitRvalueOp0(tfgParser.RvalueOp0Context ctx) {
		Value r = this.visit(ctx.r);
		Value r1 = this.visit(ctx.r1);
		String op = ctx.op.getText();
		return doOperations(r,r1,op,ctx.start.getLine());
	}
	
	public Value visitRvalueOp1(tfgParser.RvalueOp1Context ctx) {
		Value r = this.visit(ctx.r);
		Value r1 = this.visit(ctx.r1);
		String op = ctx.op.getText();
		return doOperations(r,r1,op,ctx.start.getLine());
	}
	
	public Value visitRvalueop2(tfgParser.Rvalueop2Context ctx) {
		Value r = this.visit(ctx.r);
		Value r1 = this.visit(ctx.r1);
		String op = ctx.op.getText();
		return doOperations(r,r1,op,ctx.start.getLine());
	}
	public Value visitRvalueUnaryOp(tfgParser.RvalueUnaryOpContext ctx) {
		Value r = this.visit(ctx.r);
		String op = ctx.op.getText();
		
		if (r.isNumber()){
			int aux=1;
			if (op.equals("-")){
				aux=-1;
			}
			if (r.isInteger()){
				/*TODO error with r sign and op sign at parsing*/
				r=new Value(aux*r.asInteger());
			}else{
				r=new Value(aux*r.asDouble());
			}
		}else{
			errorHandler("The value: "+r.getValClass()+" is not a number.", ctx.start.getLine());
		}
		return r;
	}

	public Value visitRvalueBoolean2(tfgParser.RvalueBoolean2Context ctx) {
	   	Value r = this.visit(ctx.r);
		String op = ctx.op.getText();
		Boolean result=false;
		if (r.isBoolean()){
			switch(op){
				case "!":
				case "not":
					result=!r.asBoolean();
				break;
			}
		}else{
			errorHandler("The value: "+r.getValClass()+" is not boolean.",ctx.start.getLine());
		}
		return new Value(result); 
	}

	public Value visitRvalueBoolean1(tfgParser.RvalueBoolean1Context ctx) {
	   	Value r = this.visit(ctx.r);
		Value r1 =null;
		String op=ctx.op.getText();
		Boolean result=false;
		if (r.isBoolean()){
			result=r.asBoolean();
			switch (op){
				case "and":
				case "&&":
				if (result) r1=this.visit(ctx.r1);
			break;
				case "or":
				case "||":
				if (!result) r1=this.visit(ctx.r1);
			break;
			}
			if (r1!=null){
				if (r1.isBoolean())
					result=r1.asBoolean();
				else
					errorHandler("The value: " + r1.getValClass() +
							" is not boolean", ctx.start.getLine());
			}
		}else{
			r1=this.visit(ctx.r1);
			if (r.isBoolean()^r1.isBoolean()){
				errorHandler("The value: " + (r.isBoolean()? r1.getValClass() : r.getValClass())+
							" is not boolean", ctx.start.getLine());
			}else{
				errorHandler("Neither of the values are boolean.\n"+
							"Value 1: "+r.getValClass() + "\n" +
							"Value 2: " + r1.getValClass(), ctx.start.getLine());
			}
		}
		return new Value(result);
	}
	
	public Value doOperations(Value r, Value r1, String op, int line){
		Value result=Value.VOID;
		boolean entero=r.isInteger()&&r1.isInteger();
		boolean number=r.isNumber()&&r1.isNumber();
		switch (op){
			case "+":
				if (entero){
					result=new Value(r.asInteger()+r1.asInteger());
				}else if (number){
					result=new Value(r.asDouble()+r1.asDouble());
				}else if (r.isString()&&r1.isString()){
					result=new Value(r.asString()+r1.asString());
				}else if (r.isList()&&r1.isList()){
					ArrayList<Value> aux= r.asList();
					aux.addAll(r1.asList());
					result=new Value(aux);
				}else if (r.isString()||r1.isString()){
					result=new Value(r.toString()+r1.toString());
				}
			break;
			case "-":
				if (entero){
					result=new Value(r.asInteger()-r1.asInteger());
				}else if (number){
					result=new Value(r.asDouble()-r1.asDouble());
				}
			break;
			case "*":
				if (entero){
					result=new Value(r.asInteger()*r1.asInteger());
				}else if (number){
					result=new Value(r.asDouble()*r1.asDouble());
				}
			break;
			case "**":
				if (entero){
					result=new Value((int)Math.pow(r.asInteger(), r1.asInteger()));
				}else if (number){
					result=new Value(Math.pow(r.asDouble(), r1.asDouble()));
				}
			break;
			case "/":
				if (r1.asDouble()!=0){
					if (entero){
						result=new Value(r.asInteger()/r1.asInteger());
					}else if (number){
						result=new Value(r.asDouble()/r1.asDouble());
					}
				}else{
					errorHandler("Division by zero",line);
				}
			break;
			case "%":
				if (r1.asDouble()!=0){
					if (entero){
						result=new Value(r.asInteger()%r1.asInteger());
					}else if (number){
						result=new Value(r.asDouble()%r1.asDouble());
					}
				}else{
					errorHandler("Division by zero",line);
				}
			break;
		}
		if (result==Value.VOID){
			errorHandler("The operator "+ op + " is incompatible with the values: "+r.getValClass()+" and "+r1.getValClass(), line);
		}
		return result;
	}

	public Value visitRvalueComp2(tfgParser.RvalueComp2Context ctx) {
	   	Value r=this.visit(ctx.r);
		Value r1=this.visit(ctx.r1);
		String op=ctx.op.getText();
		Value result=Value.VOID;
		if (r.isNumber()&&r1.isNumber()){
			switch (op){
				case ">":
					result=new Value(r.asDouble()>r1.asDouble());
				break;
				case ">=":
					result=new Value(r.asDouble()>=r1.asDouble());
				break;
				case "<":
					result=new Value(r.asDouble()<r1.asDouble());
				break;
				case "<=":
					result=new Value(r.asDouble()<=r1.asDouble());
				break;
			}
		}else if (r.isString()&&r1.isString()){
			int val = r.asString().compareTo(r1.asString());
			switch (op){
			case ">":
				result=new Value(val>0);
			break;
			case ">=":
				result=new Value(val>=0);
			break;
			case "<":
				result=new Value(val<0);
			break;
			case "<=":
				result=new Value(val<=0);
			break;
			}
		}else{
			errorHandler("The values " +r.getValClass() +" and "+ r1.getValClass() +" are unorderable types",ctx.start.getLine());
		}
		return result;
	}

	public Value visitRvalueComp1(tfgParser.RvalueComp1Context ctx) {
		Value r=this.visit(ctx.r);
		Value r1=this.visit(ctx.r1);
		String op=ctx.op.getText();
		Value result=Value.VOID;
		switch (op){
			case "==":
				result=new Value(r.equals(r1));
			break;
			case "!=":
				result=new Value(!r.equals(r1));
			break;
		}
		return result;
	}

	/*Semantica Funciones*/
	/*Definicion Funcion*/
	public Value visitFunction_definition(tfgParser.Function_definitionContext ctx) {
		String func_name= this.visit(ctx.function_definition_header()).asString();
		if (!functions.peek().containsKey(func_name))
			functions.peek().put(func_name, ctx);
		else
			errorHandler("The function: "+func_name+" already exists.", ctx.start.getLine());
		return Value.VOID;
	}
	
	/*Cabecera Funcion*/
	public Value visitFunction_definition_header(tfgParser.Function_definition_headerContext ctx) {
		return new Value(ctx.id.getText());
	}
	/*Print function*/
	public Value visitFunction_call_print(tfgParser.Function_call_printContext ctx) {
		System.out.println(this.visit(ctx.rvalue()));
		return Value.VOID;
	}
	/*Size function*/
	public Value visitFunction_call_size(tfgParser.Function_call_sizeContext ctx) {
		Value rvalue = this.visit(ctx.rvalue());
		int size=-1;
		if (rvalue.isList()){
			size=rvalue.asList().size();
		}
		return new Value(size);
	}

	public Value visitFunction_call_copy(tfgParser.Function_call_copyContext ctx) {
		return new Value(this.visit(ctx.val));
	}	

	/*Arrays*/
	public Value visitArray_definition(tfgParser.Array_definitionContext ctx) {
		Value v=Value.VOID;
		ArrayList<Value> list=new ArrayList<Value>();
		if (ctx.eleArray != null){
			List<tfgParser.RvalueContext> elements=ctx.eleArray.rvalue();	
			for (tfgParser.RvalueContext rvalue : elements){
				list.add(this.visit(rvalue));
			}
			v=new Value(list);
		}else v=new Value(new ArrayList<Value>());
		return v;
	}

	public Value visitRvalueArraySelection(tfgParser.RvalueArraySelectionContext ctx) {
		ArrayList<Value> matrix = null;
		int index = -1;
		Value vmatrix=this.visit(ctx.matrix);
		Value vindex=this.visit(ctx.index);
		Value vfinal=Value.VOID;
		
		if (vmatrix.isList() && vindex.isInteger()){
			matrix=vmatrix.asList();
			index=vindex.asInteger();
			if (index<matrix.size()&&index>=0){
				vfinal=matrix.get(index);
			}else{
				errorHandler(String.format("Index Error.\n"
						+ "Index: %d\n"
						+ "Matrix: %s Size: %d",
						index, matrix, matrix.size()), ctx.start.getLine());
			}
		}else{
			if (vindex.isList())
				errorHandler("The value "+ vmatrix.getValClass() +" is not iterable",ctx.start.getLine());
			else
				errorHandler("The index "+ index + " must be a Integer",ctx.start.getLine());
		}
		return vfinal; 
	}
	
	public Value visitRvalueArrayIndexAssign(tfgParser.RvalueArrayIndexAssignContext ctx) {
		int index=-1;
		Value val = this.visit(ctx.l);
		Value r = null;
		Value vIndex=this.visit(ctx.index);
		if (vIndex.isInteger()){
			index=vIndex.asInteger();
			r = this.visit(ctx.newValue);
			if (val.isList()){
				if (index<val.asList().size()&&index>=0){
					if (!ctx.op.getText().equals("="))
						r=doOperations(r, val.asList().get(index), ctx.op.getText().split("=")[0], ctx.start.getLine());
					val.asList().set(index,r);
				}else{
					errorHandler(String.format("Index Error.\n"
							+ "Index: %d\n"
							+ "Matrix: %s Size: %d",
							index, val.asList(), val.asList().size()), ctx.start.getLine());
				}
			}else
				errorHandler("The value "+ val.getValClass() +" is not iterable",ctx.start.getLine());
		}else
			errorHandler("The index "+ index + " must be a Integer",ctx.start.getLine());
		return r;
	}
	
	/*Llamada Funcion*/
	public Value visitFunction_call_id(tfgParser.Function_call_idContext ctx) {
		int idType,sizeCall,sizeDecla=-1;
		Value ret =  Value.VOID;
		tfgParser.Function_definitionContext func= null;
		Map<String, Value> localVar=new HashMap<String, Value>();//Variables locales antes de entrar a la pila
		List<TerminalNode> decVal=null;
		List<tfgParser.RvalueContext> callVal=null;
		
		
		if (functions.peek().get(ctx.id.getText())!=null){
			func=functions.peek().get(ctx.id.getText());//Funcion
			idType=func.header.id.getType();//Se consiguen los nombres de las variables locales.
			if (func.header.param!=null && ctx.param!=null){
				decVal=func.header.param.getTokens(idType);
				callVal=ctx.param.rvalue();
				
				sizeCall=callVal.size();
				sizeDecla=decVal.size();
				
				/*Compruebo que los argumentos son el mismo numero que los declarados*/
				if (sizeCall==sizeDecla){
					for (int i=0;i<sizeCall;i++){
						localVar.put(decVal.get(i).getText(),this.visit(callVal.get(i)));
					}
				}else{
					errorHandler("The function " + ctx.getText() +
					             " exists but doesn't have the same number of parameters" ,
					             ctx.start.getLine());
				}
			}else if (func.header.param!=null || ctx.param!=null)
				errorHandler("The function " + ctx.getText() +
				             " exists but doesn't have the same number of parameters",
				             ctx.start.getLine());
			variableStack.push(localVar);//Variables locales EN la pila
			ret = this.visit(func.function_definition_body());
			variableStack.pop();//Quitamos las variables locales una vez acabada la funcion
		}else{
			errorHandler("The function " + ctx.getText() + " doesn't exists." , ctx.start.getLine());
		}
		return ret;
	}
	public Value visitFunctionDefBody(tfgParser.FunctionDefBodyContext ctx) {
		Value ret = Value.VOID;
		this.visit(ctx.expr);
		if (ctx.ret!=null){
			ret=this.visit(ctx.ret);
		}
		return ret;
	}
	
	/*Return statement*/
	public Value visitReturnStatement(tfgParser.ReturnStatementContext ctx) {
		return this.visit(ctx.ret);
	}
	/*If statement*/
	public Value visitIfStatement(tfgParser.IfStatementContext ctx) {
		Value conditionIf=this.visit(ctx.condition);
		Value condition =new Value(conditionIf);
		if (conditionIf.isBoolean()){
			if (conditionIf.asBoolean()){
				this.visit(ctx.exprIf);
			}else if (ctx.exprElseIf != null){
				List<tfgParser.ElseIf_statementContext> elseif=ctx.elseIf_statement();
				for (int i=0;i<elseif.size()&&!condition.asBoolean();i++) 
					condition=this.visit(elseif.get(i));
			}
			if (!condition.asBoolean()&&ctx.exprElse != null) this.visit(ctx.exprElse);
		}else{
			errorHandler("The condition must be a boolean. Value: "+condition.getValClass(),ctx.start.getLine());
		}
		return conditionIf;
	}
	
	/*ElseIf statement*/
	public Value visitElseIfStatement(tfgParser.ElseIfStatementContext ctx) {
		Value condition=this.visit(ctx.condition);
		if (condition.isBoolean()){
			if (condition.asBoolean()) this.visit(ctx.expr);
		}else{
			errorHandler("The condition must be a boolean. Value: "+condition.getValClass(),ctx.start.getLine());
		}
		return condition; 
	}
	
	public Value visitForClassicStatement(tfgParser.ForClassicStatementContext ctx) {
		boolean condition;
		this.visit(ctx.leftAssignment);
		Value cond = this.visit(ctx.condition);
		if (cond.isBoolean()){
			condition = cond.asBoolean();
			for (;condition;){
				this.visit(ctx.exprFor);
				condition =this.checkForCondition(ctx);
			}
		}else{
			errorHandler("The condition must be a boolean. Value: "+cond.getValClass(),ctx.start.getLine());
		}
		return cond;
	}
	protected boolean checkForCondition(tfgParser.ForClassicStatementContext ctx){
		Value cond=Value.VOID;
		/*This do the right assignment and reassign the loop condition*/
		this.visit(ctx.rightAssignment);
		cond = this.visit(ctx.condition);
		if (!cond.isBoolean()){
			errorHandler("The condition must be a boolean. Value: "+cond.getValClass(),ctx.start.getLine());
		}
		return cond.asBoolean();
	}
	/*While statement*/
	public Value visitWhileStatement(tfgParser.WhileStatementContext ctx) {
		boolean condition;
		Value cond = this.visit(ctx.condition);
		if (cond.isBoolean()){
			condition=cond.asBoolean();
			while (condition){
				this.visit(ctx.exprWhile);
				condition=this.checkWhileCondition(ctx);
			}
		}else{
			errorHandler("The condition must be a boolean. Value: "+cond.getValClass(),ctx.start.getLine());
		}
		return cond;
	}
	protected boolean checkWhileCondition(tfgParser.WhileStatementContext ctx){
		Value cond = this.visit(ctx.condition);
		if (!cond.isBoolean()){
			errorHandler("The condition must be a boolean. Value: "+cond.getValClass(),ctx.start.getLine());
		}
		return cond.asBoolean();
	}
	
	public Value visitDoWhileStatement(tfgParser.DoWhileStatementContext ctx) {
		boolean conditon;
		this.visit(ctx.expr);
		Value cond = this.visit(ctx.condition);
		conditon=this.checkDoWhileCondition(ctx);
		while(conditon){
			this.visit(ctx.expr);
			conditon=this.checkDoWhileCondition(ctx);
		}
		return cond;
	}
	protected boolean checkDoWhileCondition(tfgParser.DoWhileStatementContext ctx){
		Value cond = this.visit(ctx.condition);
		if (!cond.isBoolean())
			errorHandler("The condition must be a boolean. Value: "+cond.getValClass(),ctx.start.getLine());
		return cond.asBoolean();
	}
}
