package hangman;

public interface GameObserver {
    void onGuessAttempted(HangmanGame game);
}