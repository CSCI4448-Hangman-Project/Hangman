package hangman;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class GameTest {
    @Test
    void testGame(){
        WordLibrary library = WordLibrary.getDefaultWordLibrary()
        .orElseThrow(() -> new AssertionError("Default word library failed to load"));
        DefaultGame game = new DefaultGame(5, library);
        System.out.println(game.getTargetWord());
        assertNotNull(game.getTargetWord());
    }
}
