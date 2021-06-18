// Generated from src/main/java/org/anti_ad/mc/ipnext/parser/antlr/RulesParser.g4 by ANTLR 4.8
package org.anti_ad.mc.ipnext.gen;
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
	 * Enter a parse tree produced by {@link RulesParser#subRuleEOF}.
	 * @param ctx the parse tree
	 */
	void enterSubRuleEOF(RulesParser.SubRuleEOFContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#subRuleEOF}.
	 * @param ctx the parse tree
	 */
	void exitSubRuleEOF(RulesParser.SubRuleEOFContext ctx);
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
	 * Enter a parse tree produced by {@link RulesParser#subRule}.
	 * @param ctx the parse tree
	 */
	void enterSubRule(RulesParser.SubRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#subRule}.
	 * @param ctx the parse tree
	 */
	void exitSubRule(RulesParser.SubRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link RulesParser#subRuleIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterSubRuleIdentifier(RulesParser.SubRuleIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link RulesParser#subRuleIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitSubRuleIdentifier(RulesParser.SubRuleIdentifierContext ctx);
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