package semantica;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import gram.*;

import org.antlr.v4.runtime.tree.*;

public class EvalVisitor extends tfgBaseVisitor<Value> {
	private Map<String,tfgParser.Function_definitionContext> functions;
	private Stack<Map<String, Value>> variableStack; 
	
	public EvalVisitor(){
		functions = new HashMap<String,tfgParser.Function_definitionContext>(); 
		variableStack = new Stack<Map<String, Value>>();
		variableStack.push(new HashMap<String, Value>());
	}

	public Value visitAssignmentStatement(tfgParser.AssignmentStatementContext ctx) {
		String id = this.visit(ctx.lvalue()).asString();
		String op = ctx.op.getText();
		Value rvalue = this.visit(ctx.rvalue());
		Map<String, Value> variables = variableStack.peek();
		if (variables.containsKey(id)){
			if (!op.equals("="))
				rvalue=doOperations(variables.get(id), rvalue, op.split("=")[0]);
		}
		variables.put(id, rvalue);
		return rvalue;
	}
	/*Lvalue*/
	public Value visitLvaluelocal(tfgParser.LvaluelocalContext ctx) {
		return new Value(ctx.getText());
	}
	/*Rvalue*/
	/*Rvalue Entero*/
	public Value visitRvalueEntero(tfgParser.RvalueEnteroContext ctx) {
		return new Value(Integer.valueOf(ctx.getText()));
	}
	/*Rvalue Float*/
	public Value visitRvalueFloat(tfgParser.RvalueFloatContext ctx) {
		return new Value(Double.valueOf(ctx.getText()));
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
			//ERROR VARIABLE NOT INITIALIZED TO-DO
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
	public Value visitRvalueOp1(tfgParser.RvalueOp1Context ctx) {
		Value r = this.visit(ctx.r);
		Value r1 = this.visit(ctx.r1);
		String op = ctx.op.getText();
		return doOperations(r,r1,op);
	}
	
	public Value visitRvalueop2(tfgParser.Rvalueop2Context ctx) {
		Value r = this.visit(ctx.r);
		Value r1 = this.visit(ctx.r1);
		String op = ctx.op.getText();
		return doOperations(r,r1,op);
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
		}else{/*ERROR THE VALUE ISN'T BOOLEAN TO-DO*/}
		return new Value(result); 
	}

	public Value visitRvalueBoolean1(tfgParser.RvalueBoolean1Context ctx) {
	   	Value r = this.visit(ctx.r);
		Value r1 =this.visit(ctx.r1);
		String op=ctx.op.getText();
		Boolean result=false;
		if (r.isBoolean()&&r1.isBoolean()){
			switch (op){
				case "and":
				case "&&":
					result=r.asBoolean()&&r1.asBoolean();
				break;
				case "or":
				case "||":
					result=r.asBoolean()||r1.asBoolean();
				break;
			}
		}else{/*ERROR ONE OR MORE OF THE VALUES AREN'T BOOLEANS TO-DO*/}
		return new Value(result);
	}
	
