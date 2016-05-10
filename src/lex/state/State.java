package lex.state;

import java.util.List;

import exception.SyntaxesException;
import lex.Location;
import lex.Token;
import lex.TokenBuilder;

public abstract class State {

    public static final Start START = new Start();
    public static final Number NUMBER = new Number();
    public static final Comment COMMENT = new Comment();

    public static final DoubleOperator LESS = new DoubleOperator('<', '>', '=');
    public static final DoubleOperator GREATER = new DoubleOperator('>', '=');
    public static final DoubleOperator EQUAL = new DoubleOperator('=', '=');
    public static final DoubleOperator AND = new DoubleOperator('&', '&');
    public static final DoubleOperator OR = new DoubleOperator('|', '|');

    public static final SimpleString SIMPLE_STRING = new SimpleString();

    public static final DoubleQuotedString DQ_STRING = new DoubleQuotedString();
    public static final SingleQuotedString SQ_STRING = new SingleQuotedString();

    public static final EscInStrState ESC_IN_DQ_STRING = new EscInStrState(DQ_STRING);
    public static final EscInStrState ESC_IN_SQ_STRING = new EscInStrState(SQ_STRING);

    public abstract State nextState(char symbol, List<Token> output, TokenBuilder builder, Location location) throws SyntaxesException;
}
