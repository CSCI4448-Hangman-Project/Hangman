package hangman.factory;

import hangman.DefaultGame;
import hangman.HangmanGame;
import hangman.WordLibrary;

public class DefaultHangmanGameFactory implements HangmanGameFactory {
    private static DefaultHangmanGameFactory instance;

    private final WordLibrary wordLibrary;
    private final int maxWrongGuesses;

    private DefaultHangmanGameFactory(WordLibrary wordLibrary, int maxWrongGuesses) {
        this.wordLibrary = wordLibrary;
        this.maxWrongGuesses = maxWrongGuesses;
    }

    public static DefaultHangmanGameFactory getInstance(WordLibrary wordLibrary, int maxWrongGuesses) {
        if (instance == null) {
            instance = new DefaultHangmanGameFactory(wordLibrary, maxWrongGuesses);
        }
        return instance;
    }

    @Override
    public HangmanGame createGame() {
        return new DefaultGame(maxWrongGuesses, wordLibrary);
    }
}