	public Value doOperations(Value r, Value r1, String op){
		Value result=Value.VOID;
		boolean entero=r.isInteger()&&r1.isInteger();
		boolean number=r.isNumber()&&r1.isNumber();
		switch (op){
			case "+":
				if (entero){
					result=new Value(r.asInteger()+r1.asInteger());
				}else if (number){
					result=new Value(r.asDouble()+r1.asDouble());
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
			case "/":
				if (r1.asInteger()!=0){
					if (entero){
						result=new Value(r.asInteger()/r1.asInteger());
					}else if (number){
						result=new Value(r.asDouble()/r1.asDouble());
					}
				}else{/*ERROR DIVISION BY ZERO TO-DO*/}
			break;

			/*ADD MORE OPERATORS TO-DO*/
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
		}else{/*THE BOTH VALUES MUST BE NUMBERS TO-DO*/}
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
		functions.put(func_name, ctx);
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

	public Value visitArray_assigment(tfgParser.Array_assignmentContext ctx) {
	   	String id = this.visit(ctx.l).asString();
		String op = ctx.op.getText();
		Map<String, Value> variables = variableStack.peek();
		switch (op){
		case "=":
			variables.put(id,this.visit(ctx.a));	
		break;
		case "+=":
		break;
		/*TO-DO*/
		}
		return visitChildren(ctx); 
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
			
			if (index<matrix.size())
				vfinal=matrix.get(index);
			else
				System.out.println("Index Error");
		}else
			System.out.println("Object: "+ vmatrix.getValClass() +" is not iterable" );	
		return vfinal; 
	}

	public Value visitRvalueArrayDefRange(tfgParser.RvalueArrayDefRangeContext ctx) {
		Value from = this.visit(ctx.from);
		Value to = this.visit(ctx.to);
		ArrayList<Value> aux=new ArrayList<Value>();
		Value list=new Value(aux);
		if (from.isInteger() && to.isInteger()){
			for (int i=from.asInteger();i<to.asInteger();i++){
				aux.add(new Value(i));
			}	
		}
		return list;
	}
	
	public Value visitArraySingleAssignment(tfgParser.ArraySingleAssignmentContext ctx) {
		String id = this.visit(ctx.l).asString();
		Value val = getVariableValue(id);
		int index = this.visit(ctx.index).asInteger();
		val.asList().set(index,this.visit(ctx.newValue));

		return val;
	}
	
	/*Llamada Funcion*/
	public Value visitFunction_call_id(tfgParser.Function_call_idContext ctx) {
	
		tfgParser.Function_definitionContext func=functions.get(ctx.id.getText());//Funcion
		int idType=func.header.id.getType();//Tipo toquen (IDENTIFICADOR)
		
		Map<String, Value> localVar=new HashMap<String, Value>();//Variables locales antes de entrar a la pila
		
		Value ret =  Value.VOID;
		if (func.header.param!=null && ctx.param!=null){
			List<TerminalNode> decVal=func.header.param.getTokens(idType);
			List<tfgParser.RvalueContext> callVal=ctx.param.rvalue();
			
			int sizeCall=callVal.size();
			int sizeDecla=decVal.size();
			
			/*Compruebo que los argumentos son el mismo numero que los declarados*/
			if (sizeCall==sizeDecla){
				for (int i=0;i<sizeCall;i++){
					localVar.put(decVal.get(i).getText(),this.visit(callVal.get(i)));
				}
			}
			variableStack.push(localVar);//Variables locales EN la pila
			ret = this.visit(func.function_definition_body());
			variableStack.pop();//Quitamos la pila una vez acabada la funcion
		}else{/*TO-DO Comprobar si cumple con los mismos parámetros que la función*/}
		return ret;
	}
	/*If statement*/
	public Value visitIfStatement(tfgParser.IfStatementContext ctx) {
		Value condition=this.visit(ctx.condition);
		if (condition.isBoolean()){
			if (condition.asBoolean()){
				this.visit(ctx.exprIf);
			}else if (ctx.exprElseIf != null){
				List<tfgParser.ElseIf_statementContext> elseif=ctx.elseIf_statement();
				for (int i=0;i<elseif.size()&&!condition.asBoolean();i++) 
					condition=this.visit(elseif.get(i));
			}
			if (!condition.asBoolean()&&ctx.exprElse != null) this.visit(ctx.exprElse);
		}else{/*ERROR NO BOOLEAN EXPRESION TO-DO*/}
		return Value.VOID;
	}
	/*ElseIf statement*/
	public Value visitElseIfStatement(tfgParser.ElseIfStatementContext ctx) {
		Value condition=this.visit(ctx.condition);
		if (condition.isBoolean()){
			if (condition.asBoolean()) this.visit(ctx.expr);
		}else{/*ERROR NO BOOLEAN EXPRESION TO-DO*/}
		return condition; 
	}
	/*For statement*/
	public Value visitForInStatement(tfgParser.ForInStatementContext ctx) {
		String id = this.visit(ctx.element).asString();
		Map<String, Value> variables = variableStack.peek();
		Value iter = this.visit(ctx.iterator);
		if (iter.isList()){
			for (int i=0;i<iter.asList().size();i++){
				variables.put(id, iter.asList().get(i));
				this.visit(ctx.exprFor);
			}
		}else{
			//NO ITERATOR ERROR TO-DO
		}
		return Value.VOID;
	}
	
	public Value visitForClassicStatement(tfgParser.ForClassicStatementContext ctx) {
		this.visit(ctx.leftAssignment).asInteger();
		boolean condition = this.visit(ctx.condition).asBoolean();
		for (;condition;){
			this.visit(ctx.exprFor);

			this.visit(ctx.rightAssignment);
			condition = this.visit(ctx.condition).asBoolean();
		}
		return Value.VOID;
	}

	/*While statement*/
	public Value visitWhileStatement(tfgParser.WhileStatementContext ctx) {
		Value condition = this.visit(ctx.condition);
		if (condition.isBoolean()){
			while (condition.asBoolean()){
				this.visit(ctx.exprWhile);
				condition = this.visit(ctx.condition);
			}
		}
		return Value.VOID;
	}
}
