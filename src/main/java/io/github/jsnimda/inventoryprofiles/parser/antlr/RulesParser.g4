parser grammar RulesParser;

options {
    tokenVocab=RulesLexer;
}

customRuleEOF: head ruleEntry+ EOF;
ruleEntryEOF: ruleEntry EOF;
head: AT RuleName;
ruleEntry: REVERSE? ruleIdentifier arguments?;
ruleIdentifier
    : (AT|DOUBLE_COLON) RuleName
    | HASHTAG RuleName NBT?
    | ItemName NBT?
    ;
arguments
    : OPEN pair (COMMA pair)* CLOSE
    | OPEN CLOSE
    ;
pair: Parameter EQUAL Argument;
