import java.io.*;
import java.net.*;
import java.util.Random;

public class TypingGameServer {
    private static ServerSocket serverSocket;
    private static Socket player1Socket, player2Socket;
    private static PrintWriter player1Writer, player2Writer;
    private static BufferedReader player1Reader, player2Reader;

    private static final String[] JAVA_PARAGRAPHS = {
        "Writing System.out.println() repeatedly is the ultimate finger workout. Python devs giggle while typing just print().",
        "Garbage collection in Java automatically manages memory allocation and deallocation, reducing memory leaks and simplifying memory management for developers compared to languages like C and C++.",
        "Java's strong typing and robust exception handling mechanisms make it a popular choice for enterprise-level applications, ensuring type safety and providing comprehensive error management.",
        "The Java Collections Framework provides powerful, high-performance implementations of data structures and algorithms, including ArrayList, HashMap, and TreeSet, which simplify complex programming tasks.",
        "Multithreading in Java allows concurrent execution of multiple parts of a program, enabling efficient use of CPU resources and creating responsive, scalable applications."
    };

    private static String currentChallengeParagraph = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(12345);
            System.out.println("Server is running...");

            while (true) {
                // Select random challenge text if not already selected
                if (currentChallengeParagraph == null) {
                    currentChallengeParagraph = getRandomChallengeParagraph();
                }

                // Accept player 1
                player1Socket = serverSocket.accept();
                player1Writer = new PrintWriter(player1Socket.getOutputStream(), true);
                player1Reader = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
                System.out.println("Player 1 connected.");

                // Prompt for Player 1's name
                player1Writer.println("Enter your name:");
                String player1Name = player1Reader.readLine();
                System.out.println("Player 1: " + player1Name);

                // Accept player 2
                player2Socket = serverSocket.accept();
                player2Writer = new PrintWriter(player2Socket.getOutputStream(), true);
                player2Reader = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
                System.out.println("Player 2 connected.");

                // Prompt for Player 2's name
                player2Writer.println("Enter your name:");
                String player2Name = player2Reader.readLine();
                System.out.println("Player 2: " + player2Name);

                // Send current challenge text
                player1Writer.println("CHALLENGE:" + currentChallengeParagraph);
                player2Writer.println("CHALLENGE:" + currentChallengeParagraph);

                // Wait for typed text from both players
                String player1Typed = player1Reader.readLine();
                String player2Typed = player2Reader.readLine();

                // Calculate time for both players
                long player1Time = Long.parseLong(player1Reader.readLine());
                long player2Time = Long.parseLong(player2Reader.readLine());

                // Calculate accuracy for both players
                double player1Accuracy = calculateAccuracy(currentChallengeParagraph, player1Typed);
                double player2Accuracy = calculateAccuracy(currentChallengeParagraph, player2Typed);

                // Calculate WPM for both players
                double player1WPM = calculateWPM(currentChallengeParagraph, player1Typed, player1Time);
                double player2WPM = calculateWPM(currentChallengeParagraph, player2Typed, player2Time);

                // Determine winner
                String winner = determineWinner(player1Name, player2Name, player1WPM, player2WPM, player1Accuracy, player2Accuracy);

                // Send personalized stats
                player1Writer.println("PLAYERSTATS:" + 
                    player1Name + ":" + 
                    String.format("%.2f:%.2f", player1WPM, player1Accuracy) + ":" +
                    (winner.equals(player1Name) ? "WIN" : "LOSS"));
                
                player2Writer.println("PLAYERSTATS:" + 
                    player2Name + ":" + 
                    String.format("%.2f:%.2f", player2WPM, player2Accuracy) + ":" +
                    (winner.equals(player2Name) ? "WIN" : "LOSS"));

                // Send winner indication
                player1Writer.println("WINNER:" + (winner.equals(player1Name) ? "You:" : "Opponent:") + winner);
                player2Writer.println("WINNER:" + (winner.equals(player2Name) ? "You:" : "Opponent:") + winner);

                // Send replay option
                player1Writer.println("REPLAY:Would you like to play again?");
                player2Writer.println("REPLAY:Would you like to play again?");

                // Wait for replay decision
                String player1Replay = player1Reader.readLine();
                String player2Replay = player2Reader.readLine();

                // If both players want to replay, keep the same challenge text
                // If not, select a new challenge text
                if (!player1Replay.equals("REPLAY") || !player2Replay.equals("REPLAY")) {
                    currentChallengeParagraph = null;
                }

                // Close current connections
                player1Socket.close();
                player2Socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ... (rest of the methods remain the same as in the previous version)


    private static String getRandomChallengeParagraph() {
        Random random = new Random();
        return JAVA_PARAGRAPHS[random.nextInt(JAVA_PARAGRAPHS.length)];
    }

    // Existing calculation methods remain the same
    private static double calculateAccuracy(String original, String typed) {
        int correctChars = 0;
        for (int i = 0; i < Math.min(original.length(), typed.length()); i++) {
            if (original.charAt(i) == typed.charAt(i)) {
                correctChars++;
            }
        }
        return (correctChars / (double) original.length()) * 100;
    }

    private static double calculateWPM(String original, String typed, long timeTaken) {
        if (timeTaken <= 0) return 0;
        int standardWordLength = 5;
        int correctChars = 0;
        for (int i = 0; i < Math.min(original.length(), typed.length()); i++) {
            if (original.charAt(i) == typed.charAt(i)) {
                correctChars++;
            }
        }
        double timeInMinutes = timeTaken / 60000.0;
        return timeInMinutes > 0 ? (correctChars / (double) standardWordLength / timeInMinutes) : 0;
    }

    private static String determineWinner(String player1Name, String player2Name, double player1WPM, double player2WPM, double player1Accuracy, double player2Accuracy) {
        double player1Score = player1WPM * player1Accuracy;
        double player2Score = player2WPM * player2Accuracy;

        if (player1Score > player2Score) return player1Name;
        if (player1Score < player2Score) return player2Name;
        return "Draw";
    }
}