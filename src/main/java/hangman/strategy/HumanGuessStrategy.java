package hangman.strategy;

import hangman.HangmanGame;

public class HumanGuessStrategy implements GuessStrategy {
    @Override
    public boolean isHumanControlled(){
        return true;
    }

    @Override
    public boolean submitGuess(HangmanGame game, String input){
        if(game == null || input == null || game.isGameOver()){
            return false;
        }

        String normalizedInput = input.trim().toLowerCase();
        if(normalizedInput.isEmpty()){
            return false;
        }

        if(normalizedInput.length() == 1){
            return game.guessLetter(normalizedInput.charAt(0));
        }

        return game.guessWord(normalizedInput);
    }
}
