package semantica;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import gram.*;

import org.antlr.v4.runtime.tree.*;

public class EvalVisitor extends tfgBaseVisitor<Value> {
	private Map<String,tfgParser.Function_definitionContext> functions = new HashMap<String,tfgParser.Function_definitionContext>(); 
	private Stack<Map<String, Value>> variableStack = new Stack<Map<String, Value>>(){{push(new HashMap<String, Value>());}};

	public Value visitAssigment(tfgParser.AssigmentContext ctx) {
		String id = this.visit(ctx.lvalue()).asString();
		Value value = this.visit(ctx.rvalue());
		Map<String, Value> variables = variableStack.peek();
		
		variables.put(id, value);
		return Value.VOID;
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
		Map<String, Value> variables = variableStack.peek();
		return variables.get(id); 
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
			case "*":
				if (entero){
					result=new Value(r.asInteger()*r1.asInteger());
				}else if (number){
					result=new Value(r.asDouble()*r1.asDouble());
				}
			break;
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
		System.out.println(r1.getClass());
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
	
	public Value visitFunction_call_print(tfgParser.Function_call_printContext ctx) {
		System.out.println(this.visit(ctx.rvalue()));
		return Value.VOID;
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

	public Value visitArray_assigment(tfgParser.Array_assigmentContext ctx) {
	   	String id = this.visit(ctx.lvalue()).asString();
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
}
