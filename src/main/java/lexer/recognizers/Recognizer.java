package lexer.recognizers;

import lexer.states.State;
import lexer.states.StateType;
import lexer.tokens.Token;

import java.util.ArrayList;

public class Recognizer {
    public final State curState;
    public final ArrayList<Token> tokenList;

    public Recognizer(State curState, ArrayList<Token> tokenList){
        this.curState = curState;
        this.tokenList = tokenList;
    }

    public void endOfLexeme(){
        curState.setState(StateType.START);
    }

    public static boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <='Z') || c == '_';
    }

    public static boolean isDigit(char c){
        return (c >= '0' && c <= '9');
    }

    public static boolean isOperator(char c){
        return (c == '#'
                || c == '@' || c == '^' ); //for pointers
    }

    public static boolean isValidHex(char c){
        return (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <='F') ||
                (c >= '0' && c <='9') ;
    }
}
