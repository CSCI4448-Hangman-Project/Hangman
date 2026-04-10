package hangman;

import java.util.Set;

import hangman.observer.GameObserver;


public interface HangmanGame {
    boolean guessLetter(char letter);
    boolean guessWord(String word);
    
    String getMaskedWord();
    Set<Character> getGuessedLetters();

    int getWrongGuessCount();
    int getRemainingGuesses();

    boolean isGameOver();
    boolean playerHasWon();
    String getTargetWord();

    void reset();

    void addObserver(GameObserver observer);
    void removeObserver(GameObserver observer);
}