package bullsandcows.client;

import telran.net.*;
import telran.view.*;

import java.util.*;

public class BullsAndCowsClientAppl {

    private static final int PORT = 5000;
    private static TcpClient tcpClient;
    private static boolean gameInProgress = false;

    public static void main(String[] args) {
        tcpClient = new TcpClient("localhost", PORT);
        List<Item> items = getMenuItems();
        Menu menu = new Menu("Bulls and Cows Client Application", items.toArray(Item[]::new));
        menu.perform(new SystemInputOutput());
    }

    private static List<Item> getMenuItems() {
        return Arrays.asList(
            Item.of("Start new game", BullsAndCowsClientAppl::startNewGame),
            Item.of("Exit", io -> {
                tcpClient.close();
                System.exit(0);
            })
        );
    }

    private static void startNewGame(InputOutput io) {
        if (gameInProgress) {
            io.writeLine("A game is already in progress!");
            return;
        }

        String response = tcpClient.sendAndReceive(new Request("startGame", ""));
        if (response != null) {
            gameInProgress = true;
            playGame(io);
        } else {
            io.writeLine("Failed to start a new game. Please try again.");
        }
    }

    private static void playGame(InputOutput io) {
        List<String> history = new ArrayList<>();
        while (gameInProgress) {
            String guess = io.readString("Enter 4 unique digits (0-9):");
            if (!isValidGuess(guess)) {
                io.writeLine("Invalid input. Please enter 4 unique digits.");
                continue;
            }

            String response = tcpClient.sendAndReceive(new Request("makeMove", guess));
            if (response != null) {
                history.add(guess + " -> " + response);
                io.writeLine("Result: " + response);

                if (response.equals("4 Bulls")) {
                    io.writeLine("Congratulations! You've guessed the correct sequence!");
                    gameInProgress = false;
                }
            } else {
                io.writeLine("Error in communication with server. Please try again.");
            }
        }

        io.writeLine("Game history:");
        history.forEach(io::writeLine);
    }

    private static boolean isValidGuess(String guess) {
        if (guess.length() != 4) return false;
        Set<Character> uniqueDigits = new HashSet<>();
        for (char c : guess.toCharArray()) {
            if (!Character.isDigit(c) || !uniqueDigits.add(c)) {
                return false;
            }
        }
        return true;
    }
}
