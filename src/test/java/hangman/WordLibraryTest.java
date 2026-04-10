package hangman;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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

    // @Test
    // void testMakeLibrary
}