package hangman;


import java.beans.Transient;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

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
}