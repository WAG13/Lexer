package lexer.recognizers;

import lexer.states.*;
import lexer.tokens.*;

import java.util.ArrayList;

public class NumberRecognizer extends Recognizer {

    public NumberRecognizer(State curState, ArrayList<Token> tokenList){
        super(curState, tokenList);
    }

    public void checkEndOfNum(char symbol, String buffer){
        if(LangPatterns.isPunctuation(symbol)){
           tokenList.add(new Token(TokenType.NUMBER, buffer));
           endOfLexeme();
        }
        else curState.setState(StateType.ERROR);
    }

    public boolean recognizeNumber(char symbol,  String buffer){
        if(symbol == '.') {
            curState.setState(StateType.FRACTIONAL_NUM);
            return true;
        }
        if(symbol == 'e' || symbol == 'E') {
            curState.setState(StateType.NUM_E);
            return true;
        }
        if(!isDigit(symbol)){
            checkEndOfNum(symbol, buffer);
            return false;
        }
        else return true;
    }

    public boolean recognizeNumE(char symbol, String buffer){
        if(!isDigit(symbol)){
            if(symbol == '+' || symbol == '-'){
                curState.setState(StateType.NUM_E_NEXT); return true;
            }
           else {
               checkEndOfNum(symbol, buffer);
               return false;
           }
        }
        else return true;
    }
    public boolean recognizeNumENext(char symbol, String buffer){
        if(!isDigit(symbol)){
            checkEndOfNum(symbol, buffer);
            return false;
        }
        else return true;
    }


    public boolean recognizeFractionalNum(char symbol, String buffer){
        if(isDigit(symbol)){ curState.setState(StateType.FRACTIONAL_NUM_AFTER_DOT); return true;}
        if (symbol == '.'){
                buffer = buffer.substring(0, buffer.length() - 1);
                String str = "..";
                tokenList.add(new Token(TokenType.NUMBER, buffer));
                tokenList.add(new Token(TokenType.PUNCTUATION, str));
                endOfLexeme();
                return true;
        }
        else {
            curState.setState(StateType.ERROR); return true;
        }
    }

    public boolean recognizeFractionalNumAfterDot(char symbol, String buffer){
        if(!isDigit(symbol)){
            if(symbol == 'e' || symbol == 'E') {
                curState.setState(StateType.NUM_E);
                return true;
            }
            else {
                checkEndOfNum(symbol, buffer);
                return false;
            }
        }
        else return true;
    }

    public boolean recognizeHexNum(char symbol, String buffer){
        if(!(isValidHex(symbol)) || buffer.length()>8){
            checkEndOfNum(symbol, buffer);
            return false;
        }
        else return true;
    }

    public boolean recognizeError(char symbol, String buffer){
        if (LangPatterns.isPunctuation(symbol)){
           tokenList.add(new Token(TokenType.ERROR, buffer));
           endOfLexeme();
           return false;
        }
        else{ curState.setState(StateType.ERROR);return true;}
    }
}
