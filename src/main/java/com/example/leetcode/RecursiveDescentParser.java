package com.example.leetcode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RecursiveDescentParser {
    private String filename = "";
    private List<String[]> vtTable = new ArrayList<>();
    private int readVtIndex = 0;
    private int errorFlag = 0;
    private int step = 1;
    private List<String> stack = new ArrayList<>();
    private String method = "";

    public static void main(String[] args) {
        RecursiveDescentParser parser = new RecursiveDescentParser();
        parser.read();
        System.out.println("-----------Step 1 语法分析--------------");
        parser.stack.add("program");
        parser.program();
        System.out.println("递归下降语法分析结束。");
    }

    private void info() {
        System.out.printf("-----Step: %d-----%n", step);
        System.out.print("识别串：=>");
        for (String item : stack) {
            System.out.print(item + " ");
        }
        System.out.println();
        System.out.println("动作：" + method);
        System.out.println();
        step++;
    }

    private void read() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入文件名");
        filename = scanner.nextLine();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename + ".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String[] pair = new String[2];
                pair[0] = parts[0];
                pair[1] = parts.length > 1 ? parts[1] : "";
                vtTable.add(pair);
            }
        } catch (IOException e) {
            System.out.println("文件读取错误: " + e.getMessage());
        }
    }

    private void match(String vt) {
        if (errorFlag == 1) {
            return;
        }
        if (readVtIndex >= vtTable.size() || !vt.equals(vtTable.get(readVtIndex)[1])) {
            error();
            System.out.printf("当前出现的%s与需要的%s不匹配%n", 
                readVtIndex < vtTable.size() ? vtTable.get(readVtIndex)[1] : "EOF", vt);
            return;
        }
        readVtIndex++;
    }

    private void program() {
        if (errorFlag == 1) {
            return;
        }
        method = "program\t-->\tblock";
        info();
        int index = stack.lastIndexOf("program");
        if (index != -1) {
            stack.remove(index);
            stack.add(index, "block");
        }
        block();
    }

    private void block() {
        if (errorFlag == 1) {
            return;
        }
        method = "block\t-->\t{stmts}";
        info();
        int index = stack.lastIndexOf("block");
        if (index != -1) {
            stack.remove(index);
            stack.add(index, "{");
            stack.add(index + 1, "stmts");
            stack.add(index + 2, "}");
        }
        match("{");
        stmts();
        match("}");
    }

    private void stmts() {
        if (errorFlag == 1) {
            return;
        }
        if (readVtIndex < vtTable.size() && vtTable.get(readVtIndex)[1].equals("}")) {
            method = "stmts\t-->\tnull";
            info();
            int index = stack.lastIndexOf("stmts");
            if (index != -1) {
                stack.remove(index);
            }
            return;
        }
        method = "stmts\t-->\tstmt stmts";
        info();
        int index = stack.lastIndexOf("stmts");
        if (index != -1) {
            stack.remove(index);
            stack.add(index, "stmt");
            stack.add(index + 1, "stmts");
        }
        stmt();
        stmts();
    }

    private void stmt() {
        if (errorFlag == 1) {
            return;
        }
        
        if (readVtIndex >= vtTable.size()) {
            error();
            return;
        }
        
        String tokenValue = vtTable.get(readVtIndex)[1];
        String tokenType = vtTable.get(readVtIndex)[0];
        
        if (tokenType.equals("42")) { // id
            method = "stmt\t-->\tid = expr;";
            info();
            int index = stack.lastIndexOf("stmt");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "id");
                stack.add(index + 1, "=");
                stack.add(index + 2, "expr");
                stack.add(index + 3, ";");
            }
            readVtIndex++;
            match("=");
            expr();
            match(";");
        } else if (tokenValue.equals("if")) {
            readVtIndex++;
            match("(");
            booleanExpr();
            match(")");
            stmt();
            
            if (readVtIndex < vtTable.size() && vtTable.get(readVtIndex)[1].equals("else")) {
                method = "stmt\t-->\tif (boolean) stmt else stmt";
                info();
                int index = stack.lastIndexOf("stmt");
                if (index != -1) {
                    stack.remove(index);
                    stack.add(index, "if");
                    stack.add(index + 1, "(");
                    stack.add(index + 2, "boolean");
                    stack.add(index + 3, ")");
                    stack.add(index + 4, "stmt");
                    stack.add(index + 5, "else");
                    stack.add(index + 6, "stmt");
                }
                match("else");
                stmt();
                return;
            }
            method = "stmt\t-->\tif (boolean) stmt";
            info();
            int index = stack.lastIndexOf("stmt");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "if");
                stack.add(index + 1, "(");
                stack.add(index + 2, "boolean");
                stack.add(index + 3, ")");
                stack.add(index + 4, "stmt");
            }
        } else if (tokenValue.equals("while")) {
            method = "stmt\t-->\twhile (boolean) stmt";
            info();
            int index = stack.lastIndexOf("stmt");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "while");
                stack.add(index + 1, "(");
                stack.add(index + 2, "boolean");
                stack.add(index + 3, ")");
                stack.add(index + 4, "stmt");
            }
            readVtIndex++;
            match("(");
            booleanExpr();
            match(")");
            stmt();
        } else if (tokenValue.equals("do")) {
            method = "stmt\t-->\tdo stmt while (boolean)";
            info();
            int index = stack.lastIndexOf("stmt");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "do");
                stack.add(index + 1, "stmt");
                stack.add(index + 2, "while");
                stack.add(index + 3, "(");
                stack.add(index + 4, "boolean");
                stack.add(index + 5, ")");
            }
            readVtIndex++;
            stmt();
            match("while");
            match("(");
            booleanExpr();
            match(")");
        } else if (tokenValue.equals("break")) {
            method = "stmt\t-->\tbreak";
            info();
            int index = stack.lastIndexOf("stmt");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "break");
            }
            readVtIndex++;
        } else {
            method = "stmt\t-->\tblock";
            info();
            int index = stack.lastIndexOf("stmt");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "block");
            }
            block();
        }
    }

    private void booleanExpr() {
        if (errorFlag == 1) {
            return;
        }
        
        if (readVtIndex + 1 >= vtTable.size()) {
            method = "boolean\t-->\texpr";
            info();
            int index = stack.lastIndexOf("boolean");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "expr");
            }
            expr();
            return;
        }
        
        String nextToken = vtTable.get(readVtIndex + 1)[1];
        
        switch (nextToken) {
            case "<":
                method = "boolean\t-->\texpr < expr";
                info();
                int index1 = stack.lastIndexOf("boolean");
                if (index1 != -1) {
                    stack.remove(index1);
                    stack.add(index1, "expr");
                    stack.add(index1 + 1, "<");
                    stack.add(index1 + 2, "expr");
                }
                expr();
                readVtIndex++;
                expr();
                break;
            case "<=":
                method = "boolean\t-->\texpr <= expr";
                info();
                int index2 = stack.lastIndexOf("boolean");
                if (index2 != -1) {
                    stack.remove(index2);
                    stack.add(index2, "expr");
                    stack.add(index2 + 1, "<=");
                    stack.add(index2 + 2, "expr");
                }
                expr();
                readVtIndex++;
                expr();
                break;
            case ">":
                method = "boolean\t-->\texpr > expr";
                info();
                int index3 = stack.lastIndexOf("boolean");
                if (index3 != -1) {
                    stack.remove(index3);
                    stack.add(index3, "expr");
                    stack.add(index3 + 1, ">");
                    stack.add(index3 + 2, "expr");
                }
                expr();
                readVtIndex++;
                expr();
                break;
            case ">=":
                method = "boolean\t-->\texpr >= expr";
                info();
                int index4 = stack.lastIndexOf("boolean");
                if (index4 != -1) {
                    stack.remove(index4);
                    stack.add(index4, "expr");
                    stack.add(index4 + 1, ">=");
                    stack.add(index4 + 2, "expr");
                }
                expr();
                readVtIndex++;
                expr();
                break;
            default:
                method = "boolean\t-->\texpr";
                info();
                int index5 = stack.lastIndexOf("boolean");
                if (index5 != -1) {
                    stack.remove(index5);
                    stack.add(index5, "expr");
                }
                expr();
                break;
        }
    }

    private void expr() {
        if (errorFlag == 1) {
            return;
        }
        method = "expr\t-->\tterm expr1";
        info();
        int index = stack.lastIndexOf("expr");
        if (index != -1) {
            stack.remove(index);
            stack.add(index, "term");
            stack.add(index + 1, "expr1");
        }
        term();
        expr1();
    }

    private void expr1() {
        if (errorFlag == 1) {
            return;
        }
        
        if (readVtIndex >= vtTable.size()) {
            method = "expr1\t-->\tnull";
            info();
            int index = stack.lastIndexOf("expr1");
            if (index != -1) {
                stack.remove(index);
            }
            return;
        }
        
        String tokenValue = vtTable.get(readVtIndex)[1];
        
        if (tokenValue.equals("+")) {
            method = "expr1\t-->\t + term expr1";
            info();
            int index = stack.lastIndexOf("expr1");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "+");
                stack.add(index + 1, "term");
                stack.add(index + 2, "expr1");
            }
            readVtIndex++;
            term();
            expr1();
        } else if (tokenValue.equals("-")) {
            method = "expr1\t-->\t - term expr1";
            info();
            int index = stack.lastIndexOf("expr1");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "-");
                stack.add(index + 1, "term");
                stack.add(index + 2, "expr1");
            }
            readVtIndex++;
            term();
            expr1();
        } else {
            method = "expr1\t-->\tnull";
            info();
            int index = stack.lastIndexOf("expr1");
            if (index != -1) {
                stack.remove(index);
            }
        }
    }

    private void term() {
        if (errorFlag == 1) {
            return;
        }
        method = "term\t-->\tfactor term1";
        info();
        int index = stack.lastIndexOf("term");
        if (index != -1) {
            stack.remove(index);
            stack.add(index, "factor");
            stack.add(index + 1, "term1");
        }
        factor();
        term1();
    }

    private void term1() {
        if (errorFlag == 1) {
            return;
        }
        
        if (readVtIndex >= vtTable.size()) {
            method = "term1\t-->\tnull";
            info();
            int index = stack.lastIndexOf("term1");
            if (index != -1) {
                stack.remove(index);
            }
            return;
        }
        
        String tokenValue = vtTable.get(readVtIndex)[1];
        
        if (tokenValue.equals("*")) {
            method = "term1\t-->\t * factor term1";
            info();
            int index = stack.lastIndexOf("term1");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "*");
                stack.add(index + 1, "factor");
                stack.add(index + 2, "term1");
            }
            readVtIndex++;
            factor();
            term1();
        } else if (tokenValue.equals("/")) {
            method = "term1\t-->\t / factor term1";
            info();
            int index = stack.lastIndexOf("term1");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "/");
                stack.add(index + 1, "factor");
                stack.add(index + 2, "term1");
            }
            readVtIndex++;
            factor();
            term1();
        } else {
            method = "term1\t-->\tnull";
            info();
            int index = stack.lastIndexOf("term1");
            if (index != -1) {
                stack.remove(index);
            }
        }
    }

    private void factor() {
        if (errorFlag == 1) {
            return;
        }
        
        if (readVtIndex >= vtTable.size()) {
            error();
            return;
        }
        
        String tokenValue = vtTable.get(readVtIndex)[1];
        String tokenType = vtTable.get(readVtIndex)[0];
        
        if (tokenValue.equals("(")) {
            method = "factor\t-->\t(expr)";
            info();
            int index = stack.lastIndexOf("factor");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "(");
                stack.add(index + 1, "expr");
                stack.add(index + 2, ")");
            }
            readVtIndex++;
            expr();
            match(")");
        } else if (tokenType.equals("43") || tokenType.equals("44") || tokenType.equals("45") || 
                   tokenType.equals("46") || tokenType.equals("47")) { // number
            method = "factor\t-->\tnum";
            info();
            int index = stack.lastIndexOf("factor");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "num");
            }
            readVtIndex++;
        } else if (tokenType.equals("42")) { // id
            method = "factor\t-->\tid";
            info();
            int index = stack.lastIndexOf("factor");
            if (index != -1) {
                stack.remove(index);
                stack.add(index, "id");
            }
            readVtIndex++;
        } else {
            error();
        }
    }

    private void error() {
        errorFlag = 1;
        System.out.println("出错！");
    }
}