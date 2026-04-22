package hangman;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hangman.factory.DefaultHangmanGameFactory;
import hangman.ui.HangmanPanel;

public class FactoryAndPanelCoverageTest {

    private static class FixedWordLibrary extends WordLibrary {
        private final String fixedWord;

        FixedWordLibrary(String fixedWord) throws IOException {
            super("/hangman/words.txt");
            this.fixedWord = fixedWord;
        }

        @Override
        public String getRandomTargetWord() {
            return fixedWord;
        }
    }

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instanceField = DefaultHangmanGameFactory.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void factoryReturnsSameSingletonInstance() throws IOException {
        WordLibrary library = new FixedWordLibrary("cat");

        DefaultHangmanGameFactory factory1 = DefaultHangmanGameFactory.getInstance(library, 3);
        DefaultHangmanGameFactory factory2 = DefaultHangmanGameFactory.getInstance(library, 3);

        assertSame(factory1, factory2);
    }

    @Test
    void factoryCreatesNewGamesUsingConfiguredValues() throws IOException {
        WordLibrary library = new FixedWordLibrary("cat");
        DefaultHangmanGameFactory factory = DefaultHangmanGameFactory.getInstance(library, 3);

        HangmanGame game1 = factory.createGame();
        HangmanGame game2 = factory.createGame();

        assertNotSame(game1, game2);
        assertEquals("cat", game1.getTargetWord());
        assertEquals(3, game1.getRemainingGuesses());
        assertEquals("cat", game2.getTargetWord());
        assertEquals(3, game2.getRemainingGuesses());
    }

    @Test
    void hangmanPanelHasExpectedPreferredSize() {
        HangmanPanel panel = new HangmanPanel();
        assertEquals(new Dimension(260, 300), panel.getPreferredSize());
    }

    @Test
    void hangmanPanelPaintsWithoutThrowingForAllStages() {
        HangmanPanel panel = new HangmanPanel();
        panel.setSize(260, 300);

        BufferedImage image = new BufferedImage(260, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        try {
            for (int i = 0; i <= 6; i++) {
                int count = i;
                assertDoesNotThrow(() -> {
                    panel.setWrongGuessCount(count);
                    panel.paint(graphics);
                });
            }
        } finally {
            graphics.dispose();
        }
    }
}