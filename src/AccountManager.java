import java.util.Arrays;

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
            // Save all details including the status field: firstName, lastName, username, password, status
            writer.write(firstName + "," + lastName + "," + username + "," + password + ",Not Completed");
            writer.newLine();
            System.out.println("Account successfully saved!");
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }

    public static void updatePlayerStatus(String username) {
        List<String[]> accounts = getAllAccounts(); // Retrieve current accounts
        boolean statusUpdated = false;

        // Iterate through the list of accounts
        for (String[] account : accounts) {
            if (account.length >= 4 && account[2].equals(username)) { // Ensure the account has at least 4 elements (username, password, etc.)
                // Check if the "Avatar" tag is already present at index 4
                if (account.length > 4 && account[4].equals("Avatar")) {
                    System.out.println("Avatar tag already added for " + username);
                    return;  // Exit the method if the tag already exists
                }

                // If the account doesn't already have the "Avatar" tag, add it
                String[] updatedAccount = Arrays.copyOf(account, account.length + 1); // Increase array size by 1
                updatedAccount[4] = "Avatar";  // Add the "Avatar" tag at index 4

                // Replace the old account with the updated one
                accounts.set(accounts.indexOf(account), updatedAccount);
                statusUpdated = true; // Flag as updated
                break; // No need to check further accounts once we've updated the status
            }
        }

        // If status was updated, write the updated list back to the file
        if (statusUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (String[] account : accounts) {
                    writer.write(String.join(",", account));  // Write back the account info
                    writer.newLine();
                }
                writer.flush(); // Ensure data is written to the file
                System.out.println("Player status updated successfully.");
            } catch (IOException e) {
                System.out.println("Error updating player status: " + e.getMessage());
            }
        } else {
            System.out.println("Username not found or Avatar tag already present.");
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
                if (credentials.length >= 4 && credentials[2].equals(username) && credentials[3].equals(password)) {
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