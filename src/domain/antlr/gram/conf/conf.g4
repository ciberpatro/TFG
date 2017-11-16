grammar conf;

start : (CRLF* CURLYBRACKETOPEN CRLF+ expression_list CURLYBRACKETCLOSED (CRLF+ | EOF))*;

expression_list : exp=expression+ #expressionList;
expression : exp=PROPERTIES val=value #expressionStatement;

value : EQUALS str=STRING CRLF+ #valueStatement;

PROPERTIES : 'NAME' | 'FOLDER_NAME' | 'ALGORITHM' | 'DESCRIPTION' ;
EQUALS : '=';
CURLYBRACKETOPEN : '{' ;
CURLYBRACKETCLOSED : '}' ;
CRLF : '\n';

STRING : '"'  ( ~('\n'|'\r'|'"'|'\'') )* '"'  {setText(getText().substring(1, getText().length()-1));}
	   | '\'' ( ~('\n'|'\r'|'"'|'\'') )* '\'' {setText(getText().substring(1, getText().length()-1));};

WHITESPACES : [ \t\r]+ -> skip;
