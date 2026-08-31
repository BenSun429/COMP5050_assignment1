import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ε-NFA State Class
 */
public class State {

    public int id;
    public boolean isAccept;

    // ε transition: List<target state>
    public List<State> epsilonTransitions = new ArrayList<>();

    // char transition: Map<char, target state>
    public Map<Character, State> transitions = new HashMap<>();

    public State() {
        this.isAccept = false;
    }

    public void addTransition(char c, State target) {
        transitions.put(c, target);
    }

    public void addEpsilon(State target) {
        epsilonTransitions.add(target);
    }
}
