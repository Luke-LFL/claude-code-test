package com.example.leetcode;// LexicalAnalyzer.java
import java.io.*;
import java.util.*;

public class LexicalAnalyzer {
    // 全局变量
    private String program = "";
    private List<Token> program_new = new ArrayList<>();
    private String filename = "";
    
    // 分析状态变量
    private int line = 1;
    private int line_word = 1;
    private int is_string = 0;  // 0:不在字符串中，1:在字符串中
    private int program_char = 0;
    
    // 当前token的临时变量
    private StringBuilder token = new StringBuilder();
    private int symbol = 0;
    
    // 保留字表
    private static final List<String> keyword = Arrays.asList(
        "if", "else", "while", "do", "main", "int", "float", "double", 
        "return", "const", "void", "continue", "break", "char", "unsigned", 
        "enum", "long", "switch", "case", "auto", "static"
    );
    
    // 清除注释（完全对应Python的Read函数）
    public void read(String inputFilename) throws IOException {
        filename = inputFilename;
        StringBuilder programBuilder = new StringBuilder();
        
        try (BufferedReader f1 = new BufferedReader(new FileReader(filename + ".txt"))) {
            int read_current_state = 0;
            String read_line;
            
            while ((read_line = f1.readLine()) != null) {
                read_line = read_line + "\n";
                
                for (int i = 0; i < read_line.length(); i++) {
                    char read_char = read_line.charAt(i);
                    
                    if (read_current_state == 0) {
                        if (read_char == '/') {
                            read_current_state = 1;
                            continue;
                        } else {
                            programBuilder.append(read_char);
                            continue;
                        }
                    }
                    
                    if (read_current_state == 1) {
                        if (read_char == '*') {
                            read_current_state = 2;
                            continue;
                        } else if (read_char == '/') {
                            read_current_state = 4;
                            continue;
                        } else {
                            read_current_state = 0;
                            programBuilder.append('/');
                            programBuilder.append(read_char);
                            continue;
                        }
                    }
                    
                    if (read_current_state == 2) {
                        if (read_char == '*') {
                            read_current_state = 3;
                            continue;
                        } else {
                            read_current_state = 2;
                            continue;
                        }
                    }
                    
                    if (read_current_state == 3) {
                        if (read_char == '/') {
                            read_current_state = 0;
                            continue;
                        } else if (read_char == '*') {
                            read_current_state = 3;
                            continue;
                        } else {
                            read_current_state = 2;
                            continue;
                        }
                    }
                    
                    if (read_current_state == 4) {
                        if (read_char == '\n') {
                            read_current_state = 0;
                            programBuilder.append(read_char);
                            continue;
                        } else {
                            read_current_state = 4;
                            continue;
                        }
                    }
                }
            }
        }
        
        program = programBuilder.toString();
        
        // 消除主程序前的空行
        while (!program.isEmpty() && program.charAt(0) == '\n') {
            program = program.substring(1);
        }
        
        // 消除主程序中的空行与段前空格
        int index_of_blank = 1;
        while (index_of_blank < program.length()) {
            if (program.charAt(index_of_blank - 1) == '\n' && 
                (program.charAt(index_of_blank) == ' ' || 
                 program.charAt(index_of_blank) == '\n')) {
                program = program.substring(0, index_of_blank) + 
                         program.substring(index_of_blank + 1);
            } else {
                index_of_blank++;
            }
        }
        
        // 按格式输出修改后的主程序
        System.out.println("-----------Step 1 消除注释--------------");
        int row = 1;
        System.out.print(row + "\t");
        for (int i = 0; i < program.length(); i++) {
            char c = program.charAt(i);
            System.out.print(c);
            if (c == '\n') {
                row++;
                System.out.print(row + "\t");
            }
        }
        System.out.println("\n");
    }
    
