import java.util.List;
/**
 * Define a fragment of an NFA
 * q0(start state) -> transition -> q1(dangling state)
 */
public class Fragment {
    public State start;
    public List<State> danglingStates; // not yet connected to the accept state

    public Fragment(State start, List<State> danglingStates) {
        this.start = start;
        this.danglingStates = danglingStates;
    }
}
