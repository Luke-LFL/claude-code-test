package com.example.leetcode;

// Token.java
public class Token {
    private int symbol;     // 种别码
    private String token;   // 词素值
    private int line;       // 行号
    private int column;     // 列号
    
    public Token(int symbol, String token, int line, int column) {
        this.symbol = symbol;
        this.token = token;
        this.line = line;
        this.column = column;
    }
    
    // Getter方法
    public int getSymbol() { return symbol; }
    public String getToken() { return token; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    
    @Override
    public String toString() {
        return String.format("(%2d, %-8s) line:%d, col:%d", 
                           symbol, token, line, column);
    }
}