    //
    private void getTokenInternal() {
        token.setLength(0);  // 清空token
        symbol = 0;
        
        // 处理字符串开始
        if (is_string == 0 && program_char > 0 && program.charAt(program_char - 1) == '\"') {
            is_string = 1;
            symbol = 48;  // 字符串常量
            while (true) {
                token.append(program.charAt(program_char));
                program_char++;
                if (program.charAt(program_char) == '\"') {
                    return;
                }
            }
        }
        
        // 处理字符串结束
        if (is_string == 1 && program_char > 0 && program.charAt(program_char - 1) == '\"') {
            is_string = 0;
        }
        
        // 跳过空格和制表符
        while (program_char < program.length() && 
               (program.charAt(program_char) == ' ' || program.charAt(program_char) == '\t')) {
            program_char++;
            line_word++;
        }
        
        // 处理换行
        if (program_char < program.length() && program.charAt(program_char) == '\n') {
            line++;
            line_word = 1;
            program_char++;
        }
        
        // 检查是否越界
        if (program_char >= program.length()) {
            return;
        }
        
        char currentChar = program.charAt(program_char);
        
        // 处理标识符和保留字
        if (('a' <= currentChar && currentChar <= 'z') || 
            ('A' <= currentChar && currentChar <= 'Z') || 
            currentChar == '_') {
            symbol = 42;  // id
            
            while (program_char < program.length()) {
                char ch = program.charAt(program_char);
                if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || 
                    ('0' <= ch && ch <= '9') || ch == '_') {
                    token.append(ch);
                    program_char++;
                } else {
                    break;
                }
            }
            
            String tokenStr = token.toString();
            if (keyword.contains(tokenStr)) {
                symbol = keyword.indexOf(tokenStr) + 1;
            }
            return;
        }
        
