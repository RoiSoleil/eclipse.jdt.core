package org.eclipse.jdt.internal.compiler.parser;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import org.eclipse.jdt.internal.compiler.*;

/*Maps each terminal symbol in the java-grammar into a unique integer. 
This integer is used to represent the terminal when computing a parsing action. */

public interface TerminalSymbols {

	public final static int
	  TokenNameIdentifier = 5,
	  TokenNameabstract = 92,
	  TokenNameboolean = 18,
	  TokenNamebreak = 118,
	  TokenNamebyte = 19,
	  TokenNamecase = 211,
	  TokenNamecatch = 225,
	  TokenNamechar = 20,
	  TokenNameclass = 163,
	  TokenNamecontinue = 119,
	  TokenNamedefault = 212,
	  TokenNamedo = 120,
	  TokenNamedouble = 21,
	  TokenNameelse = 213,
	  TokenNameextends = 241,
	  TokenNamefalse = 37,
	  TokenNamefinal = 93,
	  TokenNamefinally = 226,
	  TokenNamefloat = 22,
	  TokenNamefor = 121,
	  TokenNameif = 122,
	  TokenNameimplements = 265,
	  TokenNameimport = 190,
	  TokenNameinstanceof = 65,
	  TokenNameint = 23,
	  TokenNameinterface = 184,
	  TokenNamelong = 24,
	  TokenNamenative = 94,
	  TokenNamenew = 33,
	  TokenNamenull = 38,
	  TokenNamepackage = 214,
	  TokenNameprivate = 95,
	  TokenNameprotected = 96,
	  TokenNamepublic = 97,
	  TokenNamereturn = 123,
	  TokenNameshort = 25,
	  TokenNamestatic = 98,
	  TokenNamestrictfp = 99,
	  TokenNamesuper = 35,
	  TokenNameswitch = 124,
	  TokenNamesynchronized = 84,
	  TokenNamethis = 36,
	  TokenNamethrow = 125,
	  TokenNamethrows = 227,
	  TokenNametransient = 100,
	  TokenNametrue = 39,
	  TokenNametry = 126,
	  TokenNamevoid = 26,
	  TokenNamevolatile = 101,
	  TokenNamewhile = 117,
	  TokenNameIntegerLiteral = 40,
	  TokenNameLongLiteral = 41,
	  TokenNameFloatingPointLiteral = 42,
	  TokenNameDoubleLiteral = 43,
	  TokenNameCharacterLiteral = 44,
	  TokenNameStringLiteral = 45,
	  TokenNamePLUS_PLUS = 1,
	  TokenNameMINUS_MINUS = 2,
	  TokenNameEQUAL_EQUAL = 32,
	  TokenNameLESS_EQUAL = 66,
	  TokenNameGREATER_EQUAL = 67,
	  TokenNameNOT_EQUAL = 34,
	  TokenNameLEFT_SHIFT = 13,
	  TokenNameRIGHT_SHIFT = 11,
	  TokenNameUNSIGNED_RIGHT_SHIFT = 12,
	  TokenNamePLUS_EQUAL = 166,
	  TokenNameMINUS_EQUAL = 167,
	  TokenNameMULTIPLY_EQUAL = 168,
	  TokenNameDIVIDE_EQUAL = 169,
	  TokenNameAND_EQUAL = 170,
	  TokenNameOR_EQUAL = 171,
	  TokenNameXOR_EQUAL = 172,
	  TokenNameREMAINDER_EQUAL = 173,
	  TokenNameLEFT_SHIFT_EQUAL = 174,
	  TokenNameRIGHT_SHIFT_EQUAL = 175,
	  TokenNameUNSIGNED_RIGHT_SHIFT_EQUAL = 176,
	  TokenNameOR_OR = 79,
	  TokenNameAND_AND = 78,
	  TokenNamePLUS = 3,
	  TokenNameMINUS = 4,
	  TokenNameNOT = 71,
	  TokenNameREMAINDER = 8,
	  TokenNameXOR = 63,
	  TokenNameAND = 62,
	  TokenNameMULTIPLY = 7,
	  TokenNameOR = 70,
	  TokenNameTWIDDLE = 72,
	  TokenNameDIVIDE = 9,
	  TokenNameGREATER = 68,
	  TokenNameLESS = 69,
	  TokenNameLPAREN = 10,
	  TokenNameRPAREN = 86,
	  TokenNameLBRACE = 109,
	  TokenNameRBRACE = 102,
	  TokenNameLBRACKET = 14,
	  TokenNameRBRACKET = 164,
	  TokenNameSEMICOLON = 64,
	  TokenNameQUESTION = 80,
	  TokenNameCOLON = 153,
	  TokenNameCOMMA = 88,
	  TokenNameDOT = 6,
	  TokenNameEQUAL = 165,
	  TokenNameEOF = 156,
	  TokenNameERROR = 304;
}
