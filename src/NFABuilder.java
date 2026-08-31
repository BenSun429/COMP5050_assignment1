import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class NFABuilder {
    /**
     * Thompson Algorithm
     *
     * @param regex regular expression, e.g. "(ab)*|c+"
     * @return NFA Fragment
     */
    public static Fragment build(String regex) {
        String regexWithConcat = insertConcatenation(regex);

        List<String> postfix = convertToPostfix(regexWithConcat);

        Fragment NFA = buildNFAFromPostfix(postfix);

        return NFA;
    }

    /**
     * insert concatenation symbol between two characters
     *
     * @param regex regular expression, e.g. "ab"
     * @return regular expression with concatenation symbol, e.g. "a.b"
     */
    private static String insertConcatenation(String regex) {
        String result = "";
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            result += c;

            if (i < regex.length() - 1) { // not the last character
                char next = regex.charAt(i + 1);
                if (needConcat(c, next)) {
                    result += '.';
                }
            }
        }
        return result;
    }

    private static boolean needConcat(char c, char next) {
        boolean currentSymbol = Character.isLetterOrDigit(c) || c == ')' || c == '*' || c == '+';
        boolean nextSymbol = Character.isLetterOrDigit(next) || next == '(';
        return currentSymbol && nextSymbol;
    }

    /**
     * Conversion of infix expressions to postfix expressions
     *
     * @param regex regular expression, e.g. "(a.b)*|c+"
     * @return ab.*c+|
     */
    private static List<String> convertToPostfix(String regex) {
        List<String> postfixExpressionList = new ArrayList<>();
        Deque<Character> operators = new ArrayDeque<>(); // .*+|

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                postfixExpressionList.add(String.valueOf(c));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    postfixExpressionList.add(String.valueOf(operators.pop()));
                }
                operators.pop(); // pop '('
            } else { // .*+|
                while (!operators.isEmpty() && operators.peek() != '(' && calcPrecedence(operators.peek()) >= calcPrecedence(c)) {
                    postfixExpressionList.add(String.valueOf(operators.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            postfixExpressionList.add(String.valueOf(operators.pop()));
        }

        return postfixExpressionList;
    }

    private static int calcPrecedence(char op) {
        if (op == '*' || op == '+') {
            return 3;
        } else if (op == '.') {
            return 2;
        } else if (op == '|') {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * build NFA fragment from postfix expression
     * @param postfix postfix expression, e.g. ab.*c+|
     * @return NFA fragment
     */
    private static Fragment buildNFAFromPostfix(List<String> postfix) {
        Deque<Fragment> stack = new ArrayDeque<>();
        for (String token : postfix) {
            if (token.equals("|")) { // f1|f2
                Fragment f2 = stack.pop();
                Fragment f1 = stack.pop();

                State state = new State();
                state.addEpsilon(f1.start);
                state.addEpsilon(f2.start);

                List<State> dangling = new ArrayList<>();
                dangling.addAll(f1.danglingStates);
                dangling.addAll(f2.danglingStates);

                stack.push(new Fragment(state, dangling));
            } else if (token.equals(".")) { // f1.f2
                Fragment f2 = stack.pop();
                Fragment f1 = stack.pop();

                // f1.danglingStates -> f2.start
                for (State ds : f1.danglingStates) {
                    ds.addEpsilon(f2.start);
                }

                stack.push(new Fragment(f1.start, f2.danglingStates));
            } else if (token.equals("*")) { // The closure(also called the Kleene star)
                Fragment f = stack.pop(); // f*

                State state = new State();
                state.addEpsilon(f.start); // skip f
                state.addEpsilon(f.danglingStates.get(0)); // f -> f

                List<State> dangling = new ArrayList<>();
                for (State ds : f.danglingStates) {
                    ds.addEpsilon(state); // loop
                    dangling.add(ds); // link to new dangling states
                }

                stack.push(new Fragment(state, dangling));
            } else if (token.equals("+")) { // The closure(also called the Kleene plus)
                Fragment f = stack.pop(); // f+

                State state = new State();
                state.addEpsilon(f.start);

                List<State> dangling = new ArrayList<>();
                for (State ds : f.danglingStates) {
                    ds.addEpsilon(f.start); // loop
                    dangling.add(ds);    // link to new dangling states
                }

                stack.push(new Fragment(state, dangling));
            } else { // ->c->end
                char c = token.charAt(0); // convert to char

                State state = new State();
                State end = new State();
                state.addTransition(c, end);

                List<State> dangling = new ArrayList<>();
                dangling.add(end);

                stack.push(new Fragment(state, dangling));
            }
        }

        return stack.pop();
    }

    public static void main(String[] args) {
        System.out.println(insertConcatenation("ab"));
        System.out.println(insertConcatenation("abc"));
        System.out.println(insertConcatenation("(ab)c"));
        System.out.println(insertConcatenation("a*c"));

        System.out.println(convertToPostfix("(a.b)*|c+"));

        Fragment nfa = build("(a.b)*|c+");
        System.out.println();
    }
}
