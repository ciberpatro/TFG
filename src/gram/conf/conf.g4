grammar conf;

start : (CRLF* CURLYBRACKETOPEN expression_list CURLYBRACKETCLOSED)* CRLF* EOF;

expression_list : (CRLF* exp=expression)+ #expressionList;
expression : exp=EXPRESSION val=value #expressionStatement;

value : EQUALS str=STRING CRLF* #valueStatement;

EQUALS : '=';
CURLYBRACKETOPEN : '{' ;
CURLYBRACKETCLOSED : '}' ;
CRLF : '\n';

STRING : DOUBLE_QUOTE STRING_TEXT DOUBLE_QUOTE {setText(getText().substring(1, getText().length()-1));}
	   | SINGLE_QUOTE STRING_TEXT SINGLE_QUOTE {setText(getText().substring(1, getText().length()-1));};
fragment STRING_TEXT : ( ESCAPEDQUOTE | ~('\n'|'\r') )* ;
ESCAPEDQUOTE : '\\"' ;


DOUBLE_QUOTE : '"';
SINGLE_QUOTE : '\'';


EXPRESSION : 'NAME' | 'FOLDER_NAME' | 'ALGORITHM' | 'DESCRIPTION' ;

WHITESPACES : [ \t\r]+ -> skip;
