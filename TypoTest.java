import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TypoTest1 extends JFrame {
    private final String[] sentences = {
            "Java is a powerful programming language.",
            "Typing speed improves with regular practice.",
            "Artificial Intelligence is shaping the future.",
            "Developers love solving complex problems.",
            "Consistency is key to mastering any skill.",
            "Fast typing enhances productivity.",
            "Debugging is twice as hard as writing code.",
            "Always write clean and maintainable code."
    };

    private String targetSentence;
    private JTextArea textArea;
    private JLabel headingLabel, subHeadingLabel, sentenceLabel, timerLabel, wpmLabel, accuracyLabel;
    private JButton startButton, endButton, restartButton, themeButton, clearScoresButton;
    private JList<String> scoreList;
    private DefaultListModel<String> scoreModel;
    private boolean darkTheme = false;

    private Timer timer;
    private int elapsedSeconds = 0;
    private boolean started = false;

    public TypoTest1() {
        setTitle("TypoTest");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        initComponents();
        applyTheme();
        prepareNewTest();
        setVisible(true);
    }

    private void initComponents() {
        // Heading
        JPanel headingPanel = new JPanel(new GridLayout(2, 1));
        headingLabel = new JLabel("TypoTest", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 32));
        subHeadingLabel = new JLabel("Type as fast as you can", SwingConstants.CENTER);
        subHeadingLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        headingPanel.add(headingLabel);
        headingPanel.add(subHeadingLabel);
        add(headingPanel, BorderLayout.NORTH);

        // Sentence Label
        sentenceLabel = new JLabel("", SwingConstants.CENTER);
        sentenceLabel.setFont(new Font("Serif", Font.BOLD, 20));

        // Text Area
        textArea = new JTextArea(5, 30);
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEnabled(false);
        JScrollPane textScrollPane = new JScrollPane(textArea);

        // Result Labels
        timerLabel = new JLabel("Time: 0 sec");
        wpmLabel = new JLabel("WPM: 0");
        accuracyLabel = new JLabel("Accuracy: 0%");
        JPanel resultPanel = new JPanel(new FlowLayout());
        resultPanel.add(timerLabel);
        resultPanel.add(wpmLabel);
        resultPanel.add(accuracyLabel);

        // Buttons
        startButton = createStyledButton("Start Test", Color.GREEN);
        startButton.addActionListener(e -> startTest());

        endButton = createStyledButton("End Test", Color.RED);
        endButton.addActionListener(e -> endTest());
        endButton.setVisible(false);

        restartButton = createStyledButton("Restart", Color.BLUE);
        restartButton.addActionListener(e -> startTest());
        restartButton.setVisible(false);

        themeButton = createStyledButton("Toggle Theme", Color.GRAY);
        themeButton.addActionListener(e -> toggleTheme());

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(startButton);
        controlPanel.add(endButton);
        controlPanel.add(restartButton);
        controlPanel.add(themeButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Scoreboard
        scoreModel = new DefaultListModel<>();
        scoreList = new JList<>(scoreModel);
        scoreList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scoreScrollPane = new JScrollPane(scoreList);

        JLabel scoreLabel = new JLabel("Scoreboard", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

        clearScoresButton = createStyledButton("Clear Scores", Color.DARK_GRAY);
        clearScoresButton.addActionListener(e -> scoreModel.clear());

        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        scorePanel.setPreferredSize(new Dimension(250, 0));
        scorePanel.add(scoreLabel, BorderLayout.NORTH);
        scorePanel.add(scoreScrollPane, BorderLayout.CENTER);
        scorePanel.add(clearScoresButton, BorderLayout.SOUTH);

        // Center Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(sentenceLabel, BorderLayout.NORTH);
        centerPanel.add(textScrollPane, BorderLayout.CENTER);
        centerPanel.add(resultPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
        add(scorePanel, BorderLayout.EAST);

        // Timer setup
        timer = new Timer(1000, e -> {
            elapsedSeconds++;
            timerLabel.setText("Time: " + elapsedSeconds + " sec");
        });

        // Key event for early submission
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && started) {
                    e.consume();
                    endTest();
                }
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker()));
        button.setPreferredSize(new Dimension(140, 40));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2, true));
        return button;
    }

    private void prepareNewTest() {
        targetSentence = sentences[new Random().nextInt(sentences.length)];
        sentenceLabel.setText(targetSentence);
        textArea.setText("");
        timerLabel.setText("Time: 0 sec");
        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 0%");
        textArea.setEnabled(false);
        startButton.setVisible(true);
        endButton.setVisible(false);
        restartButton.setVisible(false);
    }

    private void startTest() {
        prepareNewTest();
        textArea.setEnabled(true);
        textArea.requestFocus();
        elapsedSeconds = 0;
        timer.start();
        started = true;
        startButton.setVisible(false);
        endButton.setVisible(true);
        restartButton.setVisible(true);
    }

    private void endTest() {
        timer.stop();
        started = false;

        String userInput = textArea.getText().trim();
        String[] originalWords = targetSentence.split(" ");
        String[] typedWords = userInput.split(" ");

        int correctWords = 0;
        for (int i = 0; i < Math.min(originalWords.length, typedWords.length); i++) {
            if (originalWords[i].equals(typedWords[i])) {
                correctWords++;
            }
        }

        double wpm = (correctWords / (elapsedSeconds / 60.0));
        int correctChars = 0;
        for (int i = 0; i < Math.min(targetSentence.length(), userInput.length()); i++) {
            if (targetSentence.charAt(i) == userInput.charAt(i)) {
                correctChars++;
            }
        }
        double accuracy = ((double) correctChars / targetSentence.length()) * 100;

        wpmLabel.setText("WPM: " + (int) wpm);
        accuracyLabel.setText("Accuracy: " + (int) accuracy + "%");

        scoreModel.addElement("WPM: " + (int) wpm + " | Accuracy: " + (int) accuracy + "%");

        textArea.setEnabled(false);
        startButton.setVisible(true);
        endButton.setVisible(false);
        restartButton.setVisible(false);
    }

    private void toggleTheme() {
        darkTheme = !darkTheme;
        applyTheme();
    }

    private void applyTheme() {


        Color bg = darkTheme ? new Color(34, 34, 34) : Color.WHITE;
        Color fg = darkTheme ? Color.WHITE : Color.BLACK;
        Color boxBg = darkTheme ? new Color(50, 50, 50) : Color.WHITE;
        Color panelBg = darkTheme ? new Color(45, 45, 45) : Color.LIGHT_GRAY;

        textArea.setBackground(boxBg);
        textArea.setForeground(fg);

        scoreList.setBackground(boxBg);
        scoreList.setForeground(fg);

        getContentPane().setBackground(bg);
        for (Component c : getContentPane().getComponents()) {
            updateComponentTheme(c, bg, fg, boxBg, panelBg);
        }
    }

    private void updateComponentTheme(Component c, Color bg, Color fg, Color boxBg, Color panelBg) {
        if (c instanceof JPanel) {
            c.setBackground(panelBg);
            for (Component child : ((Container) c).getComponents()) {
                updateComponentTheme(child, bg, fg, boxBg, panelBg);
            }
        } else if (c instanceof JScrollPane) {
            c.setBackground(panelBg);
        } else if (c instanceof JLabel) {
            c.setForeground(fg);
        } else if (c instanceof JButton) {
            c.setForeground(Color.WHITE);
        } else if (c instanceof JTextArea) {
            c.setBackground(boxBg);
            c.setForeground(fg);
        } else if (c instanceof JList) {
            c.setBackground(boxBg);
            c.setForeground(fg);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TypoTest1::new);
    }
}
