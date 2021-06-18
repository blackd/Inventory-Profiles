// Generated from src/main/java/org/anti_ad/mc/ipnext/parser/antlr/RulesParser.g4 by ANTLR 4.8
package org.anti_ad.mc.ipnext.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RulesParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, AT=2, ERR=3, REVERSE=4, DOUBLE_COLON=5, HASHTAG=6, NamespacedId=7, 
		OPEN=8, NBT=9, WS_mSubRule=10, RuleName=11, Parameter=12, EQUAL=13, CLOSE=14, 
		WS_mArgs=15, COMMA=16, WS_mArg=17, Argument=18;
	public static final int
		RULE_customRuleEOF = 0, RULE_subRuleEOF = 1, RULE_head = 2, RULE_subRule = 3, 
		RULE_subRuleIdentifier = 4, RULE_arguments = 5, RULE_pair = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"customRuleEOF", "subRuleEOF", "head", "subRule", "subRuleIdentifier", 
			"arguments", "pair"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'@'", null, "'!'", "'::'", "'#'", null, "'('", null, null, 
			null, null, "'='", "')'", null, "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "AT", "ERR", "REVERSE", "DOUBLE_COLON", "HASHTAG", "NamespacedId", 
			"OPEN", "NBT", "WS_mSubRule", "RuleName", "Parameter", "EQUAL", "CLOSE", 
			"WS_mArgs", "COMMA", "WS_mArg", "Argument"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "RulesParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RulesParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class CustomRuleEOFContext extends ParserRuleContext {
		public HeadContext head() {
			return getRuleContext(HeadContext.class,0);
		}
		public SubRuleEOFContext subRuleEOF() {
			return getRuleContext(SubRuleEOFContext.class,0);
		}
		public CustomRuleEOFContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_customRuleEOF; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).enterCustomRuleEOF(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).exitCustomRuleEOF(this);
		}
	}

	public final CustomRuleEOFContext customRuleEOF() throws RecognitionException {
		CustomRuleEOFContext _localctx = new CustomRuleEOFContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_customRuleEOF);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			head();
			setState(15);
			subRuleEOF();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubRuleEOFContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(RulesParser.EOF, 0); }
		public List<SubRuleContext> subRule() {
			return getRuleContexts(SubRuleContext.class);
		}
		public SubRuleContext subRule(int i) {
			return getRuleContext(SubRuleContext.class,i);
		}
		public SubRuleEOFContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subRuleEOF; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).enterSubRuleEOF(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).exitSubRuleEOF(this);
		}
	}

	public final SubRuleEOFContext subRuleEOF() throws RecognitionException {
		SubRuleEOFContext _localctx = new SubRuleEOFContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_subRuleEOF);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(17);
				subRule();
				}
				}
				setState(20); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AT) | (1L << REVERSE) | (1L << DOUBLE_COLON) | (1L << HASHTAG) | (1L << NamespacedId))) != 0) );
			setState(22);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HeadContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(RulesParser.AT, 0); }
		public TerminalNode RuleName() { return getToken(RulesParser.RuleName, 0); }
		public HeadContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_head; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).enterHead(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).exitHead(this);
		}
	}

	public final HeadContext head() throws RecognitionException {
		HeadContext _localctx = new HeadContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_head);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(AT);
			setState(25);
			match(RuleName);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubRuleContext extends ParserRuleContext {
		public SubRuleIdentifierContext subRuleIdentifier() {
			return getRuleContext(SubRuleIdentifierContext.class,0);
		}
		public TerminalNode REVERSE() { return getToken(RulesParser.REVERSE, 0); }
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public SubRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).enterSubRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).exitSubRule(this);
		}
	}

	public final SubRuleContext subRule() throws RecognitionException {
		SubRuleContext _localctx = new SubRuleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_subRule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==REVERSE) {
				{
				setState(27);
				match(REVERSE);
				}
			}

			setState(30);
			subRuleIdentifier();
			setState(32);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN) {
				{
				setState(31);
				arguments();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubRuleIdentifierContext extends ParserRuleContext {
		public TerminalNode RuleName() { return getToken(RulesParser.RuleName, 0); }
		public TerminalNode AT() { return getToken(RulesParser.AT, 0); }
		public TerminalNode DOUBLE_COLON() { return getToken(RulesParser.DOUBLE_COLON, 0); }
		public TerminalNode NamespacedId() { return getToken(RulesParser.NamespacedId, 0); }
		public TerminalNode HASHTAG() { return getToken(RulesParser.HASHTAG, 0); }
		public TerminalNode NBT() { return getToken(RulesParser.NBT, 0); }
		public SubRuleIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subRuleIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).enterSubRuleIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).exitSubRuleIdentifier(this);
		}
	}

	public final SubRuleIdentifierContext subRuleIdentifier() throws RecognitionException {
		SubRuleIdentifierContext _localctx = new SubRuleIdentifierContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_subRuleIdentifier);
		int _la;
		try {
			setState(43);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
			case DOUBLE_COLON:
				enterOuterAlt(_localctx, 1);
				{
				setState(34);
				_la = _input.LA(1);
				if ( !(_la==AT || _la==DOUBLE_COLON) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(35);
				match(RuleName);
				}
				break;
			case HASHTAG:
			case NamespacedId:
				enterOuterAlt(_localctx, 2);
				{
				setState(37);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASHTAG) {
					{
					setState(36);
					match(HASHTAG);
					}
				}

				setState(39);
				match(NamespacedId);
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NBT) {
					{
					setState(40);
					match(NBT);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentsContext extends ParserRuleContext {
		public TerminalNode OPEN() { return getToken(RulesParser.OPEN, 0); }
		public List<PairContext> pair() {
			return getRuleContexts(PairContext.class);
		}
		public PairContext pair(int i) {
			return getRuleContext(PairContext.class,i);
		}
		public TerminalNode CLOSE() { return getToken(RulesParser.CLOSE, 0); }
		public List<TerminalNode> COMMA() { return getTokens(RulesParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(RulesParser.COMMA, i);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).exitArguments(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_arguments);
		int _la;
		try {
			setState(58);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(45);
				match(OPEN);
				setState(46);
				pair();
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(47);
					match(COMMA);
					setState(48);
					pair();
					}
					}
					setState(53);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(54);
				match(CLOSE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(56);
				match(OPEN);
				setState(57);
				match(CLOSE);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PairContext extends ParserRuleContext {
		public TerminalNode Parameter() { return getToken(RulesParser.Parameter, 0); }
		public TerminalNode EQUAL() { return getToken(RulesParser.EQUAL, 0); }
		public TerminalNode Argument() { return getToken(RulesParser.Argument, 0); }
		public PairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).enterPair(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RulesParserListener ) ((RulesParserListener)listener).exitPair(this);
		}
	}

	public final PairContext pair() throws RecognitionException {
		PairContext _localctx = new PairContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(Parameter);
			setState(61);
			match(EQUAL);
			setState(62);
			match(Argument);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\24C\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\2\3\3\6\3\25\n\3"+
		"\r\3\16\3\26\3\3\3\3\3\4\3\4\3\4\3\5\5\5\37\n\5\3\5\3\5\5\5#\n\5\3\6\3"+
		"\6\3\6\5\6(\n\6\3\6\3\6\5\6,\n\6\5\6.\n\6\3\7\3\7\3\7\3\7\7\7\64\n\7\f"+
		"\7\16\7\67\13\7\3\7\3\7\3\7\3\7\5\7=\n\7\3\b\3\b\3\b\3\b\3\b\2\2\t\2\4"+
		"\6\b\n\f\16\2\3\4\2\4\4\7\7\2C\2\20\3\2\2\2\4\24\3\2\2\2\6\32\3\2\2\2"+
		"\b\36\3\2\2\2\n-\3\2\2\2\f<\3\2\2\2\16>\3\2\2\2\20\21\5\6\4\2\21\22\5"+
		"\4\3\2\22\3\3\2\2\2\23\25\5\b\5\2\24\23\3\2\2\2\25\26\3\2\2\2\26\24\3"+
		"\2\2\2\26\27\3\2\2\2\27\30\3\2\2\2\30\31\7\2\2\3\31\5\3\2\2\2\32\33\7"+
		"\4\2\2\33\34\7\r\2\2\34\7\3\2\2\2\35\37\7\6\2\2\36\35\3\2\2\2\36\37\3"+
		"\2\2\2\37 \3\2\2\2 \"\5\n\6\2!#\5\f\7\2\"!\3\2\2\2\"#\3\2\2\2#\t\3\2\2"+
		"\2$%\t\2\2\2%.\7\r\2\2&(\7\b\2\2\'&\3\2\2\2\'(\3\2\2\2()\3\2\2\2)+\7\t"+
		"\2\2*,\7\13\2\2+*\3\2\2\2+,\3\2\2\2,.\3\2\2\2-$\3\2\2\2-\'\3\2\2\2.\13"+
		"\3\2\2\2/\60\7\n\2\2\60\65\5\16\b\2\61\62\7\22\2\2\62\64\5\16\b\2\63\61"+
		"\3\2\2\2\64\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\668\3\2\2\2\67\65\3"+
		"\2\2\289\7\20\2\29=\3\2\2\2:;\7\n\2\2;=\7\20\2\2</\3\2\2\2<:\3\2\2\2="+
		"\r\3\2\2\2>?\7\16\2\2?@\7\17\2\2@A\7\24\2\2A\17\3\2\2\2\n\26\36\"\'+-"+
		"\65<";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}