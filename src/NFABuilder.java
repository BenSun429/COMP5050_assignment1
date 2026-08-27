public class NFABuilder {
    /**
     * Thompson Algorithm
     * @param regex regular expression, e.g. "(ab)*|c+"
     * @return NFA Fragment
     */
    public static Fragment build(String regex) {
        String regexWithConcat = insertConcatenation(regex);

        //
        return null;
    }

    /**
     * insert concatenation symbol between two characters
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

    public static void main(String[] args) {
        System.out.println(insertConcatenation("ab"));
        System.out.println(insertConcatenation("abc"));
        System.out.println(insertConcatenation("(ab)c"));
        System.out.println(insertConcatenation("a*c"));
    }
}
