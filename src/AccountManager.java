import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private static final String FILE_PATH = "src/accounts.txt";

    /**
     * Save a new account with first name, last name, username, and password.
     * Ensures that no duplicate usernames exist.
     */
    public static void saveAccount(String firstName, String lastName, String username, String password) {
        if (isUsernameTaken(username)) {
            System.out.println("Error: Username is already taken.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Save all details in CSV format: firstName, lastName, username, password
            writer.write(firstName + "," + lastName + "," + username + "," + password);
            writer.newLine();
            System.out.println("Account successfully saved!");
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }

    /**
     * Validate account credentials by checking the username and password.
     */
    public static boolean isValidAccount(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                // Ensure the record has at least 4 fields: firstName, lastName, username, password
                if (credentials.length == 4 && credentials[2].equals(username) && credentials[3].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading accounts: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if a username is already taken.
     */
    public static boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                // Ensure the record has at least 4 fields: firstName, lastName, username, password
                if (credentials.length == 4 && credentials[2].equals(username)) {
                    return true; // Username already exists
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading accounts: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieve all accounts for debugging or listing purposes.
     */
    public static List<String[]> getAllAccounts() {
        List<String[]> accounts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 4) {
                    accounts.add(credentials);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading accounts: " + e.getMessage());
        }
        return accounts;
    }
}