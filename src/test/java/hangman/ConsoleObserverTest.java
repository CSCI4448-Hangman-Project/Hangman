package hangman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleObserverTest {

    private DefaultGame game;
    private ConsoleObserver consoleObserver;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        WordLibrary wordLibrary = WordLibrary.getDefaultWordLibrary()
                .orElseThrow(() -> new RuntimeException("Could not load word library"));
        game = new DefaultGame(6, wordLibrary);
        consoleObserver = new ConsoleObserver();
        game.addObserver(consoleObserver);

        // Capture System.out
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String getCapturedOutput() {
        return outputStream.toString();
    }

    @Test
    void testPrintsMaskedWord() {
        game.guessLetter('z');
        String output = getCapturedOutput();
        assertTrue(output.contains("Word:"));
    }

    @Test
    void testPrintsGuessedLetters() {
        game.guessLetter('a');
        String output = getCapturedOutput();
        assertTrue(output.contains("Guessed letters:"));
    }

    @Test
    void testPrintsWrongGuessCount() {
        game.guessLetter('z');
        String output = getCapturedOutput();
        assertTrue(output.contains("Wrong guesses:"));
    }

    @Test
    void testPrintsRemainingGuesses() {
        game.guessLetter('a');
        String output = getCapturedOutput();
        assertTrue(output.contains("Remaining guesses:"));
    }

    @Test
    void testPrintsWinMessage() {
        // Guess the entire target word letter by letter
        String target = game.getTargetWord();
        for (char c : target.toCharArray()) {
            game.guessLetter(c);
        }
        String output = getCapturedOutput();
        assertTrue(output.contains("Congratulations! You won!"));
    }

    @Test
    void testPrintsLossMessage() {
        // Make enough wrong guesses to lose
        String target = game.getTargetWord();
        char wrongChar = 'a';
        for (int i = 0; i < 6; i++) {
            // Find a letter NOT in the target word
            while (target.indexOf(wrongChar) != -1) {
                wrongChar++;
            }
            game.guessLetter(wrongChar);
            wrongChar++;
        }
        String output = getCapturedOutput();
        assertTrue(output.contains("Game over! The word was: " + target));
    }

    @Test
    void testDoesNotPrintGameOverMidGame() {
        game.guessLetter('a');
        if (!game.isGameOver()) {
            String output = getCapturedOutput();
            assertFalse(output.contains("Congratulations"));
            assertFalse(output.contains("Game over"));
        }
    }

    @Test
    void testOutputUpdatesEachGuess() {
        game.guessLetter('a');
        String firstOutput = getCapturedOutput();

        outputStream.reset();
        game.guessLetter('b');
        String secondOutput = getCapturedOutput();

        // Both guesses should produce output
        assertFalse(firstOutput.isEmpty());
        assertFalse(secondOutput.isEmpty());
    }
}
