grammar TS;

@header {
package parse;
}

// PARSER ======================================================================
file
    :  'Transition' 'system' '{' stateDef+ '}'
    ;

stateDef
    :  'State' INT (',' 'initial')? ':' 'Outgoing' 'transitions' ':' edgeDef+
    ;

edgeDef
    :  'to' 'state' INT 'labeled' orlabel
    ;

orlabel
    :  '(' ulabel ')'  ('||' '(' ulabel ')')*
    ;

ulabel
    :  '(' andformula ')' 'U' '(' orformula ')'
    ;

orformula
    :  '(' andformula ')' ('||' '(' andformula ')')+
    |  andformula
    ;

andformula
    :  notformula ('&&' notformula)*
    ;

notformula
    :  'T'
    |  ('!')? ID
    ;

// LEXER =======================================================================
INT :  ('0' | '1'..'9' DIGIT*)
    ;
fragment
DIGIT
    :  '0'..'9'
    ;


ID  :  LETTER (LETTER | DIGIT | '_')*
    ;
fragment
LETTER
    :  ('a'..'z' | 'A'..'Z')
    ;

WS  :  [ \r\t\u000C\n]+ -> channel(HIDDEN)
    ;

COMMENT
    :  '/*' .*? '*/'    -> channel(HIDDEN)
    ;

LINE_COMMENT
    :  '//' ~[\r\n]* -> channel(HIDDEN)
    ;