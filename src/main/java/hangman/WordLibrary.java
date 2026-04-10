package hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

public class WordLibrary {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Random random = new Random();

    private final List<String> availableTargetWords;
    

    public WordLibrary(String targetWordsFilePath) throws IOException {
        this.availableTargetWords = readWords(targetWordsFilePath);
        System.out.println(sizeOfTargetWords() + " target words loaded");
        
    }

    private static Optional<WordLibrary> tryCreate(String targetPath) {
        try {
            return Optional.of(new WordLibrary(targetPath));
        } catch (IOException e) {
            System.err.println("Failed to load word library: " + e.getMessage());
            return Optional.empty();
        }
    }

     public static Optional<WordLibrary> getDefaultWordLibrary() {
        return tryCreate("/hangman/words.txt");
    }

    protected List<String> getAvailableTargetWords() {
        return availableTargetWords;
    }

    public String getRandomTargetWord() {
        return getAvailableTargetWords().get(random.nextInt(getAvailableTargetWords().size()));
    }

    public int sizeOfTargetWords() {
        return availableTargetWords.size();
    }

    

    public static List<String> readWords(String filePath) throws IOException {
        // First look for the file in the resources directory
        try (var in = WordLibrary.class.getResourceAsStream(filePath); var reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().toList();
        } catch (Exception e) {
            // If not found in resources, try to read from the file system
            try (var lines = Files.lines(Path.of(filePath))) {
                return lines
                        .flatMap(WHITESPACE::splitAsStream)
                        .filter(s -> !s.isBlank())
                        .toList();
            }
        }
    }

    @Override
    public String toString() {
        return "WordLibrary{" +
                ", availableTargetWords=" + availableTargetWords.size() +
                
                '}';
    }

}