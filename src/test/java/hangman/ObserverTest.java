package hangman;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import hangman.observer.GameObserver;

public class ObserverTest {

    private DefaultGame game;
    private TestObserver observer;

    // A simple test observer that tracks if/how many times it was notified
    private static class TestObserver implements GameObserver {
        int notifyCount = 0;
        HangmanGame lastGame = null;

        @Override
        public void onGuessAttempted(HangmanGame game) {
            notifyCount++;
            lastGame = game;
        }
    }

    @BeforeEach
    void setUp() {
        WordLibrary wordLibrary = WordLibrary.getDefaultWordLibrary()
                .orElseThrow(() -> new RuntimeException("Could not load word library"));
        game = new DefaultGame(6, wordLibrary);
        observer = new TestObserver();
    }

    @Test
    void testObserverNotifiedOnGuessLetter() {
        game.addObserver(observer);
        game.guessLetter('a');
        assertEquals(1, observer.notifyCount);
    }

    @Test
    void testObserverNotifiedOnGuessWord() {
        game.addObserver(observer);
        game.guessWord("wrong");
        assertEquals(1, observer.notifyCount);
    }

    @Test
    void testObserverNotifiedOnReset() {
        game.addObserver(observer);
        game.reset();
        assertEquals(1, observer.notifyCount);
    }

    @Test
    void testObserverReceivesCorrectGame() {
        game.addObserver(observer);
        game.guessLetter('e');
        assertSame(game, observer.lastGame);
    }

    @Test
    void testMultipleObserversNotified() {
        TestObserver observer2 = new TestObserver();
        game.addObserver(observer);
        game.addObserver(observer2);
        game.guessLetter('a');
        assertEquals(1, observer.notifyCount);
        assertEquals(1, observer2.notifyCount);
    }

    @Test
    void testObserverNotNotifiedAfterRemoval() {
        game.addObserver(observer);
        game.guessLetter('a');
        assertEquals(1, observer.notifyCount);

        game.removeObserver(observer);
        game.guessLetter('b');
        assertEquals(1, observer.notifyCount); // still 1, not 2
    }

    @Test
    void testNoObserversDoesNotThrow() {
        // No observers added — should not throw
        assertDoesNotThrow(() -> game.guessLetter('a'));
    }

    @Test
    void testAddNullObserverDoesNotThrow() {
        assertDoesNotThrow(() -> game.addObserver(null));
        // Should not throw when notifying either
        assertDoesNotThrow(() -> game.guessLetter('a'));
    }

    @Test
    void testObserverNotifiedMultipleTimes() {
        game.addObserver(observer);
        game.guessLetter('a');
        game.guessLetter('b');
        game.guessLetter('c');
        assertEquals(3, observer.notifyCount);
    }

    @Test
    void testObserverNotNotifiedOnDuplicateGuess() {
        game.addObserver(observer);
        game.guessLetter('a');
        game.guessLetter('a'); // duplicate — guessLetter returns false, should NOT notify
        assertEquals(1, observer.notifyCount);
    }
}