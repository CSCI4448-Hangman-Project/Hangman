package hangman.observer;

import hangman.HangmanGame;

public class ConsoleObserver implements GameObserver {

    @Override
    public void onGuessAttempted(HangmanGame game) {
        System.out.println();
        System.out.println("Word: " + game.getMaskedWord());
        System.out.println("Guessed letters: " + game.getGuessedLetters());
        System.out.println("Wrong guesses: " + game.getWrongGuessCount());
        System.out.println("Remaining guesses: " + game.getRemainingGuesses());

        if (game.isGameOver()) {
            if (game.playerHasWon()) {
                System.out.println("Congratulations! You won!");
            } else {
                System.out.println("Game over! The word was: " + game.getTargetWord());
            }
        }
    }
}