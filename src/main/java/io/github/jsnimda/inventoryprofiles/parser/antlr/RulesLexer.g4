lexer grammar RulesLexer;
// https://github.com/antlr/grammars-v4/blob/master/json/JSON.g4

// default = mode mDeclare
WS : [\p{white_space}]+ -> skip ;
AT: '@' -> mode(mDeclareName);
ERR: .;
fragment ID: [a-zA-Z0-9_]+;

mode mDeclareName; // no ws in between
DeclareName: ID -> type(RuleName), mode(mSubRule);
ERR_mDeclareName: . -> type(ERR);

mode mSubRule;
REVERSE: '!';
AT_mSubRule: '@' -> type(AT), pushMode(mSubRuleName);
DOUBLE_COLON: '::'           -> pushMode(mSubRuleName);
HASHTAG: '#';
NamespacedId: ID ':' ID | ID;
OPEN: '(' -> pushMode(mArgs);
NBT: '{' NESTED* '}';
WS_mSubRule: WS -> skip;
ERR_mSubRule: . -> type(ERR);

mode mSubRuleName; // no ws in between
RuleName: ID -> popMode; // goto mSubRule
ERR_mRule: . -> type(ERR);

mode mArgs;
Parameter: ID;
EQUAL: '='    -> pushMode(mArg);
CLOSE: ')'    -> popMode; // goto mSubRule
WS_mArgs : WS -> skip;
ERR_mArgs: .  -> type(ERR);

mode mArg;
CLOSE_mArg: ')' -> type(CLOSE), popMode, popMode; // goto mSubRule
COMMA: ','      -> popMode; // goto mArgs
WS_mArg : WS -> skip;
Argument
    : NON_NESTED+
    ;
ERR_mArg: . -> type(ERR);

fragment COMMON
    : STRING
    | '(' NESTED* ')'
    | '[' NESTED* ']'
    | '{' NESTED* '}'
    ;
fragment NON_NESTED
    : COMMON
    | ~["',{[()\]}]+
    ;
fragment NESTED
    : COMMON
    | ~["'{[()\]}]+ // allow ',' now
    ;

fragment STRING: STRING_SINGLE | STRING_DOUBLE;
fragment STRING_DOUBLE
    : '"' (ESC_DOUBLE | SAFECODEPOINT_DOUBLE)* '"'
    ;
fragment STRING_SINGLE
    : '\'' (ESC_SINGLE | SAFECODEPOINT_SINGLE)* '\''
    ;
fragment ESC_DOUBLE
    : '\\' (["\\/bfnrt] | UNICODE)
    ;
fragment ESC_SINGLE
    : '\\' (['\\/bfnrt] | UNICODE)
    ;
fragment UNICODE
    : 'u' HEX HEX HEX HEX
    ;
fragment HEX
    : [0-9a-fA-F]
    ;
fragment SAFECODEPOINT_DOUBLE
    : ~ ["\\\u0000-\u001F]
    ;
fragment SAFECODEPOINT_SINGLE
    : ~ ['\\\u0000-\u001F]
    ;
