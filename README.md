# COMP5050_assignment1
COMP 5050 Event Draven Computing assignment1

Student name: Jianghan Sun
Student ID: a1880442

## Project Features

This project implements a regular expression matching engine based on Thompson's algorithm. It converts a regular expression into an ε-NFA and checks whether each input string matches the entire expression.

The supported regular expression elements include:

- Letters, digits, and spaces
- Concatenation, for example `ab`
- Alternation using `|`, for example `a|b`
- Kleene star `*`, for example `a*`
- Kleene plus `+`, for example `a+`
- Parentheses, for example `(ab)*`

The current implementation does not support nested parentheses, character classes such as `[a-z]`, or other escaped special characters.

## Compilation

Run the following command from the project root:

```powershell
javac -d out src\Fragment.java src\IdGenerator.java src\NFABuilder.java src\RegexEngine.java src\State.java
```

## Usage

The program reads from standard input. The first line must contain the regular expression, and each following line is an input string to match. Matching is performed against the entire input string rather than a substring.

### Normal Mode

```powershell
@("(ab)*|c+", "abab", "ccc", "a") | java -cp out RegexEngine
```

The program first prints `ready`, then prints `true` or `false` for each input string. An empty line can also be used as an input string.

### Verbose Mode

```powershell
@("(ab)*|c+", "abab", "ccc") | java -cp out RegexEngine -v
```

Verbose mode first prints the NFA transition table, then prints whether the current state is accepting while each input character is processed.

If the regular expression is invalid, the program prints `Invalid regex expression.` and returns exit code `1`. A successful execution returns exit code `0`.

## AI Usage Declaration

AI model: Qwen 3.8 Max

AI was used for the following purposes:

- Understanding the Java data structures required for ε-NFA implementation.
- Understanding the Thompson Algorithm for generating ε-NFAs.
- Understanding prefix-to-postfix expression conversion.
- Understanding how to build NFA fragments from postfix expressions.
- Generating examples of invalid regular expressions for testing.
- Assisting with writing unit tests and locating issues when tests failed.
