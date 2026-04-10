package hangman.strategy;

import hangman.HangmanGame;

public interface GuessStrategy {
    boolean isHumanControlled();

    default boolean submitGuess(HangmanGame game, String input) {
        return false;
    }

    default void takeTurn(HangmanGame game) {
        // no-op by default
    }
}
