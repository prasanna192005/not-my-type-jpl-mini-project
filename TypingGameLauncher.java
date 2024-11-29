public class TypingGameLauncher {
    public static void main(String[] args) {
        try {
            // Start the server
            Process serverProcess = new ProcessBuilder("java", "TypingGameServer").start();

            // Start player clients
            Process player1Process = new ProcessBuilder("java", "TypingGameClient", "Player 1").start();
            Process player2Process = new ProcessBuilder("java", "TypingGameClient", "Player 2").start();
            
            serverProcess.waitFor();
            player1Process.waitFor();
            player2Process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
