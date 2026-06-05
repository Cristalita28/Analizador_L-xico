import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Lexer {
    private String source;      // El código fuente completo como texto
    private int pos;            // Posición actual del lexer en el texto
    private int line;           // Línea actual
    private int column;         // Columna actual
    private List<Token> tokens; // Lista donde guardamos todos los tokens encontrados

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("int",    TokenType.INT);
        KEYWORDS.put("float",  TokenType.FLOAT);
        KEYWORDS.put("if",     TokenType.IF);
        KEYWORDS.put("else",   TokenType.ELSE);
        KEYWORDS.put("while",  TokenType.WHILE);
        KEYWORDS.put("for",    TokenType.FOR);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("true",   TokenType.BOOLEAN_LITERAL);
        KEYWORDS.put("false",  TokenType.BOOLEAN_LITERAL);
    }

    public Lexer(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
        this.column = 0;
        this.tokens = new ArrayList<>();
    }

    // Mira el carácter actual SIN moverse
    private char peek() {
        if (pos >= source.length()) return '\0'; // '\0' significa "no hay nada"
        return source.charAt(pos);
    }

    // Mira el SIGUIENTE carácter sin moverse tampoco
    private char peekNext() {
        if (pos + 1 >= source.length()) return '\0';
        return source.charAt(pos + 1);
    }

    // Avanza el dedo una posición y regresa el carácter que acababa de leer
    private char advance() {
        char c = source.charAt(pos);
        pos++;
        column++;
        return c;
    }

    private void skipWhitespace() {
        while (pos < source.length()) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r') {
                advance();
            } else if (c == '\n') {
                line++;        // Nueva línea: incrementamos el contador
                column = 0;    // Reiniciamos la columna a cero
                advance();
            } else {
                break; // Encontramos algo que no es espacio, paramos
            }
        }
    }

    private void addToken(TokenType type, String lexeme, int tokenColumn) {
        tokens.add(new Token(type, lexeme, line, tokenColumn));
    }

    public List<Token> tokenize() {
        while (pos < source.length()) {
            skipWhitespace();
            if (pos >= source.length()) break;

            int tokenColumn = column; // Guardamos la columna ANTES de avanzar
            char c = advance();       // Leemos el carácter actual y avanzamos

            // ¿Es una letra? → puede ser keyword o identificador
            if (Character.isLetter(c) || c == '_') {
                scanIdentifierOrKeyword(c, tokenColumn);

                // ¿Es un dígito? → es un número
            } else if (Character.isDigit(c)) {
                scanNumber(c, tokenColumn);

                // ¿Es una comilla? → es un String
            } else if (c == '"') {
                scanString(tokenColumn);

                // ¿Es un operador o delimitador?
            } else {
                scanSymbol(c, tokenColumn);
            }
        }

        addToken(TokenType.EOF, "EOF", column); // Marcamos el fin del archivo
        return tokens;
    }

    private void scanIdentifierOrKeyword(char first, int tokenColumn) {
        StringBuilder word = new StringBuilder();
        word.append(first); // Ya teníamos la primera letra, la agregamos

        // Seguimos leyendo mientras sean letras, dígitos o guión bajo
        while (pos < source.length() &&
                (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            word.append(advance());
        }

        String result = word.toString();

        // ¿La palabra está en nuestro mapa de keywords?
        if (KEYWORDS.containsKey(result)) {
            addToken(KEYWORDS.get(result), result, tokenColumn);
        } else {
            addToken(TokenType.IDENTIFIER, result, tokenColumn);
        }
    }

    private void scanNumber(char first, int tokenColumn) {
        StringBuilder number = new StringBuilder();
        number.append(first); // Ya teníamos el primer dígito

        // Seguimos leyendo dígitos manualmente
        while (pos < source.length() && Character.isDigit(peek())) {
            number.append(advance());
        }

        // ¿Viene un punto seguido de más dígitos? → es decimal
        if (peek() == '.' && Character.isDigit(peekNext())) {
            number.append(advance()); // consumimos el punto
            while (pos < source.length() && Character.isDigit(peek())) {
                number.append(advance()); // leemos los decimales
            }
        }

        String result = number.toString();

        // Aquí entran las expresiones regulares para clasificar
        if (result.matches("\\d+\\.\\d+")) {
            addToken(TokenType.FLOAT_LITERAL, result, tokenColumn);
        } else if (result.matches("\\d+")) {
            addToken(TokenType.INT_LITERAL, result, tokenColumn);
        } else {
            addToken(TokenType.ERROR, result, tokenColumn);
        }
    }

    private void scanString(int tokenColumn) {
        StringBuilder str = new StringBuilder();

        while (pos < source.length() && peek() != '"') {
            // ¿Hay un salto de línea dentro del string?
            if (peek() == '\n') {
                line++;
                column = 0;
            }
            str.append(advance());
        }

        // ¿Llegamos al final del archivo sin encontrar la comilla de cierre?
        if (pos >= source.length()) {
            addToken(TokenType.ERROR, str.toString(), tokenColumn);
            return;
        }

        advance(); // Consumimos la comilla de cierre "
        addToken(TokenType.STRING_LITERAL, str.toString(), tokenColumn);
    }

    private void scanSymbol(char c, int tokenColumn) {
        switch (c) {
            // Operadores aritméticos simples
            case '+': addToken(TokenType.PLUS,     "+", tokenColumn); break;
            case '-': addToken(TokenType.MINUS,    "-", tokenColumn); break;
            case '*': addToken(TokenType.MULTIPLY, "*", tokenColumn); break;
            case '/': addToken(TokenType.DIVIDE,   "/", tokenColumn); break;

            // Operadores que pueden ser simples o dobles
            case '=':
                if (peek() == '=') { advance(); addToken(TokenType.EQUAL,       "==", tokenColumn); }
                else                {            addToken(TokenType.ASSIGN,      "=",  tokenColumn); }
                break;
            case '!':
                if (peek() == '=') { advance(); addToken(TokenType.NOT_EQUAL,   "!=", tokenColumn); }
                else                {            addToken(TokenType.ERROR,       "!",  tokenColumn); }
                break;
            case '<':
                if (peek() == '=') { advance(); addToken(TokenType.LESS_EQ,     "<=", tokenColumn); }
                else                {            addToken(TokenType.LESS_THAN,   "<",  tokenColumn); }
                break;
            case '>':
                if (peek() == '=') { advance(); addToken(TokenType.GREATER_EQ,  ">=", tokenColumn); }
                else                {            addToken(TokenType.GREATER_THAN,">",  tokenColumn); }
                break;

            // Delimitadores
            case ';': addToken(TokenType.SEMICOLON, ";", tokenColumn); break;
            case ',': addToken(TokenType.COMMA,     ",", tokenColumn); break;
            case '(': addToken(TokenType.LPAREN,    "(", tokenColumn); break;
            case ')': addToken(TokenType.RPAREN,    ")", tokenColumn); break;
            case '{': addToken(TokenType.LBRACE,    "{", tokenColumn); break;
            case '}': addToken(TokenType.RBRACE,    "}", tokenColumn); break;
            case '[': addToken(TokenType.LBRACKET,  "[", tokenColumn); break;
            case ']': addToken(TokenType.RBRACKET,  "]", tokenColumn); break;

            // Cualquier cosa no reconocida
            default:
                addToken(TokenType.ERROR, String.valueOf(c), tokenColumn);
                break;
        }
    }
}