        // 处理数字（完全对应Python逻辑）
        if (currentChar == '0') {
            token.append(currentChar);
            program_char++;
            
            if (program_char < program.length()) {
                char nextChar = Character.toLowerCase(program.charAt(program_char));
                
                if (nextChar == 'b') {
                    // 二进制
                    token.append(nextChar);
                    program_char++;
                    while (program_char < program.length()) {
                        char ch = program.charAt(program_char);
                        if ('0' <= ch && ch <= '1') {
                            token.append(ch);
                            program_char++;
                        } else {
                            break;
                        }
                    }
                    symbol = 45;  // 数值-二进制
                    return;
                } else if (nextChar == 'o') {
                    // 八进制
                    token.append(nextChar);
                    program_char++;
                    while (program_char < program.length()) {
                        char ch = program.charAt(program_char);
                        if ('0' <= ch && ch <= '7') {
                            token.append(ch);
                            program_char++;
                        } else {
                            break;
                        }
                    }
                    symbol = 46;  // 数值-八进制
                    return;
                } else if (nextChar == 'x') {
                    // 十六进制（注意：Python代码中是'h'，但通常用'x'）
                    token.append(nextChar);
                    program_char++;
                    while (program_char < program.length()) {
                        char ch = Character.toLowerCase(program.charAt(program_char));
                        if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'f')) {
                            token.append(ch);
                            program_char++;
                        } else {
                            break;
                        }
                    }
                    symbol = 47;  // 数值-十六进制
                    return;
                } else if (nextChar == '.') {
                    // 浮点数
                    token.append(nextChar);
                    program_char++;
                    while (program_char < program.length()) {
                        char ch = program.charAt(program_char);
                        if ('0' <= ch && ch <= '9') {
                            token.append(ch);
                            program_char++;
                        } else {
                            break;
                        }
                    }
                    symbol = 44;  // 数值-浮点数
                    return;
                } else {
                    // 纯数字0
                    symbol = 43;  // 数值-整型
                    return;
                }
            } else {
                symbol = 43;  // 数值-整型
                return;
            }
        } 
        // 处理1-9开头的数字
        else if ('1' <= currentChar && currentChar <= '9') {
            boolean hasDot = false;
            
            while (program_char < program.length()) {
                char ch = program.charAt(program_char);
                if ('0' <= ch && ch <= '9') {
                    token.append(ch);
                    program_char++;
                } else if (ch == '.') {
                    hasDot = true;
                    token.append(ch);
                    program_char++;
                    break;
                } else {
                    break;
                }
            }
            
            if (hasDot) {
                symbol = 44;  // 浮点数
                while (program_char < program.length()) {
                    char ch = program.charAt(program_char);
                    if ('0' <= ch && ch <= '9') {
                        token.append(ch);
                        program_char++;
                    } else {
                        break;
                    }
                }
            } else {
                symbol = 43;  // 整型
            }
            return;
        } 
        // 处理特殊符号
        else {
            token.append(currentChar);
            
            switch (currentChar) {
                case '+':
                    symbol = 22;
                    program_char++;
                    break;
                case '-':
                    symbol = 23;
                    program_char++;
                    break;
                case '*':
                    symbol = 24;
                    program_char++;
                    break;
                case '/':
                    symbol = 25;
                    program_char++;
                    break;
                case '=':
                    symbol = 26;
                    program_char++;
                    if (program_char < program.length() && program.charAt(program_char) == '=') {
                        token.append('=');
                        symbol = 35;
                        program_char++;
                    }
                    break;
                case '<':
                    symbol = 27;
                    program_char++;
                    if (program_char < program.length() && program.charAt(program_char) == '=') {
                        token.append('=');
                        symbol = 41;
                        program_char++;
                    }
                    break;
                case '>':
                    symbol = 39;
                    program_char++;
                    if (program_char < program.length() && program.charAt(program_char) == '=') {
                        token.append('=');
                        symbol = 40;
                        program_char++;
                    }
                    break;
                case '{':
                    symbol = 28;
                    program_char++;
                    break;
                case '}':
                    symbol = 29;
                    program_char++;
                    break;
                case ';':
                    symbol = 30;
                    program_char++;
                    break;
                case '(':
                    symbol = 31;
                    program_char++;
                    break;
                case ')':
                    symbol = 32;
                    program_char++;
                    break;
                case '\'':
                    symbol = 33;
                    program_char++;
                    break;
                case '\"':
                    symbol = 34;
                    program_char++;
                    break;
                case '!':
                    program_char++;
                    if (program_char < program.length() && program.charAt(program_char) == '=') {
                        token.append('=');
                        symbol = 36;
                        program_char++;
                    }
                    break;
                case '&':
                    program_char++;
                    if (program_char < program.length() && program.charAt(program_char) == '&') {
                        token.append('&');
                        symbol = 37;
                        program_char++;
                    }
                    break;
                case '|':
                    program_char++;
                    if (program_char < program.length() && program.charAt(program_char) == '|') {
                        token.append('|');
                        symbol = 38;
                        program_char++;
                    }
                    break;
                default:
                    symbol = -2;
                    program_char++;
            }
            return;
        }
    }
    
    // 主分析函数
    public void analyze() throws IOException {
        System.out.println("-----------Step 2 词法分析--------------");
        
        // 重置状态
        line = 1;
        line_word = 1;
        is_string = 0;
        program_char = 0;
        program_new.clear();
        
        while (program_char < program.length()) {
            getTokenInternal();
            
            String tokenStr = token.toString();
            
            // 跳过空白token
            if (tokenStr.isEmpty() && symbol == 0) {
                continue;
            }
            
            // 错误检查（完全对应Python逻辑）
            if (program_new.size() > 1) {
                Token lastToken = program_new.get(program_new.size() - 1);
                int lastSymbol = lastToken.getSymbol();
                
                // 变量声明后检查
                if (lastSymbol == 6 || lastSymbol == 7 || lastSymbol == 8 || 
                    lastSymbol == 11 || lastSymbol == 14 || lastSymbol == 15 || 
                    lastSymbol == 16 || lastSymbol == 17) {
                    if (symbol == 43) {
                        System.out.println("变量名不能以数字开头");
                        symbol = -1;
                    } else if (symbol != 5 && symbol != 42) {
                        System.out.println("缺少变量名");
                        symbol = -2;
                    }
                }
                // 数字后检查
                else if (lastSymbol == 43 || lastSymbol == 44 || lastSymbol == 45 || 
                         lastSymbol == 46 || lastSymbol == 47) {
                    if (symbol == 42 || symbol == 43 || symbol == 44 || 
                        symbol == 45 || symbol == 46 || symbol == 47) {
                        System.out.println("数字书写不规范");
                        symbol = -3;
                    }
                }
            }
            
            // 类型检查
            if (program_new.size() > 2) {
                Token token3 = program_new.get(program_new.size() - 3);
                int symbol3 = token3.getSymbol();
                
                if (symbol3 == 6) {  // int
                    if (symbol != 32 && symbol != 43 && symbol != 45 && 
                        symbol != 46 && symbol != 47) {
                        System.out.println("变量不是int类型");
                        symbol = -4;
                    }
                } else if (symbol3 == 7 || symbol3 == 8) {  // float/double
                    if (symbol != 44) {
                        System.out.println("变量不是float类型");
                        symbol = -5;
                    }
                }
            }
            
            if (program_new.size() > 3) {
                Token token4 = program_new.get(program_new.size() - 4);
                int symbol4 = token4.getSymbol();
                
                if (symbol4 == 14) {  // char
                    if (symbol != 48) {
                        System.out.println("变量不是char类型");
                        symbol = -6;
                    }
                }
            }
            
            // 输出（使用Python的格式）
            System.out.printf("(%2d,%8s)\tline:%d,row:%d\n", 
                symbol, tokenStr, line, line_word);
            
            // 添加到列表
            program_new.add(new Token(symbol, tokenStr, line, line_word));
            
            line_word += tokenStr.length();
        }
        
        // 检查括号匹配（对应Python逻辑）
        List<String> tokensList = new ArrayList<>();
        for (Token t : program_new) {
            tokensList.add(t.getToken());
        }
        
        int singleQuoteCount = 0;
        int doubleQuoteCount = 0;
        int leftParenCount = 0;
        int rightParenCount = 0;
        int leftBraceCount = 0;
        int rightBraceCount = 0;
        
        for (String t : tokensList) {
            if (t.equals("'")) singleQuoteCount++;
            else if (t.equals("\"")) doubleQuoteCount++;
            else if (t.equals("(")) leftParenCount++;
            else if (t.equals(")")) rightParenCount++;
            else if (t.equals("{")) leftBraceCount++;
            else if (t.equals("}")) rightBraceCount++;
        }
        
        if (singleQuoteCount % 2 == 1) {
            System.out.println("单引号数量不匹配");
        }
        if (doubleQuoteCount % 2 == 1) {
            System.out.println("双引号数量不匹配");
        }
        if (leftParenCount != rightParenCount) {
            System.out.println("小括号数量不匹配");
        }
        if (leftBraceCount != rightBraceCount) {
            System.out.println("大括号数量不匹配");
        }
        
        // 保存到文件
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename + "-out.txt"))) {
            for (Token t : program_new) {
                writer.printf("%d,%s,%d,%d,\n", 
                    t.getSymbol(), t.getToken(), t.getLine(), t.getColumn());
            }
        }
        
        System.out.println("词法分析结束，存入文件" + filename + "-out.txt");
    }
    
    public static void main(String[] args) {
        // src\main\java\com\example\leetcode\text
        LexicalAnalyzer analyzer = new LexicalAnalyzer();
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("请输入文件名（不含扩展名）: ");
        String filename = scanner.nextLine();
        
        try {
            analyzer.read(filename);
            analyzer.analyze();
        } catch (IOException e) {
            System.out.println("错误: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}