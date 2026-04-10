package hangman.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import hangman.HangmanGame;
import hangman.WordLibrary;
import hangman.factory.DefaultHangmanGameFactory;
import hangman.factory.HangmanGameFactory;
import hangman.observer.GameObserver;
import hangman.strategy.ComputerGuessStrategy;
import hangman.strategy.GuessStrategy;
import hangman.strategy.HumanGuessStrategy;

public class SwingUI extends JFrame implements GameObserver {
    private static final int DEFAULT_MAX_WRONG_GUESSES = 6;

    private final HangmanGameFactory gameFactory;
    private HangmanGame game;
    private GuessStrategy strategy;

    private final HangmanPanel hangmanPanel = new HangmanPanel();
    private final JLabel wordLabel = new JLabel();
    private final JLabel wrongGuessLabel = new JLabel();
    private final JLabel remainingGuessLabel = new JLabel();
    private final JLabel guessedLettersLabel = new JLabel();
    private final JLabel modeLabel = new JLabel();
    private final JTextField guessField = new JTextField(12);
    private final JButton submitButton = new JButton("Submit Guess");
    private final JButton computerMoveButton = new JButton("Computer Guess");
    private final JButton newGameButton = new JButton("New Game");
    private final JComboBox<String> modeSelector =
            new JComboBox<>(new String[] {"Human", "Random Computer"});
    private final JTextArea statusArea = new JTextArea(8, 26);
    private final JPanel letterPanel = new JPanel(new GridLayout(3, 9, 4, 4));
    private final JButton[] letterButtons = new JButton[26];

    public SwingUI() {
        super("Hangman");

        WordLibrary wordLibrary = WordLibrary.getDefaultWordLibrary()
                .orElseThrow(() -> new IllegalStateException("Could not load default word library."));
        this.gameFactory = new DefaultHangmanGameFactory(wordLibrary, DEFAULT_MAX_WRONG_GUESSES);

        buildUi();
        setStrategy(new HumanGuessStrategy());
        startNewGame();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        wordLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
        wrongGuessLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        remainingGuessLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        guessedLettersLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        modeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        leftPanel.add(wordLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(wrongGuessLabel);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(remainingGuessLabel);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(guessedLettersLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(modeLabel);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(hangmanPanel);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        JPanel topControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topControls.add(new JLabel("Mode:"));
        topControls.add(modeSelector);
        topControls.add(newGameButton);
        topControls.add(computerMoveButton);

        JPanel guessControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guessControls.add(new JLabel("Guess:"));
        guessControls.add(guessField);
        guessControls.add(submitButton);

        buildLetterButtons();

        JPanel centerControls = new JPanel();
        centerControls.setLayout(new BoxLayout(centerControls, BoxLayout.Y_AXIS));
        centerControls.add(guessControls);
        centerControls.add(Box.createVerticalStrut(10));
        centerControls.add(new JLabel("Letter Buttons:"));
        centerControls.add(Box.createVerticalStrut(6));
        centerControls.add(letterPanel);

        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);

        JScrollPane statusScrollPane = new JScrollPane(statusArea);
        statusScrollPane.setPreferredSize(new Dimension(320, 160));

        rightPanel.add(topControls, BorderLayout.NORTH);
        rightPanel.add(centerControls, BorderLayout.CENTER);
        rightPanel.add(statusScrollPane, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        wireEvents();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildLetterButtons() {
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            JButton button = new JButton(String.valueOf(letter));
            button.addActionListener(e -> handleLetterButton(letter));
            letterButtons[i] = button;
            letterPanel.add(button);
        }
    }

    private void wireEvents() {
        submitButton.addActionListener(e -> handleTypedGuess());
        guessField.addActionListener(e -> handleTypedGuess());
        newGameButton.addActionListener(e -> startNewGame());
        computerMoveButton.addActionListener(e -> handleComputerMove());
        modeSelector.addActionListener(e -> handleModeChange());
    }

    private void handleModeChange() {
        if ("Random Computer".equals(modeSelector.getSelectedItem())) {
            setStrategy(new ComputerGuessStrategy());
        } else {
            setStrategy(new HumanGuessStrategy());
        }

        refreshView();
        appendStatus("Mode switched to " + modeSelector.getSelectedItem() + ".");
    }

    private void setStrategy(GuessStrategy strategy) {
        this.strategy = strategy;
    }

    private void startNewGame() {
        if (game != null) {
            game.removeObserver(this);
        }

        game = gameFactory.createGame();
        game.addObserver(this);
        statusArea.setText("");
        appendStatus("New game started.");
        refreshView();
    }

    private void handleTypedGuess() {
        if (!strategy.isHumanControlled()) {
            appendStatus("Typed guesses are disabled in Random Computer mode.");
            return;
        }

        String input = guessField.getText();
        boolean accepted = strategy.submitGuess(game, input);
        guessField.setText("");

        if (!accepted) {
            appendStatus("That guess was not accepted.");
        }
    }

    private void handleLetterButton(char letter) {
        if (!strategy.isHumanControlled()) {
            appendStatus("Letter buttons are disabled in Random Computer mode.");
            return;
        }

        boolean accepted = strategy.submitGuess(game, String.valueOf(Character.toLowerCase(letter)));
        if (!accepted) {
            appendStatus("That guess was not accepted.");
        }
    }

    private void handleComputerMove() {
        if (strategy.isHumanControlled()) {
            appendStatus("Switch to Random Computer mode to use automatic guessing.");
            return;
        }

        strategy.takeTurn(game);
    }

    @Override
    public void onGuessAttempted(HangmanGame game) {
        refreshView();

        if (game.isGameOver()) {
            if (game.playerHasWon()) {
                appendStatus("Game over: you won. The word was " + game.getTargetWord() + ".");
                JOptionPane.showMessageDialog(this,
                        "You won! The word was " + game.getTargetWord() + ".");
            } else {
                appendStatus("Game over: you lost. The word was " + game.getTargetWord() + ".");
                JOptionPane.showMessageDialog(this,
                        "You lost. The word was " + game.getTargetWord() + ".");
            }
        } else {
            appendStatus("Guess accepted. Current word: " + game.getMaskedWord());
        }
    }

    private void refreshView() {
        wordLabel.setText("Word: " + game.getMaskedWord());
        wrongGuessLabel.setText("Wrong guesses: " + game.getWrongGuessCount());
        remainingGuessLabel.setText("Remaining guesses: " + game.getRemainingGuesses());
        guessedLettersLabel.setText("Guessed letters: " + game.getGuessedLetters());
        modeLabel.setText("Current mode: " + modeSelector.getSelectedItem());
        hangmanPanel.setWrongGuessCount(game.getWrongGuessCount());

        boolean humanMode = strategy.isHumanControlled();
        boolean gameOver = game.isGameOver();

        guessField.setEnabled(humanMode && !gameOver);
        submitButton.setEnabled(humanMode && !gameOver);
        computerMoveButton.setEnabled(!humanMode && !gameOver);

        for (JButton button : letterButtons) {
            char lower = Character.toLowerCase(button.getText().charAt(0));
            boolean alreadyGuessed = game.getGuessedLetters().contains(lower);
            button.setEnabled(humanMode && !gameOver && !alreadyGuessed);
        }
    }

    private void appendStatus(String message) {
        if (!statusArea.getText().isEmpty()) {
            statusArea.append("\n");
        }
        statusArea.append(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingUI ui = new SwingUI();
            ui.setVisible(true);
        });
    }
}