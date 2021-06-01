package lexer;

import lexer.recognizers.*;
import lexer.states.*;
import lexer.tokens.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private final String output;
    boolean endOfFile = false;
    State currentState = new State();
    ArrayList<Token> tokenList = new ArrayList<>();
    String buffer = "";

    private NumberRecognizer numberRecognizer = new NumberRecognizer(currentState, tokenList);
    private IdentifierRecognizer identifierRecognizer = new IdentifierRecognizer(currentState, tokenList);
    private OperatorRecognizer operatorRecognizer = new OperatorRecognizer(currentState, tokenList);

    public Lexer(String input, String output){
        this.input = input;
        this.output = output;
        readfile();
    }

    public void readfile(){
        currentState.setState(StateType.START);
        int currentSymbol;
        File file = new File(input);
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
            while((currentSymbol = fileReader.read()) != -1) {
                recognize((char)currentSymbol);
            }
        } catch (IOException e) {
            System.err.println("File error:" + e);
        }
        endOfFile = true;
        recognize(' ');

        output(output, tokenList);
    }

    public static void output(String path, List<Token> tokenList) {
        String defaultHtml = "<html><head><link href =\"style.css\" rel=\"stylesheet\" type=\"text/css\"></head><body>";
        for (Token token : tokenList) {
            if (token.getTokenType() == TokenType.PUNCTUATION && token.getContent().equals("\\n")) {
                System.out.println(token.getTokenType() + "\t\t\t\"" + token.getContent() + "\"");
                defaultHtml += "<br/>";
            } else {
                System.out.println(token.getTokenType() + "\t\t\t\"" + token.getContent() + "\"");
                defaultHtml += String.format("<pre class=\"%s\">%s</pre>", token.getTokenType(), token.getContent());
            }
        }
        defaultHtml += "</body></html>";

        File file = new File(path);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(defaultHtml);
            fileWriter.close();

        } catch (IOException ignored) {
        }
    }

    public void recognize(char symbol){
        boolean symbolChecked = false;

        while (!symbolChecked) {
            switch (currentState.getState()) {
                case START: {
                    buffer = "";
                    if (Character.isDigit(symbol)) {
                        currentState.setState(StateType.NUMBER);
                        numberRecognizer = new NumberRecognizer(currentState, tokenList);
                    }
                    else if (Character.isLetter(symbol)) {
                        currentState.setState(StateType.IDENTIFIER);
                        identifierRecognizer = new IdentifierRecognizer(currentState, tokenList);
                    }
                    else if(lexer.recognizers.Recognizer.isOperator(symbol) || symbol == '=') {
                        tokenList.add(new Token(TokenType.OPERATOR, Character.toString(symbol)));
                    }
                    else if(symbol == '$'){
                        currentState.setState(StateType.HEX_NUM);
                        numberRecognizer = new NumberRecognizer(currentState, tokenList);
                    }
                    else if(symbol == ':' || symbol == '+' || symbol == '/'){
                         currentState.setState(StateType.ASSIGN);
                         operatorRecognizer = new OperatorRecognizer(currentState, tokenList);
                    }
                    else if( symbol == '-' ){
                        currentState.setState(StateType.MINUS);
                    }
                    else if(symbol == '*'){
                        currentState.setState(StateType.MULTIPLY);
                        operatorRecognizer = new OperatorRecognizer(currentState, tokenList);
                    }

                    else if(symbol == '{'){
                        currentState.setState(StateType.COMPILER_DIRECTIVE);
                        operatorRecognizer = new OperatorRecognizer(currentState, tokenList);
                    }
                    else if(symbol == '('){
                        currentState.setState(StateType.COMMENT_NEW);
                        operatorRecognizer = new OperatorRecognizer(currentState, tokenList);
                    }
                    else if(symbol == '>' || symbol == '<'){
                        currentState.setState(StateType.RELOP);
                        operatorRecognizer = new OperatorRecognizer(currentState, tokenList);
                    }
                    else if (symbol == '\"' || symbol == '\'')
                        currentState.setState(StateType.STRINGLITERAL);
                    else {
                        identifierRecognizer = new IdentifierRecognizer(currentState, tokenList);
                        identifierRecognizer.checkTerminateSymbol(symbol);
                    }

                    symbolChecked = true;
                }
                break;
                case IND_ERROR:{
                    symbolChecked = identifierRecognizer.recognizeErrInd(symbol, buffer);
                }
                break;
                case HEX_NUM: {
                    symbolChecked = numberRecognizer.recognizeHexNum(symbol, buffer);
                }
                break;

                case NUM_E: {
                    symbolChecked = numberRecognizer.recognizeNumE(symbol, buffer);
                }
                break;
                case NUM_E_NEXT: {
                    symbolChecked = numberRecognizer.recognizeNumENext(symbol, buffer);
                }
                break;

                case MINUS: {
                    symbolChecked = operatorRecognizer.recognizeMinus(symbol, buffer);
                }
                break;

                case RELOP: {
                    symbolChecked = operatorRecognizer.recognizeRelop(symbol, buffer);
                }
                break;

                case MULTIPLY: {
                    symbolChecked = operatorRecognizer.recognizeMultiplication(symbol, buffer);
                }
                break;

                case STRINGLITERAL: {
                    symbolChecked = identifierRecognizer.recognizeStringLiteral(symbol, buffer);
                }
                break;

                case COMPILER_DIRECTIVE: {
                    symbolChecked = operatorRecognizer.recognizeCompilerDirective(symbol, buffer);
                }
                break;

                case COMPILER_DIRECTIVE_NEXT: {
                    symbolChecked = operatorRecognizer.recognizeCompilerDirectiveNext(symbol, buffer, endOfFile);
                }
                break;

                case COMMENT_OLD: {
                    symbolChecked = operatorRecognizer.recognizeCommentNew(symbol, buffer, endOfFile);
                }
                break;

                case COMMENT_NEW: {
                    symbolChecked = operatorRecognizer.recognizeCommentOld(symbol);
                }
                break;

                case COMMENT_END: {
                    symbolChecked = operatorRecognizer.recognizeCommentNewEnd(symbol, buffer, endOfFile);
                }
                break;

                case COMMENT_NEXT: {
                    symbolChecked = operatorRecognizer.recognizeCommentNewNext(symbol, buffer, endOfFile);
                }
                break;

                case ASSIGN: {
                    symbolChecked = operatorRecognizer.recognizeAssign(symbol, buffer);
                }
                break;

                case NUMBER: {
                    symbolChecked = numberRecognizer.recognizeNumber(symbol, buffer);
                }
                break;

                case FRACTIONAL_NUM: {
                    symbolChecked = numberRecognizer.recognizeFractionalNum(symbol, buffer);
                }
                break;

                case FRACTIONAL_NUM_AFTER_DOT: {
                    symbolChecked = numberRecognizer.recognizeFractionalNumAfterDot(symbol, buffer);
                }
                break;

                case IDENTIFIER: {
                    symbolChecked = identifierRecognizer.recognizeIdentifier(symbol, buffer);
                }
                break;

                case ERROR: {
                    symbolChecked = numberRecognizer.recognizeError(symbol, buffer);
                }
                default: {
                    System.out.println("There is no such state");
                }
                break;
            }
        }
        if(currentState.getState() != StateType.START){buffer += symbol;}
    }

}
