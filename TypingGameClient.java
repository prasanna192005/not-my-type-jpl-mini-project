import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class TypingGameClient {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static JFrame frame;
    private static JTextArea challengeArea;
    private static JTextField inputField;
    private static JLabel timerLabel, resultLabel, winnerLabel;
    private static long startTime;
    private static boolean gameInProgress = true;

    public static void main(String[] args) {
        connectToServer();
    }

    private static void connectToServer() {
        String serverAddress = JOptionPane.showInputDialog("Enter server address:");
        int port = 12345;

        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Prompt for player name
            String playerName = JOptionPane.showInputDialog("Enter your name:");
            out.println(playerName);

            setupGameUI();
            listenToServer();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection Error: " + e.getMessage());
        }
    }

    private static void setupGameUI() {
        frame = new JFrame("Typing Speed Challenge");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        challengeArea = new JTextArea();
        challengeArea.setEditable(false);
        challengeArea.setFont(new Font("Consolas", Font.PLAIN, 18));
        challengeArea.setBackground(Color.WHITE);
        challengeArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        challengeArea.setLineWrap(true);
        challengeArea.setWrapStyleWord(true);

        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 16));
        inputField.addActionListener(e -> handleInput());

        JPanel labelPanel = new JPanel(new GridLayout(3, 1));
        timerLabel = new JLabel("Get Ready!", SwingConstants.CENTER);
        resultLabel = new JLabel("Waiting for result...", SwingConstants.CENTER);
        winnerLabel = new JLabel("", SwingConstants.CENTER);
        
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 22));

        labelPanel.add(timerLabel);
        labelPanel.add(resultLabel);
        labelPanel.add(winnerLabel);

        frame.setLayout(new BorderLayout(10, 10));
        frame.add(labelPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(challengeArea), BorderLayout.CENTER);
        frame.add(inputField, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void listenToServer() {
        try {
            String message;
            while (gameInProgress && (message = in.readLine()) != null) {
                final String finalMessage = message;
                SwingUtilities.invokeLater(() -> {
                    if (finalMessage.startsWith("CHALLENGE:")) {
                        challengeArea.setText(finalMessage.substring(10).trim());
                        startTime = System.currentTimeMillis();
                        inputField.setEnabled(true);
                        inputField.requestFocusInWindow();
                    } else if (finalMessage.startsWith("RESULT:")) {
                        resultLabel.setText(finalMessage.substring(7).trim());
                    } else if (finalMessage.startsWith("WINNER:")) {
                        String[] parts = finalMessage.substring(7).split(":");
                        String winnerStatus = parts[0];
                        String winnerName = parts[1];
                        
                        winnerLabel.setText(winnerStatus.equals("You") ? 
                            "YOU WIN! 🏆 " + winnerName : 
                            "OPPONENT WINS! 😔 " + winnerName);
                        
                        frame.getContentPane().setBackground(
                            winnerStatus.equals("You") ? Color.GREEN : Color.RED
                        );
                        
                        inputField.setEnabled(false);
                    } else if (finalMessage.startsWith("REPLAY:")) {
                        int response = JOptionPane.showConfirmDialog(
                            frame, 
                            "Game Over. " + finalMessage.substring(7), 
                            "Play Again?", 
                            JOptionPane.YES_NO_OPTION
                        );
                        
                        if (response == JOptionPane.YES_OPTION) {
                            out.println("REPLAY");
                            resetGameUI();
                        } else {
                            out.println("NO_REPLAY");
                            gameInProgress = false;
                            frame.dispose();
                        }
                    }
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Server Disconnected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void resetGameUI() {
        challengeArea.setText("");
        resultLabel.setText("Waiting for result...");
        winnerLabel.setText("");
        frame.getContentPane().setBackground(new Color(240, 240, 240));
    }

    private static void handleInput() {
        long timeTaken = System.currentTimeMillis() - startTime;
        out.println(inputField.getText());
        out.println(timeTaken);
        inputField.setText("");
        inputField.setEnabled(false);
    }
}