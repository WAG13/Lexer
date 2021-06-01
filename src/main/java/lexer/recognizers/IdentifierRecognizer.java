package lexer.recognizers;

import lexer.states.*;
import lexer.tokens.*;

import java.util.ArrayList;

public class IdentifierRecognizer extends Recognizer{

    public IdentifierRecognizer(State curState, ArrayList<Token> tokenList){
        super(curState, tokenList);
    }

    public void endOfLexeme(){
        curState.setState(StateType.START);
    }

    public boolean recognizeIdentifier(char symbol, String buffer){
        if (symbol == '.'){
           if (buffer.toLowerCase().equals("end")){
            tokenList.add(new Token(TokenType.IDENTIFIER, buffer));
            tokenList.add(new Token(TokenType.PUNCTUATION, Character.toString(symbol)));
            endOfLexeme();
            return true;
           }
           if (buffer.toCharArray()[buffer.length() - 1] == '.'){
               buffer = buffer.substring(0, buffer.length() - 1);
               String str = "..";
               tokenList.add(new Token(TokenType.IDENTIFIER, buffer));
               tokenList.add(new Token(TokenType.PUNCTUATION, str));
               endOfLexeme();
               return true;
           }
           return true;
        }
        else if (!isLetter(symbol) && !(isDigit(symbol)) && symbol !='.'){
            if(LangPatterns.isPunctuation(symbol)){
                tokenList.add(new Token(TokenType.IDENTIFIER, buffer));
                endOfLexeme();
                return false;
            }
            else {
                tokenList.add(new Token(TokenType.IDENTIFIER, buffer));
                tokenList.add(new Token(TokenType.ERROR,Character.toString(symbol)));
                endOfLexeme();
              return true;
            }
        }
        else return true;
    }
    public boolean recognizeErrInd(char symbol, String buffer){
        if (LangPatterns.isPunctuation(symbol)){
            tokenList.add(new Token(TokenType.ERROR, buffer));
            tokenList.add(new Token(TokenType.PUNCTUATION, Character.toString(symbol)));
            endOfLexeme();
            return true;
        }
       else return true;

    }
    public void checkTerminateSymbol(char character) {
        if (LangPatterns.isPunctuation(character))
            tokenList.add(new Token(TokenType.PUNCTUATION, Character.toString(character)));
        else
            tokenList.add(new Token(TokenType.ERROR, Character.toString(character)));
        curState.setState(StateType.START);
    }

   public boolean recognizeStringLiteral(char symbol, String buffer){
       if (symbol == '\r' || symbol == '\n') {
           tokenList.add(new Token(TokenType.ERROR, buffer));
           endOfLexeme();
           return true;
       } else if (symbol == '\"' || symbol == '\'') {
           tokenList.add(new Token(TokenType.LITERAL, buffer + symbol));
           endOfLexeme();
           return true;
       } else {

          return true;
       }
   }
}
