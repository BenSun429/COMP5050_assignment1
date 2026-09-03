import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RegexEngine {
    private static Set<State> currentStates;

    public static void main(String[] args) throws IOException {
        boolean verbose = args.length > 0 && args[0].equals("-v");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String regex = reader.readLine();

        if (regex == null || regex.isEmpty()) {
            System.out.println("Regular expressions can't be empty.");
            System.exit(1);
        }
        if(!checkRegexIsValid(regex)) {
            System.out.println("Invalid regex expression.");
            System.exit(1);
        }

        Fragment fragment = NFABuilder.build(regex);

        // add accept state
        State acceptState = new State();
        acceptState.isAccept = true;
        for (State s : fragment.danglingStates) {
            s.addEpsilon(acceptState);
        }

        if(verbose) {
            printTransitionTable();

            System.out.println("ready");

            checkEmptyLineIsAccept(fragment);

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                if (inputLine.isEmpty()) {
                    checkEmptyLineIsAccept(fragment);
                } else {
                    for (int i = 0; i < inputLine.length(); i++) {
                        calcNextEpsilonClosure(inputLine.charAt(i));
                        printStateIsAccept();
                    }
                }
            }
        } else {
            System.out.println("ready");

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {

                currentStates = epsilonClosure(Collections.singleton(fragment.start));

                for (int i = 0; i < inputLine.length(); i++) {
                    calcNextEpsilonClosure(inputLine.charAt(i));
                }

                if (currentStates.contains(acceptState)) {
                    System.out.println("true");
                } else {
                    System.out.println("false");
                }
            }
        }
    }

    private static void printTransitionTable() {
        System.out.println("Transition Table:xxx");
    }

    private static void checkEmptyLineIsAccept(Fragment fragment) {
        currentStates = epsilonClosure(Collections.singleton(fragment.start));
        printStateIsAccept();
    }

    public static void printStateIsAccept() {
        for (State state : currentStates) {
            if (state.isAccept) {
                System.out.println("true");
                return;
            }
        }
        System.out.println("false");
    }

    /**
     * epsilon-closure (ε-closure)
     * Starting from states, find all states that can only be reached through the ε edge
     */
    public static Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Deque<State> stack = new ArrayDeque<>(states);

        while (!stack.isEmpty()) {
            State s = stack.pop();
            for (State epsTarget : s.epsilonTransitions) {
                if (!closure.contains(epsTarget)) {
                    closure.add(epsTarget);
                    stack.push(epsTarget);
                }
            }
        }

        return closure;
    }

    public static void calcNextEpsilonClosure(char c) {
        Set<State> nextStates = new HashSet<>();

        // Calculate the possible next states
        for (State s : currentStates) {
            if (s.transitions.containsKey(c)) {
                nextStates.add(s.transitions.get(c));
            }
        }

        currentStates = epsilonClosure(nextStates);
    }

    public static boolean checkRegexIsValid(String regex) {
        int leftBracketCount = 0;
        int rightBracketCount = 0;

        for (char c : regex.toCharArray()) {
            if (Character.isLetterOrDigit(c)
                    || c == ' '
                    || c == '('
                    || c == ')'
                    || c == '*'
                    || c == '+'
                    || c == '|') {
            } else {
                return false;
            }

            if (c == '(') {
                leftBracketCount++;
            } else if (c == ')') {
                rightBracketCount++;
            }

        }

        // Nested parentheses
        if(leftBracketCount > 1 || rightBracketCount > 1) {
            return false;
        }
        if(leftBracketCount != rightBracketCount) {
            return false;
        }
        if(regex.indexOf(")") < regex.indexOf("(")) {
            return false;
        }
        // Empty in parentheses, e.g. ()
        if(regex.indexOf("(") + 1 == regex.indexOf(")")) {
            return false;
        }


        return true;
    }

    public static boolean checkRegexIsValid(String regex) {
        int leftBracketCount = 0;
        int rightBracketCount = 0;

        for (char c : regex.toCharArray()) {
            if (Character.isLetterOrDigit(c)
                    || c == ' '
                    || c == '('
                    || c == ')'
                    || c == '*'
                    || c == '+'
                    || c == '|') {
            } else {
                return false;
            }

            if (c == '(') {
                leftBracketCount++;
            } else if (c == ')') {
                rightBracketCount++;
            }

        }

        // Nested parentheses
        if(leftBracketCount > 1 || rightBracketCount > 1) {
            return false;
        }
        if(leftBracketCount != rightBracketCount) {
            return false;
        }
        if(regex.indexOf(")") < regex.indexOf("(")) {
            return false;
        }
        // Empty in parentheses, e.g. ()
        if(regex.indexOf("(") + 1 == regex.indexOf(")")) {
            return false;
        }


        return true;
    }
}
