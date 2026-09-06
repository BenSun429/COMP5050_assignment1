import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

public class RegexEngine_Test {

    // 辅助方法：构建 NFA 并判断给定字符串是否完全匹配
    private boolean matches(String regex, String input) {
        Fragment fragment = new NFABuilder().build(regex);

        State acceptState = new State();
        acceptState.isAccept = true;
        for (State s : fragment.danglingStates) {
            s.addEpsilon(acceptState);
        }

        Set<State> currentStates = RegexEngine.epsilonClosure(Collections.singleton(fragment.start));

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            Set<State> nextStates = new java.util.HashSet<>();
            for (State s : currentStates) {
                if (s.transitions.containsKey(c)) {
                    nextStates.add(s.transitions.get(c));
                }
            }
            currentStates = RegexEngine.epsilonClosure(nextStates);
        }

        return currentStates.contains(acceptState);
    }

    @Test
    public void testRegexInValidMatch() {
        assertFalse(RegexEngine.checkRegexIsValid(""));
        assertFalse(RegexEngine.checkRegexIsValid("[a-z]*"));
        assertFalse(RegexEngine.checkRegexIsValid("\n*"));
        assertFalse(RegexEngine.checkRegexIsValid("\ta"));
        assertFalse(RegexEngine.checkRegexIsValid("(ab*"));
        assertFalse(RegexEngine.checkRegexIsValid("ab)*"));
        assertTrue(RegexEngine.checkRegexIsValid("ab*|"));
        assertTrue(RegexEngine.checkRegexIsValid("|ab"));
        assertFalse(RegexEngine.checkRegexIsValid("()"));
        assertFalse(RegexEngine.checkRegexIsValid(")("));
        assertFalse(RegexEngine.checkRegexIsValid("((ab)*)+"));
        assertFalse(RegexEngine.checkRegexIsValid("((ab)*)+"));
        assertFalse(RegexEngine.checkRegexIsValid("a**"));
        assertTrue(RegexEngine.checkRegexIsValid("a++"));
        assertFalse(RegexEngine.checkRegexIsValid("*ab"));
        assertFalse(RegexEngine.checkRegexIsValid("+ab"));
    }

    @Test
    public void testNormalModeMatch() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("(ab)*|c+\nabc\nccc"));
        assertEquals(RegexEngine.match(false, reader), 0);
        reader.close();

        BufferedReader reader1 = new BufferedReader(new StringReader("ab)*|c+\nabc\nccc"));
        assertEquals(RegexEngine.match(false, reader1), 1);
        reader1.close();
    }

    @Test
    public void testVerboseModeMatch() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("(ab)*|c+\na\nb\nc\n\nc"));
        assertEquals(RegexEngine.match(true, reader), 0);
        reader.close();
    }

    @Test
    public void testEmptyInput() {
        assertTrue(matches("(ab)*", ""));
        assertTrue(matches("(ab)*|c+", ""));
        assertTrue(matches("|a", ""));
        assertTrue(matches("a|", ""));
        assertFalse(matches("ab", ""));
    }

    @Test
    public void testBasicCharacters() {
        assertTrue(matches("a", "a"));
        assertFalse(matches("a", "b"));
        assertFalse(matches("a", "\ta"));
        assertFalse(matches("a", "a\t"));

        assertTrue(matches("abc", "abc"));
        assertFalse(matches("abc", "ab"));
        assertFalse(matches("a c", "ac"));
        assertFalse(matches("a c", "a "));
        assertTrue(matches("a c", "a c"));
        assertFalse(matches("a c", "a c "));
    }

    @Test
    public void testUnionOperator() {
        assertTrue(matches("a|b", "a"));
        assertTrue(matches("a|b", "b"));
        assertFalse(matches("a|b", "c"));
    }

    @Test
    public void testKleeneStar() {
        assertTrue(matches("a*", ""));
        assertTrue(matches("a*", "aaaaa"));
        assertTrue(matches("(ab)*", "ababab"));
        assertFalse(matches("(ab)*", "ababa"));
    }

    @Test
    public void testPlusOperator() {
        assertFalse(matches("a+", ""));
        assertTrue(matches("a+", "a"));
        assertTrue(matches("c+", "ccc"));
    }

    @Test
    public void testComplexRegex() {
        assertTrue(matches("(ab)*|c+", ""));
        assertTrue(matches("(ab)*|c+", "abab"));
        assertTrue(matches("(ab)*|c+", "ccc"));
        assertFalse(matches("(ab)*|c+", "a"));
    }
}