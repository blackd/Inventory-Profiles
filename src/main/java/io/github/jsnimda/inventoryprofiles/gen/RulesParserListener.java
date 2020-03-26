// Generated from src/main/java/io/github/jsnimda/inventoryprofiles/parser/antlr/RulesParser.g4 by ANTLR 4.8
package io.github.jsnimda.inventoryprofiles.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RulesParser}.
 */
public interface RulesParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RulesParser#customRuleEOF}.
	 * @param ctx the parse tree
	 */
	void enterCustomRuleEOF(RulesParser.CustomRuleEOFContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#customRuleEOF}.
	 * @param ctx the parse tree
	 */
	void exitCustomRuleEOF(RulesParser.CustomRuleEOFContext ctx);
	/**
	 * Enter a parse tree produced by {@link RulesParser#ruleEntryEOF}.
	 * @param ctx the parse tree
	 */
	void enterRuleEntryEOF(RulesParser.RuleEntryEOFContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#ruleEntryEOF}.
	 * @param ctx the parse tree
	 */
	void exitRuleEntryEOF(RulesParser.RuleEntryEOFContext ctx);
	/**
	 * Enter a parse tree produced by {@link RulesParser#head}.
	 * @param ctx the parse tree
	 */
	void enterHead(RulesParser.HeadContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#head}.
	 * @param ctx the parse tree
	 */
	void exitHead(RulesParser.HeadContext ctx);
	/**
	 * Enter a parse tree produced by {@link RulesParser#ruleEntry}.
	 * @param ctx the parse tree
	 */
	void enterRuleEntry(RulesParser.RuleEntryContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#ruleEntry}.
	 * @param ctx the parse tree
	 */
	void exitRuleEntry(RulesParser.RuleEntryContext ctx);
	/**
	 * Enter a parse tree produced by {@link RulesParser#ruleIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterRuleIdentifier(RulesParser.RuleIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#ruleIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitRuleIdentifier(RulesParser.RuleIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link RulesParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(RulesParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(RulesParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RulesParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(RulesParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(RulesParser.PairContext ctx);
}