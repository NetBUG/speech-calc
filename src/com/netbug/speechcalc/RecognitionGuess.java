package com.netbug.speechcalc;

import android.util.Log;

import com.google.common.base.Joiner;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecognitionGuess {
    private static Map<String, String> BINARY_OPERATIONS = new HashMap<String, String>() {{
        put("add", "+");
        put("сложить", "+");
        put("прибавить", "+");
        put("multiply", "*");
        put("умножить", "*");
        put("divide", "/");
        put("разделить", "/");
        put("subtract", "-");
        put("вычесть", "-");
    }};
    private static Map<String, String> UNARY_OPERATIONS = new HashMap<String, String>() {{
        put("root", "sqrt");
        put("корень", "sqrt");
        put("logarithm", "log");
        put("логарифм", "log");
        put("sign", "sin");
        put("sine", "sin");
        put("синус", "sin");
        put("cosine", "cos");
        put("косинус", "cos");
        put("tangent", "tan");
        put("тангенс", "tan");
    }};

    private static Evaluator mEvaluator = new Evaluator();

    private String recognitionOutput;
    private String mathExpression;
    private String evaluatedValue;
    private boolean isEvaluated;

    RecognitionGuess(String recognitionOutput) {
        this.recognitionOutput = recognitionOutput;
        //~ TODO: use http://numbertrans.sourceforge.net/ to parse strings
        this.mathExpression = convertToMathExpression(this.recognitionOutput);
        evaluate(this.mathExpression);
    }

    private static String normalizeSingleToken(String token) {
        token = token.toLowerCase();
        if (token.matches("[-+]?[0-9]*\\.?[0-9]*") || UNARY_OPERATIONS.containsKey(token) || BINARY_OPERATIONS.containsKey(token)) {
            return token;
        } else if (token.equals("times") || token.equals("multiplied") || token.contains("множ")) {
            return "*";
        } else if (token.startsWith("divi") || token.startsWith("over") || token.contains("дел")) {
            return "/";
        } else if (token.startsWith("plus") || token.contains("плюс")) {
            return "+";
        } else if (token.startsWith("minus") || token.contains("минус")) {
            return "-";
        } else if (token.equals("at")) {
            return "add";
        } else {
            return "";
        }
    }

    private static List<String> normalizeTokens(String recognitionResult) {
        String[] tokens = recognitionResult.split("\\s+");
        List<String> normalizedTokens = new ArrayList<String>();
        for (String token: tokens) {
            String normalizedToken = normalizeSingleToken(token);
            if (normalizedToken.length() > 0) {
                normalizedTokens.add(normalizedToken);
            }
        }
        return normalizedTokens;
    }

    private static List<String> applyUnaryOperations(List<String> tokens) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < tokens.size(); ++i) {
            String token = tokens.get(i);
            if (UNARY_OPERATIONS.containsKey(token)) {
                try {
                    result.add(UNARY_OPERATIONS.get(token) + "(" + tokens.get(++i) + ")");
                } catch (IndexOutOfBoundsException ignored) {

                }
            } else {
                result.add(token);
            }
        }
        return result;
    }

    private static List<String> applyBinaryOperations(List<String> tokens) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < tokens.size(); ++i) {
            String token = tokens.get(i);
            if (BINARY_OPERATIONS.containsKey(token)) {
                try {
                    String firstOperand = tokens.get(++i);
                    String secondOperand = tokens.get(++i);
                    result.add(secondOperand);
                    result.add(BINARY_OPERATIONS.get(token));
                    result.add(firstOperand);
                } catch (IndexOutOfBoundsException ignored) {

                }
            } else {
                result.add(token);
            }
        }
        return result;
    }

    private static String joinTokens(List<String> tokens) {
        return Joiner.on(" ").join(tokens);
    }

    private String convertToMathExpression(String recognitionOutput) {
        return joinTokens(
                applyBinaryOperations(applyUnaryOperations(normalizeTokens(recognitionOutput)))
        );
    }

    private void evaluate(String mathExpression) {
        try {
            this.evaluatedValue = String.format(
                    Locale.US,
                    "%.2f",
                    Double.parseDouble(mEvaluator.evaluate(mathExpression))
            );
            if (this.evaluatedValue.endsWith("00")) {
                this.evaluatedValue = this.evaluatedValue.substring(0, this.evaluatedValue.length() - 3);
            }
            this.isEvaluated = true;
        } catch (EvaluationException exception) {
            this.evaluatedValue = MainActivity.language.equals("en_US") ? "Please, say it again" : "не понимаю";
            Log.d("LANGUAGE", MainActivity.language);
            this.isEvaluated = false;
        }
    }

    public String getRecognitionOutput() {
        return recognitionOutput;
    }

    public String getNormalizedGuess() {
        return mathExpression;
    }

    public String getEvaluatedValue() {
        return evaluatedValue;
    }

    public boolean getIsEvaluated() {
        return isEvaluated;
    }

    @Override
    public String toString() {
        return this.getRecognitionOutput() + ": " + this.getNormalizedGuess() + ": "
                + this.getEvaluatedValue();
    }

    public String toTTSForm() {
        return this.getEvaluatedValue();
    }

}
