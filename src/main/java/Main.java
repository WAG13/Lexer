import lexer.Lexer;

public class Main {

    public static void main(String[] args) {
        String inputPath = "input.txt";
        String outputPath = "output.html";
        Lexer Lexer = new Lexer(inputPath, outputPath);
    }
}
