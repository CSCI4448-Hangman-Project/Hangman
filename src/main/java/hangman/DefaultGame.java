package hangman;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;


// import hangman.factory.WordLibrary;
// import hangman.strategy.DifficultyStrategy;

public class DefaultGame implements HangmanGame{
    // private final String word;
    private final List<GameObserver> observers = new ArrayList<>();
    // private final DifficultyStrategy difficultyStrategy;
    private final WordLibrary wordLibrary;
    
    private String targetWord;
    private final Set<Character> guessedLetters = new LinkedHashSet<>();
    //strategy pattern for... human & computer. human, random, smart computer etc
    //factory for strategies
    //maybe singleton for stats or command
    //ui swing console and printlns
    //for ui, just put a viewer. no console
    private int wrongGuessCount;
    private final int maxWrongGuesses;

    private boolean gameOver;
    private boolean playerWon;
    
    // might need to implement WordSource instead of using String
    public DefaultGame(int maxWrongGuesses, WordLibrary wordLibrary){
        this.wordLibrary = wordLibrary;
        // this.word = word;
        this.maxWrongGuesses = maxWrongGuesses;
        this.targetWord = wordLibrary.getRandomTargetWord();
    }

    @Override
    public boolean guessLetter(char letter){
        if(gameOver){
            return false;
        }

        letter = Character.toLowerCase(letter);
        
        if(!Character.isLetter(letter)){
            return false;
        }

        if(guessedLetters.contains(letter)){
            return false;
        }

        guessedLetters.add(letter);
        if (targetWord.indexOf(letter) == -1) {
            wrongGuessCount++;
        }

        updateGameState();
        notifyObservers();
        return true;
    }

    @Override
    public boolean guessWord(String word){
        if(gameOver || word == null){
            return false;
        }

        word = word.trim().toLowerCase();

        if(word.isEmpty()){
            return false;
        }

        if(word.equals(targetWord)){
            for(char c : targetWord.toCharArray()){
                guessedLetters.add(c);
            }
            playerWon = true;
            gameOver = true;
        }
        else{
            wrongGuessCount++;
            updateGameState();
        }

        notifyObservers();
        return true;
    }

    @Override
    public String getMaskedWord(){
        StringBuilder sb = new StringBuilder();

        for(char c : targetWord.toCharArray()){
            if(guessedLetters.contains(c)){
                sb.append(c);
            }
            else{
                sb.append("_");
            }
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    @Override
    public Set<Character> getGuessedLetters(){
        return Set.copyOf(guessedLetters);
    }

    @Override
    public int getWrongGuessCount(){
        return wrongGuessCount;
    }

    @Override
    public int getRemainingGuesses(){
        // return difficultyStrategy.getMaxAttempts() - wrongGuessCount;
        return maxWrongGuesses - wrongGuessCount;
    }

    @Override
    public boolean isGameOver(){
        return gameOver;
    }

    @Override
    public boolean playerHasWon(){
        return playerWon;
    }

    @Override
    public String getTargetWord(){
        return targetWord;
    }

    @Override
    public void reset(){
        guessedLetters.clear();
        wrongGuessCount = 0;
        gameOver = false;
        playerWon = false;
        targetWord = wordLibrary.getRandomTargetWord().toLowerCase();
        notifyObservers();
    }

    @Override
    public void addObserver(GameObserver observer){
        if (observer != null) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(GameObserver observer){
        observers.remove(observer);
    }

    private void updateGameState() {
        if (allLettersGuessed()) {
            playerWon = true;
            gameOver = true;
        } else if (wrongGuessCount >= maxWrongGuesses) {
            playerWon = false;
            gameOver = true;
        }
    }

    private boolean allLettersGuessed() {
        for (char c : targetWord.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.onGuessAttempted(this);
        }
    }
}