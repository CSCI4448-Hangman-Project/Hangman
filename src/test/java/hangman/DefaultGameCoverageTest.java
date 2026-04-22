package hangman;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import hangman.observer.GameObserver;

public class DefaultGameCoverageTest {

    private static class FixedWordLibrary extends WordLibrary {
        private final String fixedWord;

        FixedWordLibrary(String fixedWord) throws IOException {
            super("/hangman/words.txt");
            this.fixedWord = fixedWord;
        }

        @Override
        public String getRandomTargetWord() {
            return fixedWord;
        }
    }

    private static class CountingObserver implements GameObserver {
        int count = 0;
        HangmanGame lastGame;

        @Override
        public void onGuessAttempted(HangmanGame game) {
            count++;
            lastGame = game;
        }
    }

    private DefaultGame newGame(String word, int maxWrongGuesses) throws IOException {
        return new DefaultGame(maxWrongGuesses, new FixedWordLibrary(word));
    }

    @Test
    void guessLetterRejectsNonLettersAndDoesNotNotifyObserver() throws IOException {
        DefaultGame game = newGame("apple", 6);
        CountingObserver observer = new CountingObserver();
        game.addObserver(observer);

        assertFalse(game.guessLetter('1'));
        assertEquals(0, game.getWrongGuessCount());
        assertTrue(game.getGuessedLetters().isEmpty());
        assertEquals(0, observer.count);
    }

    @Test
    void correctLetterRevealsAllMatchingLetters() throws IOException {
        DefaultGame game = newGame("apple", 6);

        assertTrue(game.guessLetter('p'));
        assertEquals("_ p p _ _", game.getMaskedWord());
        assertEquals(0, game.getWrongGuessCount());
    }

    @Test
    void allLettersGuessedCausesWin() throws IOException {
        DefaultGame game = newGame("go", 6);

        assertTrue(game.guessLetter('g'));
        assertFalse(game.isGameOver());

        assertTrue(game.guessLetter('o'));
        assertTrue(game.isGameOver());
        assertTrue(game.playerHasWon());
        assertEquals("g o", game.getMaskedWord());
    }

    @Test
    void maxWrongGuessesCausesLoss() throws IOException {
        DefaultGame game = newGame("hi", 2);

        assertTrue(game.guessLetter('x'));
        assertFalse(game.isGameOver());

        assertTrue(game.guessLetter('y'));
        assertTrue(game.isGameOver());
        assertFalse(game.playerHasWon());
        assertEquals(0, game.getRemainingGuesses());
    }

    @Test
    void correctWordGuessWinsImmediately() throws IOException {
        DefaultGame game = newGame("apple", 6);

        assertTrue(game.guessWord("apple"));
        assertTrue(game.isGameOver());
        assertTrue(game.playerHasWon());
        assertEquals("a p p l e", game.getMaskedWord());
    }

    @Test
    void wrongWordGuessCountsAsWrongGuess() throws IOException {
        DefaultGame game = newGame("apple", 6);
        CountingObserver observer = new CountingObserver();
        game.addObserver(observer);

        assertTrue(game.guessWord("banana"));
        assertEquals(1, game.getWrongGuessCount());
        assertFalse(game.isGameOver());
        assertEquals(1, observer.count);
        assertSame(game, observer.lastGame);
    }

    @Test
    void resetClearsStateAndLowercasesTargetWord() throws IOException {
        DefaultGame game = newGame("BANANA", 6);

        game.guessWord("wrong");
        assertEquals(1, game.getWrongGuessCount());

        game.reset();

        assertEquals(0, game.getWrongGuessCount());
        assertTrue(game.getGuessedLetters().isEmpty());
        assertFalse(game.isGameOver());
        assertFalse(game.playerHasWon());
        assertEquals("banana", game.getTargetWord());
        assertEquals("_ _ _ _ _ _", game.getMaskedWord());
    }

    @Test
    void guessedLettersReturnedSetIsUnmodifiable() throws IOException {
        DefaultGame game = newGame("apple", 6);
        game.guessLetter('a');

        assertThrows(UnsupportedOperationException.class, () -> {
            game.getGuessedLetters().add('z');
        });
    }

    @Test
    void guessesAfterGameOverAreRejected() throws IOException {
        DefaultGame game = newGame("a", 6);

        assertTrue(game.guessLetter('a'));
        assertTrue(game.isGameOver());

        assertFalse(game.guessLetter('b'));
        assertFalse(game.guessWord("anything"));
    }

    @Test
    void nullAndBlankWordGuessesAreRejected() throws IOException {
        DefaultGame game = newGame("apple", 6);

        assertFalse(game.guessWord(null));
        assertFalse(game.guessWord("   "));
        assertEquals(0, game.getWrongGuessCount());
    }
}