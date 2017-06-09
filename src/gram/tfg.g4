grammar tfg;

start : CRLF* expression_list EOF;

expression_list : (expression CRLF+)*;
 
expression : function_definition # expression_function_definition
			|if_statement # expression_if_statement
			|rvalue # expression_rvalue
			|for_statement # expression_for
			|while_statement # expression_while
			;

function_definition: header=function_definition_header body=function_definition_body END;

function_definition_header: DEF id=IDENTIFICADOR PARENTESISABIERTO param=function_definition_params? PARENTESISCERRADO CRLF;
 
function_definition_params: (IDENTIFICADOR COMA)* IDENTIFICADOR;

function_definition_body : expression_list return_statement?;

return_statement: RETURN rvalue CRLF;
 
function_call : PRINT PARENTESISABIERTO rvalue PARENTESISCERRADO # function_call_print
			  | SIZE PARENTESISABIERTO rvalue PARENTESISCERRADO # function_call_size
			  | id=IDENTIFICADOR PARENTESISABIERTO param=function_call_param_list? PARENTESISCERRADO # function_call_id
			  ;

function_call_param_list: (rvalue COMA)* rvalue;

 
if_statement: IF PARENTESISABIERTO condition=rvalue PARENTESISCERRADO CRLF exprIf=expression_list exprElseIf=elseIf_statement* exprElse=else_statement? END #ifStatement;

elseIf_statement: ELSE IF PARENTESISABIERTO condition=rvalue PARENTESISCERRADO CRLF expr=expression_list #elseIfStatement;

else_statement:ELSE CRLF expr=expression_list #elseStatement;

for_statement : FOR element=lvalue IN iterator=rvalue CRLF exprFor=expression_list END #forInStatement
			  ;

while_statement : WHILE PARENTESISABIERTO condition=rvalue PARENTESISCERRADO CRLF exprWhile=expression_list END #whileStatement;

assigment: l=lvalue op=(IGUAL | MASIGUAL | MENOSIGUAL | MULTIGUAL | DIVISIONIGUAL | PORCENTAJEIGUAL | ELEVADOIGUAL) r=rvalue;

array_assigment: l=lvalue  op=(IGUAL | MASIGUAL | MENOSIGUAL)  a=array_definition;

array_definition: CORCHETEABIERTO eleArray=array_definition_elements? CORCHETECERRADO;

array_definition_elements: (rvalue COMA)* rvalue;

lvalue: IDENTIFICADOR  	#lvaluelocal
		|ID_GLOBAL      #lvalueglobal
		;
 
rvalue: 
	lvalue	#rvalueLvalue
	|PARENTESISABIERTO val=rvalue PARENTESISCERRADO	#rvalueParenthesis
	|CADENA	#rvalueCadena
	|BOOLEAN	#rvalueBoolean
	|FLOAT	#rvalueFloat
	|ENTERO	#rvalueEntero
	|NULO	#rvalueNull
	|matrix=rvalue CORCHETEABIERTO index=rvalue CORCHETECERRADO	#rvalueArraySelection
	|from=rvalue RANGO to=rvalue #rvalueArrayDefRange
	|array_definition	#rvalueArrayDefinition
	|array_assigment	#rvalueregla8
	|assigment	#rvalueregla9
	|function_call	#rvalueFunction_call
	|r=rvalue op=ELEVADO r1=rvalue	#rvalueOp0
	|r=rvalue op=(MULT | DIVISION | PORCENTAJE) r1=rvalue	#rvalueOp1
	|r=rvalue op=(SUMA | RESTA) r1=rvalue	#rvalueop2
	|op=COMPARADORBOL2 r=rvalue	#rvalueBoolean2
	|r=rvalue op=COMPARADORBOL1 r1=rvalue	#rvalueBoolean1
	|r=rvalue op=COMPARADORES1 r1=rvalue	#rvalueComp1
	|r=rvalue op=COMPARADORES2 r1=rvalue	#rvalueComp2
	;

PRINT : 'print';
SIZE : 'size';
REQUIRE : 'require';
END : 'end';
DEF : 'function';
RETURN : 'return';
IF : 'if';
THEN : 'then';
BEGIN : 'begin';
ELSE : 'else';
CASE : 'case';
WHEN : 'when';
UNTIL : 'until';
DO : 'do';
ELSIF : 'elsif';
UNLESS: 'unless';
WHILE : 'while';
RETRY : 'retry';
BREAK : 'break';
FOR : 'for';
IN : 'in';
TO : 'to';
BOOLEAN : 'true' | 'false';
SUMA: '+';
RESTA: '-';
MULT: '*';
DIVISION: '/';
PORCENTAJE: '%';
ELEVADO: '**';
COMPARADORES1: '==' | '!=';
COMPARADORES2: '>' | '<' | '<=' | '>=';
IGUAL: '=';
MASIGUAL: '+=';
MENOSIGUAL: '-=';
MULTIGUAL: '*=';
DIVISIONIGUAL: '/=';
PORCENTAJEIGUAL: '%=';
ELEVADOIGUAL: '**=';
COMPARADORBOL1 : 'and' | '&&' | 'or' | '||';
COMPARADORBOL2 : 'not' | '!';
PARENTESISABIERTO : '(';
PARENTESISCERRADO : ')';
CORCHETEABIERTO : '[';
CORCHETECERRADO : ']';
NULO : 'nil';
ENTERO : [0-9]+ ;
FLOAT : [0-9]+'.'[0-9]+;
IDENTIFICADOR : [a-zA-Z_][a-zA-Z0-9_]*;
ID_GLOBAL : '$' [a-zA-Z_][a-zA-Z0-9_]*;
COMA : ',';
PUNTOYCOMA : ';';
RANGO : '..';
CRLF : '\n';
COMENTARIOS : ('#' ~('\r' | '\n')* '\n' | '=begin' .*? '=end') -> skip;
CADENA :  '"'( ESCAPEDQUOTE| ~('\n'|'\r') )*? '"'| '\''( ESCAPEDQUOTE| ~('\n'|'\r') )*? '\'';
ESCAPEDQUOTE : '\\"';
ESPACIOSBLANCOS : [ \t\r]+ -> skip;
