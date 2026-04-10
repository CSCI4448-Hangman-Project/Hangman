package hangman.observer;

import hangman.HangmanGame;

public interface GameObserver {
    void onGuessAttempted(HangmanGame game);
}