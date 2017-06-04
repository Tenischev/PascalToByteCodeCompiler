grammar Pascal;

program : nameOfProg defs block '.';

nameOfProg : 'program' PROGNAME SEMICOLON;

defs : varDef? procDef?;
varDef : 'var' variable SEMICOLON (variable SEMICOLON)*;
variable : VAR (',' VAR)* ':' type;

procDef : (procedure SEMICOLON)*;
procedure : 'procedure' header SEMICOLON body;

header : VAR (LPAREN (variable (SEMICOLON variable)*)* RPAREN)?;
body : (varDef)? block ;

type : 'integer' | 'double' | 'boolean';

block : 'begin' line SEMICOLON? (SEMICOLON line SEMICOLON?)* 'end';

line : ifStatement
    | forLoop
    | whileLoop
    | repeatUntil
    | assigment
    | methodCall
    | block
    | read
    | write
    | writeln
    | brk;

methodCall : VAR arguments;
assigment : VAR ':=' expressionLogic;

forLoop : 'for' assigment side expressionLogic step? doWord line; // for i := 1 to i step 5 do
repeatUntil : 'repeat' line ( SEMICOLON line )* untilWord expressionLogic;
whileLoop : 'while' expressionLogic doWord line;

ifStatement : 'if' expressionLogic thenWord line (elseWord line)?;

step : 'step' (VAR | INT);

read : 'read' LPAREN (VAR (',' VAR)*)* RPAREN;
write : 'write' arguments;
writeln : 'writeln' arguments;

brk : 'break' SEMICOLON;

expressionLogic : expressionCompare (('or' | 'and') expressionCompare)*;
expressionCompare : expressionDownArithmetic (op expressionDownArithmetic)*;
expressionDownArithmetic : expressionHighArithmetic ((PLUS | MINUS ) expressionHighArithmetic )*;
expressionHighArithmetic : expressionUnarity ((divWord | MULT | modWord | DIV) expressionUnarity )*;
expressionUnarity : (PLUS | MINUS | 'not') expressionUnarity
                    | expressionValueEmbeded;
expressionValueEmbeded : VAR
                        | INT
                        | DOUBLE
                        | (TRUE | FALSE)
                        | LPAREN expressionLogic RPAREN
                        ;

op : LESS_SIGN | LESS_EQUAL | MORE_SIGN | MORE_EQUAL | equals | notEquals;
side : TO | DOWNTO ;
doWord : 'do';
untilWord : 'until';
elseWord : 'else';
thenWord : 'then';
divWord : 'div';
modWord : 'mod';
equals : EQUALS;
notEquals : NOT_EQUAL;

arguments : LPAREN (expressionLogic ( ',' expressionLogic )*)* RPAREN;

PLUS       : '+';
MINUS      : '-';
MULT       : '*';
DIV        : '/';
EQUALS     : '=';
LPAREN     : '(';
RPAREN     : ')';
SEMICOLON  : ';';
NOT_EQUAL  : '<>';
LESS_SIGN  : '<';
MORE_SIGN  : '>';
LESS_EQUAL : '<=';
MORE_EQUAL : '>=';
TO         : 'to';
DOWNTO     : 'downto';
TRUE       : 'true';
FALSE      : 'false';

VAR : [a-z_] [a-z0-9_]*;

PROGNAME : [a-z]+;

INT : ([1-9] [0-9]+) | [0-9];
DOUBLE : ([0-9]+)'.'([0-9]+);

WS : [ \t\r\n]+ -> skip;