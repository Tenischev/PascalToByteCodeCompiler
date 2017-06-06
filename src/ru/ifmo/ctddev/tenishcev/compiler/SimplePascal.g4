grammar SimplePascal;

program : nameOfProg defs block '.';

nameOfProg : 'program' PROGNAME SEMICOLON;

defs : varDef?;
varDef : 'var' variables SEMICOLON (variables SEMICOLON)*;
variables : variable (',' variable)* ':' type;

type : 'longint';

block : 'begin' line SEMICOLON? (SEMICOLON line SEMICOLON?)* 'end';

line : ifStatement
    | forLoop
    | whileLoop
    | assigment
    | block
    | writeln
    | brk;

assigment : VAR ':=' expressionArithmetic;

forLoop : 'for' assigment side expressionArithmetic doWord line; // for i := 1 to i do

whileLoop : 'while' expressionLogic doWord line;

ifStatement : 'if' expressionLogic thenWord line;

writeln : 'writeln' arguments;

brk : 'break' SEMICOLON;

expressionLogic : expressionCompare;
expressionCompare : expressionArithmetic op expressionArithmetic;
expressionArithmetic : expressionValueEmbeded ((plus | minus | modWord | divWord) expressionValueEmbeded)?;
expressionValueEmbeded : variable
                        | number
                        ;

op : less | lessOrEq | great | greatOrEq | equals | notEq;

variable : VAR;
number : INT;

side : to | downto ;
to : TO;
downto : DOWNTO;
doWord : 'do';
thenWord : 'then';
modWord : 'mod';
divWord : 'div';
minus : MINUS;
plus : PLUS;
less : LESS_SIGN;
lessOrEq : LSEQ_SIGN;
great : GRET_SIGN;
greatOrEq : GREQ_SIGN;
equals : EQUALS;
notEq : NOTEQUALS;

arguments : LPAREN expressionArithmetic RPAREN;

PLUS       : '+';
MINUS      : '-';
EQUALS     : '=';
LPAREN     : '(';
RPAREN     : ')';
SEMICOLON  : ';';
LESS_SIGN  : '<';
LSEQ_SIGN  : '<=';
GRET_SIGN  : '>';
GREQ_SIGN  : '>=';
NOTEQUALS  : '<>';

TO         : 'to';
DOWNTO     : 'downto';

VAR : [a-zA-Z_] [a-zA-Z0-9_]*;

PROGNAME : [a-zA-Z]+;

INT : ([1-9] [0-9]+) | [0-9];

WS : [ \t\r\n]+ -> skip;