public enum TokenType {
    // Palabras reservadas
    INT, FLOAT, IF, ELSE, WHILE, FOR, RETURN,

    // Identificadores y literales
    IDENTIFIER,
    INT_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    BOOLEAN_LITERAL,

    // Operadores
    PLUS, MINUS, MULTIPLY, DIVIDE,
    ASSIGN, EQUAL, NOT_EQUAL,
    LESS_THAN, GREATER_THAN, LESS_EQ, GREATER_EQ,

    // Delimitadores
    SEMICOLON, COMMA,
    LPAREN, RPAREN,
    LBRACE, RBRACE,
    LBRACKET, RBRACKET,

    // Especiales
    EOF,   // fin del archivo
    ERROR  // carácter no reconocido
}