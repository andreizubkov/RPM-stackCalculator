import java.util.ArrayDeque;

public class StackCalculator {
    private String expression;

    private enum Priority {Low, Medium}

    public StackCalculator() {
        expression = "0";
    }

    public StackCalculator(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    private static Priority getPriority(String operator) {
        Priority pr = null;
        if (operator.equals("+") || operator.equals("-")) { pr = Priority.Low; }
        if (operator.equals("*") || operator.equals("/")) { pr = Priority.Medium; }
        return pr ;
    }

    private static boolean isDouble(String s) {
        int dot_counter = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (dot_counter > 1) { return false; }
            if (s.charAt(i) == '.') {
                dot_counter++;
                continue;
            }
            if (!Character.isDigit(s.charAt(i))) { return false; }
        }
        return true;
    }

    private static double calculate_unary(String operator, double operand) throws StackCalculatorException {
        switch(operator) {
            case "u-":
                return -operand;
            default:
                throw new StackCalculatorException("Invalid operator: " + operator + "\"");
        }
    }

    private static double calculate_binary(String operator, double operand1, double operand2) throws StackCalculatorException {
        switch(operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                return operand1 / operand2;
            default:
            throw new StackCalculatorException("Invalid operator: " + operator + "\"");
        }
    }

    public static String getReversePolishNotation(String expression) throws StackCalculatorException {
        String [] symbols = expression.replace("\s", "").split("(?<=[-+*/()])|(?=[-+*/()])");
        ArrayDeque<String> stack = new ArrayDeque<>();
        StringBuilder result = new StringBuilder();
        boolean unaryOperator = true;

        for (String symbol: symbols) {
            if (isDouble(symbol)) {
                result.append(symbol).append(' ');
                unaryOperator = false;
                continue;
            }
            if (symbol.equals("-") && unaryOperator) {
                stack.addFirst("u-");
                unaryOperator = false;
                continue;
            }
            if (symbol.equals("(")) {
                stack.addFirst(symbol);
                unaryOperator = true;
                continue;
            }
            if (symbol.equals(")")) {
                while (!stack.isEmpty() && !stack.peekFirst().equals("(")) { result.append(stack.removeFirst()).append(' '); }
                if (!stack.isEmpty()) {
                    stack.removeFirst();
                } else { throw new StackCalculatorException("Mismatched parentheses in expression"); }
                unaryOperator = false;
                continue;
            }
            if (symbol.equals("+") || symbol.equals("-") || symbol.equals("*") || symbol.equals("/")) {
                while (!stack.isEmpty() && !stack.peekFirst().equals("(") && (stack.peekFirst().equals("u-") || (getPriority(stack.peekFirst())).compareTo(getPriority(symbol)) >= 0)) {
                    result.append(stack.removeFirst()).append(' ');
                }
                stack.addFirst(symbol);
                unaryOperator = true;
                continue;
            }

            throw new StackCalculatorException("Invalid symbol in expression: \"" + symbol +"\"");
        }

        while(!stack.isEmpty()) {
            if (stack.peekFirst().equals("(") || stack.peekFirst().equals(")")) {
                throw new StackCalculatorException("Mismatched parentheses in expression");
            }
            result.append(stack.removeFirst()).append(' ');
        }

        return result.toString();
    }

    public static double calculate(String expression) throws StackCalculatorException {
        expression = getReversePolishNotation(expression);
        ArrayDeque<Double> stack = new ArrayDeque<>();
        String[] symbols = expression.split(" ");

        for (String symbol : symbols) {
            if (Character.isDigit(symbol.charAt(0))) {
                stack.addFirst(Double.parseDouble(symbol));
            }
            else if (symbol.equals("u-")) {
                if (stack.isEmpty()) { throw new StackCalculatorException("Not enough operands for unary operator: \"" + symbol + "\""); }
                double op1 = stack.removeFirst();
                stack.addFirst(calculate_unary(symbol, op1));
            }
            else {
                if (stack.size() < 2) { throw new StackCalculatorException("Not enough operands for binary operator: \"" + symbol + "\""); }
                double op2 = stack.removeFirst();
                double op1 = stack.removeFirst();
                stack.addFirst(calculate_binary(symbol, op1, op2));
            }
        }

        if (stack.size() != 1) { throw new StackCalculatorException("Invalid expression"); }

        return stack.removeFirst();
    }
}