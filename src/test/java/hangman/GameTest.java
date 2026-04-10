package hangman;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class GameTest {
    // private WordLibrary makeLibrary(Path tempDir, String... words) throws IOException {
    //     Path file = tempDir.resolve("words.txt");
    //     Files.write(file, java.util.List.of(words));
    //     return new WordLibrary(file.toString());
    // }

    // @Test
    // void testGameWithTargetWord(@TempDir Path tempDir) throws IOException {
    //     WordLibrary library = makeLibrary(tempDir, "apple");
    //     DefaultGame game = new DefaultGame(5, library);

    //     assertNotNull(game.getTargetWord());
    //     assertEquals("apple", game.getTargetWord());
    // }

    @Test
    void testGame(){
        WordLibrary library = WordLibrary.getDefaultWordLibrary()
        .orElseThrow(() -> new AssertionError("Default word library failed to load"));
        DefaultGame game = new DefaultGame(5, library);
        System.out.println(game.getTargetWord());
        assertNotNull(game.getTargetWord());
    }
}
