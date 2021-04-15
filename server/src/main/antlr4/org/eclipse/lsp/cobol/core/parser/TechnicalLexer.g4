/*
 * Copyright (c) 2021 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Broadcom, Inc. - initial API and implementation
 */

lexer grammar TechnicalLexer;

// This grammar should not contains any explicit token declaration.
// There should be only symbols, literal patterns, and fragments.
// The purpose of this file is to allow similar parsing by the preprocessor and parser.
// All the token declarations that are common to several lexers should belong to CommonCobolLexer.

@lexer::members {
    boolean sqlFlag = false;
    String lastTokenText = null;
    public void emit(Token token) {
        super.emit(token);
        lastTokenText = token.getText();
    }
}
// symbols
AMPCHAR : '&';
ASTERISKCHAR : '*';
DOUBLEASTERISKCHAR : '**';
COLONCHAR : ':';
COMMA_EOF : ',' EOF {!sqlFlag}? ->skip;
COMMA_LB : ',' ('\r' | '\n' | '\f' | '\t' | ' ')+ {!sqlFlag}? -> channel(HIDDEN);
COMMACHAR : ',';
COMMENTTAG : '*>';
COMMENTENTRYTAG : '*>CE';
DOLLARCHAR : '$';
DOUBLEQUOTE : '"';

// period full stopPosition
DOT_FS : '.' ('\r' | '\n' | '\f' | '\t' | ' ')+ | '.' EOF;
DOT : '.';
EQUALCHAR : '=';
LESSTHANCHAR : '<';
LESSTHANOREQUAL : '<=';
LPARENCHAR : '(';
MINUSCHAR : '-';
MORETHANCHAR : '>';
MORETHANOREQUAL : '>=';
NOTEQUALCHAR : '<>';
PLUSCHAR : '+';
SEMICOLON : ';';
SEMICOLON_FS : ';' ('\r' | '\n' | '\f' | '\t' | ' ')+ | ';' EOF;
SINGLEQUOTE : '\'';
RPARENCHAR : ')';
SLASHCHAR : '/';
SQLLINECOMMENTCHAR: '--';

LEVEL_NUMBER : ([1-9])|([0][1-9])|([1234][0-9]);
LEVEL_NUMBER_66 : '66';
LEVEL_NUMBER_77 : '77';
LEVEL_NUMBER_88 : '88';

INTEGERLITERAL : (PLUSCHAR | MINUSCHAR)? DIGIT+ | LEVEL_NUMBER;

// DECIMAL_CONST : DIGIT DIGIT DIGIT DIGIT DOT DIGIT {sqlFlag}? ;
SINGLEDIGITLITERAL : DIGIT {sqlFlag}? ;

NUMERICLITERAL : (PLUSCHAR | MINUSCHAR)? DIGIT* (DOT | COMMACHAR) DIGIT+ (('e' | 'E') (PLUSCHAR | MINUSCHAR)? DIGIT+)?;

NONNUMERICLITERAL : UNTRMSTRINGLITERAL | STRINGLITERAL | DBCSLITERAL | HEXNUMBER | NULLTERMINATED;

//TXTLITERAL : STRINGLITERAL | IDENTIFIER;
CHAR_STRING_CONSTANT : HEXNUMBER | STRINGLITERAL;

IDENTIFIER : ([a-zA-Z0-9]+ [-_a-zA-Z0-9]*);
COPYBOOK_IDENTIFIER : ([a-zA-Z0-9#@$]+ [-_a-zA-Z0-9#@$]*);
FILENAME : IDENTIFIER+ '.' IDENTIFIER+;

OCTDIGITS : OCT_DIGIT {sqlFlag}? ;
HEX_NUMBERS : HEXNUMBER {sqlFlag}? ;

// whitespace, line breaks, comments, ...
NEWLINE : '\r'? '\n' -> channel(HIDDEN);
COMMENTLINE : COMMENTTAG WS ~('\n' | '\r')* -> channel(HIDDEN);
COMMENTENTRYLINE : COMMENTENTRYTAG WS ~('\n' | '\r')*  -> channel(HIDDEN);
WS : [ \t\f;]+ -> channel(HIDDEN);
SEPARATOR : ', ' {!sqlFlag}? -> channel(HIDDEN);

//SQL comments
SQLLINECOMMENT
	:	SQLLINECOMMENTCHAR ~[\r\n]* NEWLINE {sqlFlag}? -> channel(HIDDEN)
	;

// treat all the non-processed tokens as errors
ERRORCHAR : . ;

ZERO_DIGIT: '0';


fragment HEXNUMBER :
	X '"' [0-9A-F]+ '"'
	| X '\'' [0-9A-F]+ '\''
;

fragment NULLTERMINATED :
	Z '"' (~["\n\r] | '""' | '\'')* '"'
	| Z '\'' (~['\n\r] | '\'\'' | '"')* '\''
;

fragment STRINGLITERAL :
	'"' (~["\n\r] | '""' | '\'')* '"'
	| '\'' (~['\n\r] | '\'\'' | '"')* '\''
;

fragment UNTRMSTRINGLITERAL :
	'"' (~["\n\r] | '""' | '\'')*
	| '\'' (~['\n\r] | '\'\'' | '"')*
;

fragment DBCSLITERAL :
	[GN] '"' (~["\n\r] | '""' | '\'')* '"'
	| [GN] '\'' (~['\n\r] | '\'\'' | '"')* '\''
;

fragment
OCT_DIGIT        : [0-8] ;
fragment DIGIT: OCT_DIGIT | [9];
// case insensitive chars
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');