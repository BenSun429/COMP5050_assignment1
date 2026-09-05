/**
 * Global Id generator
 */
public class IdGenerator {
    private static int id = 0;

    public int getId() {
        return id++;
    }
}
