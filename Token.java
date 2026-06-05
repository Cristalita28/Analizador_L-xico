public class Token {
    public TokenType type;    // ¿Qué tipo de token es? (INT, PLUS, IDENTIFIER, etc.)
    public String lexeme;     // ¿Cuál es el texto original? ("int", "+", "x", etc.)
    public int line;          // ¿En qué línea del archivo apareció?
    public int column;        // ¿En qué columna de esa línea apareció?

    public Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("Token(%-15s, %-15s, linea=%-3d, columna=%d)",
                type, "\"" + lexeme + "\"", line, column);
    }
}