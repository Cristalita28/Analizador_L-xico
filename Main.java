import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // ¿El usuario pasó un archivo como argumento?
        if (args.length < 1) {
            System.out.println("Uso: java Main <archivo>");
            System.out.println("Ejemplo: java Main programa.txt");
            return;
        }

        String filename = args[0];
        String source;

        // Intentamos leer el archivo
        try {
            source = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            System.out.println("Error: No se pudo abrir el archivo '" + filename + "'");
            return;
        }

        // Creamos el lexer y generamos los tokens
        System.out.println("=== Analizador Léxico ===");
        System.out.println("Archivo: " + filename);
        System.out.println("========================\n");

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        // Imprimimos cada token encontrado
        int errors = 0;
        for (Token token : tokens) {
            if (token.type == TokenType.ERROR) {
                System.out.println("[ERROR] " + token);
                errors++;
            } else {
                System.out.println(token);
            }
        }

        // Resumen final
        System.out.println("\n========================");
        System.out.println("Total de tokens: " + tokens.size());
        if (errors > 0) {
            System.out.println("Errores encontrados: " + errors);
        } else {
            System.out.println("Sin errores léxicos.");
        }
    }
}