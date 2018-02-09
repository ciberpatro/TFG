grammar tfg;

start : expression_list;

expression_list : CRLF* (expression (CRLF+ | EOF) )*;
 
expression : function_definition # expression_function_definition
			|if_statement # expression_if_statement
			|rvalue # expression_rvalue
			|for_statement # expression_for
			|while_statement # expression_while
			|do_while_statement #expression_do_while
			;

function_definition : header=function_definition_header body=function_definition_body END;

function_definition_header : DEF id=ID ROUNDBRACKETOPEN param=function_definition_params? ROUNDBRACKETCLOSED CRLF;
 
function_definition_params : (ID COMMA)* ID;

function_definition_body : expr=expression_list ret=return_statement? #functionDefBody;

return_statement: RETURN ret=rvalue CRLF #returnStatement;
 
function_call : PRINT ROUNDBRACKETOPEN rvalue ROUNDBRACKETCLOSED # function_call_print
			  | SIZE ROUNDBRACKETOPEN rvalue ROUNDBRACKETCLOSED # function_call_size
			  | COPY ROUNDBRACKETOPEN val=rvalue ROUNDBRACKETCLOSED # function_call_copy
			  | id=ID ROUNDBRACKETOPEN param=function_call_param_list? ROUNDBRACKETCLOSED # function_call_id
			  ;

function_call_param_list : (rvalue COMMA)* rvalue;

if_statement : IF ROUNDBRACKETOPEN condition=rvalue ROUNDBRACKETCLOSED CRLF exprIf=expression_list exprElseIf=elseIf_statement* exprElse=else_statement? END #ifStatement;

elseIf_statement : ELSE IF ROUNDBRACKETOPEN condition=rvalue ROUNDBRACKETCLOSED CRLF expr=expression_list #elseIfStatement;

else_statement : ELSE CRLF expr=expression_list #elseStatement;

for_statement : FOR ROUNDBRACKETOPEN leftAssignment=assignment SEMICOLON condition=rvalue SEMICOLON rightAssignment=assignment ROUNDBRACKETCLOSED CRLF exprFor=expression_list END #forClassicStatement;

while_statement : WHILE ROUNDBRACKETOPEN condition=rvalue ROUNDBRACKETCLOSED CRLF exprWhile=expression_list END #whileStatement;

do_while_statement : DO CRLF expr=expression_list WHILE ROUNDBRACKETOPEN condition=rvalue ROUNDBRACKETCLOSED #doWhileStatement;

assignment : l=lvalue op=operators r=rvalue #assignmentStatement;

operators : EQUALS | PLUS_EQUAL | MINUS_EQUAL | MULT_EQUAL | DIV_EQUAL | MOD_EQUAL | POW_EQUAL;

array_definition : SQUAREBRACKETOPEN eleArray=array_definition_elements? SQUAREBRACKETCLOSED;

array_definition_elements : (rvalue COMMA)* rvalue;

lvalue : ID	#lvaluelocal;

rvalue :
	ROUNDBRACKETOPEN val=rvalue ROUNDBRACKETCLOSED	#rvalueParenthesis
	|lvalue	#rvalueLvalue
	|STRING	#rvalueCadena
	|BOOLEAN	#rvalueBoolean
	|FLOAT	#rvalueFloat
	|INTEGER	#rvalueEntero
	|array_definition	#rvalueArrayDefinition
	|matrix=rvalue SQUAREBRACKETOPEN index=rvalue SQUAREBRACKETCLOSED	#rvalueArraySelection
	|assignment #rvalueAssignment
	|l=rvalue SQUAREBRACKETOPEN index=rvalue SQUAREBRACKETCLOSED op=operators newValue=rvalue #rvalueArrayIndexAssign
	|function_call	#rvalueFunction_call
	|r=rvalue op=POW r1=rvalue	#rvalueOp0
	|op=(PLUS | MINUS) r=rvalue #rvalueUnaryOp
	|r=rvalue op=(MULT | DIV | MOD) r1=rvalue	#rvalueOp1
	|r=rvalue op=(PLUS | MINUS) r1=rvalue	#rvalueop2
	|r=rvalue op=COMP1 r1=rvalue	#rvalueComp1
	|r=rvalue op=COMP2 r1=rvalue	#rvalueComp2
	|r=rvalue op=COMPBOOL1 r1=rvalue	#rvalueBoolean1
	|op=COMPBOOL2 r=rvalue	#rvalueBoolean2
	;

INTEGER : [0-9]+ ;
FLOAT : [0-9]+'.'[0-9]+;
STRING :   '"'( ESCAPEDQUOTE | ~('\n'|'\r'|'"'|'\'') )*? '"' {setText(getText().substring(1, getText().length()-1));}
		| '\''( ESCAPEDQUOTE | ~('\n'|'\r'|'"'|'\'') )*? '\''{setText(getText().substring(1, getText().length()-1));};
BOOLEAN : 'true' | 'false';
PRINT : 'print';
SIZE : 'size';
COPY : 'copy';
END : 'end';
DEF : 'function';
RETURN : 'return';
IF : 'if';
ELSE : 'else';
DO : 'do';
WHILE : 'while';
FOR : 'for';
POW : '**';
PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
MOD : '%';
COMP1 : '==' | '!=';
COMP2 : '>' | '<' | '<=' | '>=';
EQUALS : '=';
PLUS_EQUAL : '+=';
MINUS_EQUAL : '-=';
MULT_EQUAL : '*=';
DIV_EQUAL : '/=';
MOD_EQUAL : '%=';
POW_EQUAL : '**=';
COMPBOOL1 : 'and' | '&&' | 'or' | '||';
COMPBOOL2 : 'not' | '!';
ROUNDBRACKETOPEN : '(';
ROUNDBRACKETCLOSED : ')';
SQUAREBRACKETOPEN : '[';
SQUAREBRACKETCLOSED : ']';
ID : [a-zA-Z_][a-zA-Z0-9_]*;
COMMA : ',';
SEMICOLON : ';';
CRLF : '\n';
COMMENTS : ('#' ~('\r' | '\n')* '\n' | '/*' .*? '*/') -> skip;
ESCAPEDQUOTE : '\\"' 
			 | '\\\'';
WHITE_SPACES : [ \t\r]+ -> skip;
