parser grammar RulesParser;

options {
    tokenVocab=RulesLexer;
}

customRuleEOF: head subRuleEOF;
subRuleEOF: subRule+ EOF;
head: AT RuleName;
subRule: REVERSE? subRuleIdentifier arguments?;
subRuleIdentifier
    : (AT|DOUBLE_COLON) RuleName
    | HASHTAG? NamespacedId NBT?
    ;
arguments
    : OPEN pair (COMMA pair)* CLOSE
    | OPEN CLOSE
    ;
pair: Parameter EQUAL Argument;
