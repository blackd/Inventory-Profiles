lexer grammar ProfilesLexer;


WS : [\p{white_space}]+ -> skip ;

PROFILE      : 'profile';
ACTIVATE     : 'activate';

HOT1         : 'HOT1';
HOT2         : 'HOT2';
HOT3         : 'HOT3';
HOT4         : 'HOT4';
HOT5         : 'HOT5';
HOT6         : 'HOT6';
HOT7         : 'HOT7';
HOT8         : 'HOT8';
HOT9         : 'HOT9';
CHESTPLATE   : 'CHEST';
LEGS         : 'LEGS';
FEET         : 'FEET';
HEAD         : 'HEAD';
OFFHAND      : 'OFFHAND';

//ENCHANTMENTS: '"Enchantments"';

COMMA : ',' ;
//fragment LBRACK : '[';
//LBrack : WS? LBRACK;
LBRACK : '[' ;
RBRACK : ']' ;
LBRACE : '{' ;
RBRACE : '}' ;
DQUOTE : '"' ;
LID    : 'id';
LVL    : 'lvl';
S      : 's';

COLON : ':';
SEMICOLON : ';';

//fragment ENCHANTMENTS    : 'Enchantments';
ENCHANTMENTS   : '"Enchantments" :';
POTION         : '"Potion" :';

ARROW : '->';

//Arrow: WS? ARROW ;
fragment ID: [a-zA-Z0-9_\-]+;

fragment NUMBER: [0-9]+([a-z])?;

Level: NUMBER;

Id :  ID;

NamespacedId: DQUOTE ID ':' ID DQUOTE;



STRING
   : '"' (ESC | SAFECODEPOINT)* '"'
   ;


fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;
fragment UNICODE
   : 'u' HEX HEX HEX HEX
   ;
fragment HEX
   : [0-9a-fA-F]
   ;
fragment SAFECODEPOINT
   : ~ ["\\\u0000-\u001F]
   ;
