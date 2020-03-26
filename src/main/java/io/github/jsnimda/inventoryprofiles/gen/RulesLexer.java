// Generated from src/main/java/io/github/jsnimda/inventoryprofiles/parser/antlr/RulesLexer.g4 by ANTLR 4.8
package io.github.jsnimda.inventoryprofiles.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RulesLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, AT=2, ERR=3, REVERSE=4, DOUBLE_COLON=5, HASHTAG=6, ItemName=7, OPEN=8, 
		NBT=9, WS_mRuleEntry=10, RuleName=11, Parameter=12, EQUAL=13, CLOSE=14, 
		WS_mArgs=15, COMMA=16, WS_mArg=17, Argument=18;
	public static final int
		mDeclareName=1, mRuleEntry=2, mRuleName=3, mArgs=4, mArg=5;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "mDeclareName", "mRuleEntry", "mRuleName", "mArgs", "mArg"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"WS", "AT", "ERR", "ID", "DeclareName", "ERR_mDeclareName", "REVERSE", 
			"AT_mRuleEntry", "DOUBLE_COLON", "HASHTAG", "ItemName", "OPEN", "NBT", 
			"WS_mRuleEntry", "ERR_mRuleEntry", "RuleName", "ERR_mRule", "Parameter", 
			"EQUAL", "CLOSE", "WS_mArgs", "ERR_mArgs", "CLOSE_mArg", "COMMA", "WS_mArg", 
			"Argument", "ERR_mArg", "COMMON", "NON_NESTED", "NESTED", "STRING", "STRING_DOUBLE", 
			"STRING_SINGLE", "ESC_DOUBLE", "ESC_SINGLE", "UNICODE", "HEX", "SAFECODEPOINT_DOUBLE", 
			"SAFECODEPOINT_SINGLE"
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
			null, "WS", "AT", "ERR", "REVERSE", "DOUBLE_COLON", "HASHTAG", "ItemName", 
			"OPEN", "NBT", "WS_mRuleEntry", "RuleName", "Parameter", "EQUAL", "CLOSE", 
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


	public RulesLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "RulesLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\24\u0127\b\1\b\1"+
		"\b\1\b\1\b\1\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t"+
		"\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20"+
		"\t\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27"+
		"\t\27\4\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36"+
		"\t\36\4\37\t\37\4 \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4"+
		"(\t(\3\2\6\2X\n\2\r\2\16\2Y\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\5\6\5e\n"+
		"\5\r\5\16\5f\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\5\f"+
		"\u0087\n\f\3\r\3\r\3\r\3\r\3\16\3\16\7\16\u008f\n\16\f\16\16\16\u0092"+
		"\13\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25"+
		"\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\33\6\33\u00c7\n\33\r\33"+
		"\16\33\u00c8\3\34\3\34\3\34\3\34\3\35\3\35\3\35\7\35\u00d2\n\35\f\35\16"+
		"\35\u00d5\13\35\3\35\3\35\3\35\7\35\u00da\n\35\f\35\16\35\u00dd\13\35"+
		"\3\35\3\35\3\35\7\35\u00e2\n\35\f\35\16\35\u00e5\13\35\3\35\5\35\u00e8"+
		"\n\35\3\36\3\36\6\36\u00ec\n\36\r\36\16\36\u00ed\5\36\u00f0\n\36\3\37"+
		"\3\37\6\37\u00f4\n\37\r\37\16\37\u00f5\5\37\u00f8\n\37\3 \3 \5 \u00fc"+
		"\n \3!\3!\3!\7!\u0101\n!\f!\16!\u0104\13!\3!\3!\3\"\3\"\3\"\7\"\u010b"+
		"\n\"\f\"\16\"\u010e\13\"\3\"\3\"\3#\3#\3#\5#\u0115\n#\3$\3$\3$\5$\u011a"+
		"\n$\3%\3%\3%\3%\3%\3%\3&\3&\3\'\3\'\3(\3(\2\2)\b\3\n\4\f\5\16\2\20\2\22"+
		"\2\24\6\26\2\30\7\32\b\34\t\36\n \13\"\f$\2&\r(\2*\16,\17.\20\60\21\62"+
		"\2\64\2\66\228\23:\24<\2>\2@\2B\2D\2F\2H\2J\2L\2N\2P\2R\2T\2\b\2\3\4\5"+
		"\6\7\13\f\2\13\17\"\"\u0087\u0087\u00a2\u00a2\u1682\u1682\u2002\u200c"+
		"\u202a\u202b\u2031\u2031\u2061\u2061\u3002\u3002\6\2\62;C\\aac|\t\2$$"+
		")+..]]__}}\177\177\b\2$$)+]]__}}\177\177\n\2$$\61\61^^ddhhppttvv\n\2)"+
		")\61\61^^ddhhppttvv\5\2\62;CHch\5\2\2!$$^^\5\2\2!))^^\2\u012a\2\b\3\2"+
		"\2\2\2\n\3\2\2\2\2\f\3\2\2\2\3\20\3\2\2\2\3\22\3\2\2\2\4\24\3\2\2\2\4"+
		"\26\3\2\2\2\4\30\3\2\2\2\4\32\3\2\2\2\4\34\3\2\2\2\4\36\3\2\2\2\4 \3\2"+
		"\2\2\4\"\3\2\2\2\4$\3\2\2\2\5&\3\2\2\2\5(\3\2\2\2\6*\3\2\2\2\6,\3\2\2"+
		"\2\6.\3\2\2\2\6\60\3\2\2\2\6\62\3\2\2\2\7\64\3\2\2\2\7\66\3\2\2\2\78\3"+
		"\2\2\2\7:\3\2\2\2\7<\3\2\2\2\bW\3\2\2\2\n]\3\2\2\2\fa\3\2\2\2\16d\3\2"+
		"\2\2\20h\3\2\2\2\22m\3\2\2\2\24q\3\2\2\2\26s\3\2\2\2\30x\3\2\2\2\32}\3"+
		"\2\2\2\34\u0086\3\2\2\2\36\u0088\3\2\2\2 \u008c\3\2\2\2\"\u0095\3\2\2"+
		"\2$\u0099\3\2\2\2&\u009d\3\2\2\2(\u00a1\3\2\2\2*\u00a5\3\2\2\2,\u00a7"+
		"\3\2\2\2.\u00ab\3\2\2\2\60\u00af\3\2\2\2\62\u00b3\3\2\2\2\64\u00b7\3\2"+
		"\2\2\66\u00bd\3\2\2\28\u00c1\3\2\2\2:\u00c6\3\2\2\2<\u00ca\3\2\2\2>\u00e7"+
		"\3\2\2\2@\u00ef\3\2\2\2B\u00f7\3\2\2\2D\u00fb\3\2\2\2F\u00fd\3\2\2\2H"+
		"\u0107\3\2\2\2J\u0111\3\2\2\2L\u0116\3\2\2\2N\u011b\3\2\2\2P\u0121\3\2"+
		"\2\2R\u0123\3\2\2\2T\u0125\3\2\2\2VX\t\2\2\2WV\3\2\2\2XY\3\2\2\2YW\3\2"+
		"\2\2YZ\3\2\2\2Z[\3\2\2\2[\\\b\2\2\2\\\t\3\2\2\2]^\7B\2\2^_\3\2\2\2_`\b"+
		"\3\3\2`\13\3\2\2\2ab\13\2\2\2b\r\3\2\2\2ce\t\3\2\2dc\3\2\2\2ef\3\2\2\2"+
		"fd\3\2\2\2fg\3\2\2\2g\17\3\2\2\2hi\5\16\5\2ij\3\2\2\2jk\b\6\4\2kl\b\6"+
		"\5\2l\21\3\2\2\2mn\13\2\2\2no\3\2\2\2op\b\7\6\2p\23\3\2\2\2qr\7#\2\2r"+
		"\25\3\2\2\2st\7B\2\2tu\3\2\2\2uv\b\t\7\2vw\b\t\b\2w\27\3\2\2\2xy\7<\2"+
		"\2yz\7<\2\2z{\3\2\2\2{|\b\n\b\2|\31\3\2\2\2}~\7%\2\2~\177\3\2\2\2\177"+
		"\u0080\b\13\b\2\u0080\33\3\2\2\2\u0081\u0082\5\16\5\2\u0082\u0083\7<\2"+
		"\2\u0083\u0084\5\16\5\2\u0084\u0087\3\2\2\2\u0085\u0087\5\16\5\2\u0086"+
		"\u0081\3\2\2\2\u0086\u0085\3\2\2\2\u0087\35\3\2\2\2\u0088\u0089\7*\2\2"+
		"\u0089\u008a\3\2\2\2\u008a\u008b\b\r\t\2\u008b\37\3\2\2\2\u008c\u0090"+
		"\7}\2\2\u008d\u008f\5B\37\2\u008e\u008d\3\2\2\2\u008f\u0092\3\2\2\2\u0090"+
		"\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0093\3\2\2\2\u0092\u0090\3\2"+
		"\2\2\u0093\u0094\7\177\2\2\u0094!\3\2\2\2\u0095\u0096\5\b\2\2\u0096\u0097"+
		"\3\2\2\2\u0097\u0098\b\17\2\2\u0098#\3\2\2\2\u0099\u009a\13\2\2\2\u009a"+
		"\u009b\3\2\2\2\u009b\u009c\b\20\6\2\u009c%\3\2\2\2\u009d\u009e\5\16\5"+
		"\2\u009e\u009f\3\2\2\2\u009f\u00a0\b\21\n\2\u00a0\'\3\2\2\2\u00a1\u00a2"+
		"\13\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\b\22\6\2\u00a4)\3\2\2\2\u00a5"+
		"\u00a6\5\16\5\2\u00a6+\3\2\2\2\u00a7\u00a8\7?\2\2\u00a8\u00a9\3\2\2\2"+
		"\u00a9\u00aa\b\24\13\2\u00aa-\3\2\2\2\u00ab\u00ac\7+\2\2\u00ac\u00ad\3"+
		"\2\2\2\u00ad\u00ae\b\25\n\2\u00ae/\3\2\2\2\u00af\u00b0\5\b\2\2\u00b0\u00b1"+
		"\3\2\2\2\u00b1\u00b2\b\26\2\2\u00b2\61\3\2\2\2\u00b3\u00b4\13\2\2\2\u00b4"+
		"\u00b5\3\2\2\2\u00b5\u00b6\b\27\6\2\u00b6\63\3\2\2\2\u00b7\u00b8\7+\2"+
		"\2\u00b8\u00b9\3\2\2\2\u00b9\u00ba\b\30\f\2\u00ba\u00bb\b\30\n\2\u00bb"+
		"\u00bc\b\30\n\2\u00bc\65\3\2\2\2\u00bd\u00be\7.\2\2\u00be\u00bf\3\2\2"+
		"\2\u00bf\u00c0\b\31\n\2\u00c0\67\3\2\2\2\u00c1\u00c2\5\b\2\2\u00c2\u00c3"+
		"\3\2\2\2\u00c3\u00c4\b\32\2\2\u00c49\3\2\2\2\u00c5\u00c7\5@\36\2\u00c6"+
		"\u00c5\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2"+
		"\2\2\u00c9;\3\2\2\2\u00ca\u00cb\13\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00cd"+
		"\b\34\6\2\u00cd=\3\2\2\2\u00ce\u00e8\5D \2\u00cf\u00d3\7*\2\2\u00d0\u00d2"+
		"\5B\37\2\u00d1\u00d0\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3"+
		"\u00d4\3\2\2\2\u00d4\u00d6\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d6\u00e8\7+"+
		"\2\2\u00d7\u00db\7]\2\2\u00d8\u00da\5B\37\2\u00d9\u00d8\3\2\2\2\u00da"+
		"\u00dd\3\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\u00de\3\2"+
		"\2\2\u00dd\u00db\3\2\2\2\u00de\u00e8\7_\2\2\u00df\u00e3\7}\2\2\u00e0\u00e2"+
		"\5B\37\2\u00e1\u00e0\3\2\2\2\u00e2\u00e5\3\2\2\2\u00e3\u00e1\3\2\2\2\u00e3"+
		"\u00e4\3\2\2\2\u00e4\u00e6\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e6\u00e8\7\177"+
		"\2\2\u00e7\u00ce\3\2\2\2\u00e7\u00cf\3\2\2\2\u00e7\u00d7\3\2\2\2\u00e7"+
		"\u00df\3\2\2\2\u00e8?\3\2\2\2\u00e9\u00f0\5>\35\2\u00ea\u00ec\n\4\2\2"+
		"\u00eb\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ee"+
		"\3\2\2\2\u00ee\u00f0\3\2\2\2\u00ef\u00e9\3\2\2\2\u00ef\u00eb\3\2\2\2\u00f0"+
		"A\3\2\2\2\u00f1\u00f8\5>\35\2\u00f2\u00f4\n\5\2\2\u00f3\u00f2\3\2\2\2"+
		"\u00f4\u00f5\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f8"+
		"\3\2\2\2\u00f7\u00f1\3\2\2\2\u00f7\u00f3\3\2\2\2\u00f8C\3\2\2\2\u00f9"+
		"\u00fc\5H\"\2\u00fa\u00fc\5F!\2\u00fb\u00f9\3\2\2\2\u00fb\u00fa\3\2\2"+
		"\2\u00fcE\3\2\2\2\u00fd\u0102\7$\2\2\u00fe\u0101\5J#\2\u00ff\u0101\5R"+
		"\'\2\u0100\u00fe\3\2\2\2\u0100\u00ff\3\2\2\2\u0101\u0104\3\2\2\2\u0102"+
		"\u0100\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0105\3\2\2\2\u0104\u0102\3\2"+
		"\2\2\u0105\u0106\7$\2\2\u0106G\3\2\2\2\u0107\u010c\7)\2\2\u0108\u010b"+
		"\5L$\2\u0109\u010b\5T(\2\u010a\u0108\3\2\2\2\u010a\u0109\3\2\2\2\u010b"+
		"\u010e\3\2\2\2\u010c\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010f\3\2"+
		"\2\2\u010e\u010c\3\2\2\2\u010f\u0110\7)\2\2\u0110I\3\2\2\2\u0111\u0114"+
		"\7^\2\2\u0112\u0115\t\6\2\2\u0113\u0115\5N%\2\u0114\u0112\3\2\2\2\u0114"+
		"\u0113\3\2\2\2\u0115K\3\2\2\2\u0116\u0119\7^\2\2\u0117\u011a\t\7\2\2\u0118"+
		"\u011a\5N%\2\u0119\u0117\3\2\2\2\u0119\u0118\3\2\2\2\u011aM\3\2\2\2\u011b"+
		"\u011c\7w\2\2\u011c\u011d\5P&\2\u011d\u011e\5P&\2\u011e\u011f\5P&\2\u011f"+
		"\u0120\5P&\2\u0120O\3\2\2\2\u0121\u0122\t\b\2\2\u0122Q\3\2\2\2\u0123\u0124"+
		"\n\t\2\2\u0124S\3\2\2\2\u0125\u0126\n\n\2\2\u0126U\3\2\2\2\34\2\3\4\5"+
		"\6\7Yf\u0086\u0090\u00c8\u00d3\u00db\u00e3\u00e7\u00ed\u00ef\u00f5\u00f7"+
		"\u00fb\u0100\u0102\u010a\u010c\u0114\u0119\r\b\2\2\4\3\2\t\r\2\4\4\2\t"+
		"\5\2\t\4\2\7\5\2\7\6\2\6\2\2\7\7\2\t\20\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}