lexer grammar RulesLexer;
// https://github.com/antlr/grammars-v4/blob/master/json/JSON.g4

WS : [\p{white_space}]+ -> skip ;
AT: '@' -> mode(mDeclareName);
ERR: .;
fragment ID: [a-zA-Z0-9_]+;

mode mDeclareName; // no ws in between
DeclareName: ID -> type(RuleName), mode(mRuleEntry);
ERR_mDeclareName: . -> type(ERR);

mode mRuleEntry;
REVERSE: '!';
AT_mRuleEntry: '@' -> type(AT), pushMode(mRuleName);
DOUBLE_COLON: '::'           -> pushMode(mRuleName);
HASHTAG: '#'                 -> pushMode(mRuleName);
ItemName: ID ':' ID | ID;
OPEN: '(' -> pushMode(mArgs);
NBT: '{' NESTED* '}';
WS_mRuleEntry: WS -> skip;
ERR_mRuleEntry: . -> type(ERR);

mode mRuleName; // no ws in between
RuleName: ID -> popMode; // goto mRuleEntry
ERR_mRule: . -> type(ERR);

mode mArgs;
Parameter: ID;
EQUAL: '='    -> pushMode(mArg);
CLOSE: ')'    -> popMode; // goto mRuleEntry
WS_mArgs : WS -> skip;
ERR_mArgs: .  -> type(ERR);

mode mArg;
CLOSE_mArg: ')' -> type(CLOSE), popMode, popMode; // goto mRuleEntry
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
