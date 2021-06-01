package lexer.recognizers;

import lexer.states.*;
import lexer.tokens.*;

import java.util.ArrayList;

public class OperatorRecognizer extends Recognizer{

    public OperatorRecognizer(State curState, ArrayList<Token> tokenList) {
        super(curState, tokenList);
    }

    public boolean recognizeCompilerDirective(char symbol, String buffer) {
        if (symbol == '$') {
            curState.setState(StateType.COMPILER_DIRECTIVE_NEXT);
            return true;
        }
        curState.setState(StateType.COMMENT_OLD);
        return false;
    }

    public boolean recognizeCompilerDirectiveNext(char symbol, String buffer, boolean endOfFile) {
        if (symbol == '}') {
            buffer += symbol;
            tokenList.add(new Token(TokenType.DIRECTIVE, buffer));
            endOfLexeme();
        }
        if (isDigit(symbol) && buffer.length() == 2) {
            curState.setState(StateType.COMMENT_OLD);
            return false;
        } else if (endOfFile) {
            tokenList.add(new Token(TokenType.ERROR, buffer));
        }
        return true;
    }

    public boolean recognizeCommentNew(char symbol, String buffer, boolean endOfFile) {
        if (symbol == '}') {
            buffer += symbol;
            tokenList.add(new Token(TokenType.COMMENT, buffer));
            endOfLexeme();
        } else if (endOfFile) {
            tokenList.add(new Token(TokenType.ERROR, buffer));
        }
        return true;
    }

    public boolean recognizeCommentOld(char symbol) {
        if (symbol == '*') {
            curState.setState(StateType.COMMENT_NEXT);
            return true;
        }
        tokenList.add(new Token(TokenType.PUNCTUATION, Character.toString('(')));
        endOfLexeme();
        return false;
    }

    public boolean recognizeCommentNewNext(char symbol, String buffer, boolean endOfFile) {
        if (symbol == '*') {
            curState.setState(StateType.COMMENT_END);
            return false;
        } else if (endOfFile) {
            tokenList.add(new Token(TokenType.ERROR, buffer));
        }
        return true;
    }

    public boolean recognizeCommentNewEnd(char symbol, String buffer, boolean endOfFile) {
        if (buffer.toCharArray()[buffer.length() - 1] == '*' && symbol == ')') {
            buffer += symbol;
            tokenList.add(new Token(TokenType.COMMENT, buffer));
            endOfLexeme();
        } else if (endOfFile) {
            tokenList.add(new Token(TokenType.ERROR, buffer));
        }
        return true;
    }

    public boolean recognizeMultiplication(char symbol, String buffer) {
        if (symbol == '=' || symbol == '*') { // "*=" "**"
            buffer += symbol;
            tokenList.add(new Token(TokenType.OPERATOR, buffer));
            endOfLexeme();
            return true;
        }
        tokenList.add(new Token(TokenType.OPERATOR, buffer)); // "*"
        endOfLexeme();
        return false;
    }

    public boolean recognizeAssign(char symbol, String buffer) {
        if (symbol == '=') { // "+=", "-=", ":="
            buffer += symbol;
            tokenList.add(new Token(TokenType.OPERATOR, buffer));
            endOfLexeme();
            return true;
        }
        tokenList.add(new Token(TokenType.OPERATOR, buffer)); // "+", "-", ":"
        endOfLexeme();
        return false;
    }

    public boolean recognizeRelop(char symbol, String buffer) {
        if (symbol == '=') { // "<=", ">="
            buffer += symbol;
            tokenList.add(new Token(TokenType.OPERATOR, buffer));
            endOfLexeme();
            return true;
        }
        if (symbol == '<' || symbol == '>') { // "<<", "<>", ">>", "><"
            buffer += symbol;
            tokenList.add(new Token(TokenType.OPERATOR, buffer));
            endOfLexeme();
            return true;
        }
        tokenList.add(new Token(TokenType.OPERATOR, buffer)); // "<", ">"
        endOfLexeme();
        return false;
    }

    public boolean recognizeMinus(char symbol, String buffer) {
        if (isDigit(symbol)) {
            curState.setState(StateType.NUMBER);
            return false;
        }
        return recognizeAssign(symbol, buffer);
    }
}
