package hangman.factory;

import hangman.DefaultGame;
import hangman.HangmanGame;
import hangman.WordLibrary;

public class DefaultHangmanGameFactory implements HangmanGameFactory {
    private final WordLibrary wordLibrary;
    private final int maxWrongGuesses;

    public DefaultHangmanGameFactory(WordLibrary wordLibrary, int maxWrongGuesses) {
        this.wordLibrary = wordLibrary;
        this.maxWrongGuesses = maxWrongGuesses;
    }

    @Override
    public HangmanGame createGame() {
        return new DefaultGame(maxWrongGuesses, wordLibrary);
    }
}
