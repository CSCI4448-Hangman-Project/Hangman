package hangman.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import hangman.HangmanGame;

public class ComputerGuessStrategy implements GuessStrategy {
    private final Random random;

    public ComputerGuessStrategy(){
        this(new Random());
    }

    public ComputerGuessStrategy(Random random) {
        this.random = random;
    }

    @Override
    public boolean isHumanControlled(){
        return false;
    }

    @Override 
    public void takeTurn(HangmanGame game){
        if(game == null || game.isGameOver()){
            return;
        }

        List<Character> remaining = remainingLetters(game.getGuessedLetters());
        if (remaining.isEmpty()) {
            return;
        }

        char guess = remaining.get(random.nextInt(remaining.size()));
        game.guessLetter(guess);
    }

    private List<Character> remainingLetters(Set<Character> guessedLetters) {
        List<Character> remaining = new ArrayList<>();

        for (char c = 'a'; c <= 'z'; c++) {
            if (!guessedLetters.contains(c)) {
                remaining.add(c);
            }
        }

        return remaining;
    }
}