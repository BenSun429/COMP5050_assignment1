import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RegexEngine {
    private static Set<State> currentStates;

    public static void main(String[] args) throws IOException {
        boolean verbose = args.length > 0 && args[0].equals("-v");
        System.exit(match(verbose, new BufferedReader(new InputStreamReader(System.in))));
    }

    /**
     * Match function
     * @param verbose true-verbose mode, false-normal mode
     * @return exit code
     * @throws IOException
     */
    public static int match(boolean verbose, BufferedReader reader) throws IOException {
        String regex = reader.readLine();

        if(!checkRegexIsValid(regex)) {
            System.out.println("Invalid regex expression.");
            return 1;
        }

        Fragment fragment = new NFABuilder().build(regex);

        // add accept state
        State acceptState = new State();
        acceptState.isAccept = true;
        for (State s : fragment.danglingStates) {
            s.addEpsilon(acceptState);
        }

        if(verbose) {
            printTransitionTable(fragment, acceptState);

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

        return 0;
    }

    /**
     * Print Transition table definition of an NFA
     */
    private static void printTransitionTable(Fragment fragment, State acceptState) {
        Set<State> allStates = new TreeSet<>(Comparator.comparingInt(s -> s.id));
        collectStates(fragment.start, allStates);
        allStates.add(acceptState);

        Set<Character> alphabet = new TreeSet<>();
        for (State s : allStates) {
            alphabet.addAll(s.transitions.keySet());
        }

        // header
        System.out.printf("%-10s", "");
        System.out.printf("%-10s", "epsilon");
        for (char c : alphabet) {
            System.out.printf("%-10s", c);
        }
        System.out.printf("%-10s", "other");
        System.out.println();

        for (State state : allStates) {
            if (state == fragment.start) { // start state, use >
                System.out.printf("%-10s", ">q" + state.id);
            } else if (state == acceptState) { // accept state, use *
                System.out.printf("%-10s", "*q" + state.id);
            } else {
                System.out.printf("%-10s", "q" + state.id);
            }

            // Epsilon column
            List<Integer> epsTargetIds = new ArrayList<>();
            for (State t : state.epsilonTransitions) epsTargetIds.add(t.id);
            Collections.sort(epsTargetIds);
            if (!epsTargetIds.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Integer id : epsTargetIds) {
                    sb.append("q").append(id).append(",");
                }
                System.out.printf("%-10s", sb.substring(0, sb.length() - 1));
            }

            // character column
            for (char c : alphabet) {
                State target = state.transitions.get(c);
                if (target != null) { // has next transition state
                    System.out.printf("%-10s", "q" + state.id);
                } else {
                    System.out.printf("%-10s", "");
                }
            }
            System.out.println();
        }
    }

    private static void collectStates(State current, Set<State> visited) {
        if (visited.contains(current)) return;
        visited.add(current);
        for (State t : current.epsilonTransitions) collectStates(t, visited);
        for (State t : current.transitions.values()) collectStates(t, visited);
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
        if(regex == null || regex.isEmpty()) {
            return false;
        }

        int leftBracketCount = 0;
        int rightBracketCount = 0;

        char lastChar = ' ';
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

            if(c == '*' && lastChar == '*') {
                return false;
            }

            lastChar = c;
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

        if(regex.startsWith("*") || regex.startsWith("+")) {
            return false;
        }


        return true;
    }
}
