import java.util.*;

public class RegexEngine {
    public static void main(String[] args) {
        String regex = "(ab)*|c+";
        String input = "ab";
        Fragment fragment = NFABuilder.build(regex);

        // add accept state
        State acceptState = new State();
        acceptState.isAccept = true;
        for (State s : fragment.danglingStates) {
            s.addEpsilon(acceptState);
        }

        System.out.println("ready");

        Set<State> currentStates;
        currentStates = epsilonClosure(Collections.singleton(fragment.start));

        for (int i = 0; i < input.length(); i++) {
            currentStates = calcNextEpsilonClosure(currentStates, input.charAt(i));
        }

        if (currentStates.contains(acceptState)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
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

    public static Set<State> calcNextEpsilonClosure(Set<State> currentStates, char c) {
        Set<State> nextStates = new HashSet<>();

        // Calculate the possible next states
        for (State s : currentStates) {
            if (s.transitions.containsKey(c)) {
                nextStates.add(s.transitions.get(c));
            }
        }

        return epsilonClosure(nextStates);
    }
}
