package hangman;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hangman.factory.DefaultHangmanGameFactory;

class WordLibraryTest {

    @Test 
    void testWordLibrary() throws IOException {
        WordLibrary wordLibrary = WordLibrary.getDefaultWordLibrary()
        .orElseThrow(() -> new AssertionError("Default word library failed to load"));

        assertNotNull(wordLibrary);
        assertTrue(wordLibrary.sizeOfTargetWords() > 0);
    }

    @Test
    void testTargetWord() {
        WordLibrary wordLibrary = WordLibrary.getDefaultWordLibrary()
        .orElseThrow(() -> new AssertionError("Default word library failed to load"));

        String word = wordLibrary.getRandomTargetWord();

        assertNotNull(word);
        assertFalse(word.isBlank());
    }

    @Test
    void testWordLibraryLoadsWords() throws IOException {
        WordLibrary wordLibrary = WordLibrary.getDefaultWordLibrary()
        .orElseThrow(() -> new AssertionError("Default word library failed to load"));
        assertTrue(wordLibrary.sizeOfTargetWords() > 0);
    }

    @Test
    void testRandomTargetWordComesFromLibrary() throws IOException {
        WordLibrary wordLibrary = WordLibrary.getDefaultWordLibrary()
        .orElseThrow(() -> new AssertionError("Default word library failed to load"));

        String word = wordLibrary.getRandomTargetWord();

        assertTrue(wordLibrary.getAvailableTargetWords().contains(word));
    }

    @Test
    void testMissingFileThrowsIOException() {
        assertThrows(IOException.class, () -> new WordLibrary("not-a-real-file.txt"));
    }

    @Test
    void testSingletonReturnsSameInstance() {
        WordLibrary library = WordLibrary.getDefaultWordLibrary()
            .orElseThrow(() -> new AssertionError("Default word library failed to load"));
        
        DefaultHangmanGameFactory factory1 = DefaultHangmanGameFactory.getInstance(library, 5);
        DefaultHangmanGameFactory factory2 = DefaultHangmanGameFactory.getInstance(library, 5);
        
        assertSame(factory1, factory2);
    }

    @TempDir
    Path tempDir;

    @Test
    void readWordsSplitsWhitespaceFromFilesystemFile() throws IOException {
        Path file = tempDir.resolve("words.txt");
        Files.writeString(file, "alpha beta\n\ngamma\t delta");

        List<String> words = WordLibrary.readWords(file.toString());

        assertEquals(List.of("alpha", "beta", "gamma", "delta"), words);
    }

    @Test
    void constructorLoadsFilesystemWordsAndToStringIncludesSize() throws IOException {
        Path file = tempDir.resolve("words.txt");
        Files.writeString(file, "one two\nthree");

        WordLibrary library = new WordLibrary(file.toString());

        assertEquals(3, library.sizeOfTargetWords());
        assertTrue(library.toString().contains("availableTargetWords=3"));
    }
}