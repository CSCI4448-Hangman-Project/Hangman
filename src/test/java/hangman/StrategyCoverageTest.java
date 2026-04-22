package hangman;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import hangman.observer.GameObserver;
import hangman.strategy.ComputerGuessStrategy;
import hangman.strategy.HumanGuessStrategy;

public class StrategyCoverageTest {

    private static class FakeGame implements HangmanGame {
        boolean gameOver;
        boolean won;
        int wrongGuessCount;
        int remainingGuesses = 6;
        String targetWord = "apple";
        String maskedWord = "_ _ _ _ _";
        char lastLetter;
        String lastWord;
        int guessLetterCalls;
        int guessWordCalls;
        Set<Character> guessedLetters = new LinkedHashSet<>();

        @Override
        public boolean guessLetter(char letter) {
            guessLetterCalls++;
            lastLetter = letter;
            guessedLetters.add(letter);
            return true;
        }

        @Override
        public boolean guessWord(String word) {
            guessWordCalls++;
            lastWord = word;
            return true;
        }

        @Override
        public String getMaskedWord() {
            return maskedWord;
        }

        @Override
        public Set<Character> getGuessedLetters() {
            return guessedLetters;
        }

        @Override
        public int getWrongGuessCount() {
            return wrongGuessCount;
        }

        @Override
        public int getRemainingGuesses() {
            return remainingGuesses;
        }

        @Override
        public boolean isGameOver() {
            return gameOver;
        }

        @Override
        public boolean playerHasWon() {
            return won;
        }

        @Override
        public String getTargetWord() {
            return targetWord;
        }

        @Override
        public void reset() {
        }

        @Override
        public void addObserver(GameObserver observer) {
        }

        @Override
        public void removeObserver(GameObserver observer) {
        }
    }

    @Test
    void humanStrategyReportsHumanControlled() {
        HumanGuessStrategy strategy = new HumanGuessStrategy();
        assertTrue(strategy.isHumanControlled());
    }

    @Test
    void humanStrategyRejectsNullGameNullInputAndGameOver() {
        HumanGuessStrategy strategy = new HumanGuessStrategy();
        FakeGame game = new FakeGame();
        game.gameOver = true;

        assertFalse(strategy.submitGuess(null, "a"));
        assertFalse(strategy.submitGuess(new FakeGame(), null));
        assertFalse(strategy.submitGuess(game, "a"));
    }

    @Test
    void humanStrategyUsesGuessLetterForSingleCharacter() {
        HumanGuessStrategy strategy = new HumanGuessStrategy();
        FakeGame game = new FakeGame();

        assertTrue(strategy.submitGuess(game, " A "));
        assertEquals(1, game.guessLetterCalls);
        assertEquals('a', game.lastLetter);
        assertEquals(0, game.guessWordCalls);
    }

    @Test
    void humanStrategyUsesGuessWordForLongerInput() {
        HumanGuessStrategy strategy = new HumanGuessStrategy();
        FakeGame game = new FakeGame();

        assertTrue(strategy.submitGuess(game, " Apple "));
        assertEquals(1, game.guessWordCalls);
        assertEquals("apple", game.lastWord);
        assertEquals(0, game.guessLetterCalls);
    }

    @Test
    void humanStrategyRejectsBlankInput() {
        HumanGuessStrategy strategy = new HumanGuessStrategy();
        FakeGame game = new FakeGame();

        assertFalse(strategy.submitGuess(game, "   "));
        assertEquals(0, game.guessLetterCalls);
        assertEquals(0, game.guessWordCalls);
    }

    @Test
    void computerStrategyReportsNotHumanControlled() {
        ComputerGuessStrategy strategy = new ComputerGuessStrategy();
        assertFalse(strategy.isHumanControlled());
    }

    @Test
    void computerStrategyDoesNothingForNullGameOrFinishedGame() {
        ComputerGuessStrategy strategy = new ComputerGuessStrategy();
        FakeGame finishedGame = new FakeGame();
        finishedGame.gameOver = true;

        assertDoesNotThrow(() -> strategy.takeTurn(null));
        assertDoesNotThrow(() -> strategy.takeTurn(finishedGame));
        assertEquals(0, finishedGame.guessLetterCalls);
    }

    @Test
    void computerStrategyGuessesOnlyRemainingLetter() {
        ComputerGuessStrategy strategy = new ComputerGuessStrategy();
        FakeGame game = new FakeGame();

        for (char c = 'a'; c <= 'y'; c++) {
            game.guessedLetters.add(c);
        }

        strategy.takeTurn(game);

        assertEquals(1, game.guessLetterCalls);
        assertEquals('z', game.lastLetter);
    }

    @Test
    void computerStrategyDoesNothingWhenNoLettersRemain() {
        ComputerGuessStrategy strategy = new ComputerGuessStrategy();
        FakeGame game = new FakeGame();

        for (char c = 'a'; c <= 'z'; c++) {
            game.guessedLetters.add(c);
        }

        strategy.takeTurn(game);

        assertEquals(0, game.guessLetterCalls);
    }
}