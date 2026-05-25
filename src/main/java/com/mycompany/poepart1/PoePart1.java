
package com.mycompany.poepart1;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;



public class PoePart1 {
    
    // Store messages in ArrayLists
    static ArrayList<String> messages = new ArrayList<String>();
    static ArrayList<String> messageIDs = new ArrayList<String>();
    static ArrayList<String> recipients = new ArrayList<String>();
    static int totalMessagesSent = 0;
    static String loggedInUser = "";
    static final String MESSAGES_FILE = "messages.txt";
    
    public static void main(String[] args) throws IOException {
        // Load existing messages from file
        loadMessagesFromFile();
        
        Scanner input = new Scanner(System.in);

        // Ask name
        System.out.print("Enter your first name: ");
        String fullName = input.nextLine();

        // Ask last name
        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        // Ask username
        System.out.print("Enter username (<=5 characters and must contain _): ");
        String username = input.nextLine();
        boolean isValidUsername = checkUsername(username);
        if (!isValidUsername) {
            System.out.println("Username unsuccessful: must be <=5 characters AND contain '_'");
            return;
        }
        System.out.println("Username successfully captured");

        // Ask password
        System.out.print("Enter password (>=8 characters and must contain special character): ");
        String password = input.nextLine();
        boolean isValidPassword = checkPassword(password);
        if (!isValidPassword) {
            System.out.println("Password unsuccessful: must be >=8 characters AND contain special character");
            return;
        }
        System.out.println("Password successfully captured");

        // Ask phone number
        System.out.print("Enter phone number (+27...): ");
        String phone = input.nextLine();
        boolean isValidPhone = checkPhone(phone);
        if (!isValidPhone) {
            System.out.println("Phone number unsuccessful: must start with +27");
            return;
        }
        System.out.println("Phone number successfully captured");

        // Ask user to login
        System.out.println("------------- LOGIN -------------");
        System.out.print("Enter username: ");
        String loginName = input.nextLine();
        System.out.print("Enter password: ");
        String loginPass = input.nextLine();

        // Check if login matches stored details
        if (loginName.equals(username) && loginPass.equals(password)) {
            System.out.println("Login successful, welcome back " + fullName + " " + lastName + "!");
            System.out.println("Your phone number is: " + hidePhone(phone));
            loggedInUser = fullName + " " + lastName;
            
            // Show welcome message and main menu
            showWelcomeScreen();
            
        } else {
            System.out.println("Login failed, please try again.");
        }
    }
    
    // Method to load messages from file
    public static void loadMessagesFromFile() {
        try {
            File file = new File(MESSAGES_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                
                // Read total messages count
                String line = reader.readLine();
                if (line != null && line.startsWith("TOTAL:")) {
                    totalMessagesSent = Integer.parseInt(line.substring(6));
                }
                
                // Clear existing lists
                messages.clear();
                messageIDs.clear();
                recipients.clear();
                
                // Read each message
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("MSG:")) {
                        // Format: MSG:messageID|recipient|content
                        String[] parts = line.substring(4).split("\\|", 3);
                        if (parts.length == 3) {
                            messageIDs.add(parts[0]);
                            recipients.add(parts[1]);
                            messages.add(parts[2]);
                        }
                    }
                }
                
                reader.close();
                System.out.println("Loaded " + messages.size() + " messages from file.");
            }
        } catch (Exception e) {
            System.out.println("No existing messages file found. Starting fresh.");
        }
    }
    
    // Method to save messages to file
    public static void saveMessagesToFile() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(MESSAGES_FILE));
        
        // Write total messages count
        writer.println("TOTAL:" + totalMessagesSent);
        
        // Write each message
        for (int i = 0; i < messages.size(); i++) {
            writer.println("MSG:" + messageIDs.get(i) + "|" + recipients.get(i) + "|" + messages.get(i));
        }
        
        writer.close();
    }
    
    // Method to generate the correct message hash
    public static String generateMessageHash(String messageID, int messageNumber, String messageContent) {
        // Extract first two numbers from message ID
        String firstTwoNumbers = "";
        for (int i = 0; i < messageID.length() && firstTwoNumbers.length() < 2; i++) {
            if (Character.isDigit(messageID.charAt(i))) {
                firstTwoNumbers += messageID.charAt(i);
            }
        }
        
        // If we couldn't find two numbers, pad with zeros
        while (firstTwoNumbers.length() < 2) {
            firstTwoNumbers += "0";
        }
        
        // Extract first and last words from message
        String[] words = messageContent.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        // Create hash: firstTwoNumbers:messageNumber:firstWord:lastWord
        String hash = firstTwoNumbers + ":" + messageNumber + ":" + firstWord + ":" + lastWord;
        
        // Return in uppercase
        return hash.toUpperCase();
    }
    
    // Method to show welcome screen and main menu
    public static void showWelcomeScreen() throws IOException {
        System.out.println("\n=====================================");
        System.out.println("     WELCOME TO QUICKCHAT");
        System.out.println("=====================================");
        System.out.println("Hello " + loggedInUser + "!");
        showMainMenu();
    }
    
    // Main menu method
    public static void showMainMenu() throws IOException {
        Scanner input = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Send New Message");
            System.out.println("2. View All Messages");
            System.out.println("3. Delete Message");
            System.out.println("4. Exit");
            System.out.println("===============================");
            System.out.print("Enter your choice: ");
            choice = input.nextInt();
            input.nextLine(); // consume newline
            
            switch(choice) {
                case 1:
                    sendMessage();
                    break;
                case 2:
                    viewMessages();
                    break;
                case 3:
                    deleteMessage();
                    break;
                case 4:
                    System.out.println("Thank you for using QuickChat! Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while(choice != 4);
    }
    
    // Method to send messages
    public static void sendMessage() throws IOException {
        Scanner input = new Scanner(System.in);
        
        // Ask how many messages they want to send
        System.out.print("\nHow many messages do you want to send? ");
        int numMessages = input.nextInt();
        input.nextLine(); // consume newline
        
        // Show total messages they want to send
        System.out.println("\nYou want to send " + numMessages + " message(s)");
        System.out.println("Total messages sent so far: " + totalMessagesSent);
        System.out.println("Messages you will send now: " + numMessages);
        System.out.println("----------------------------------------");
        
        // Loop to send multiple messages
        for(int i = 1; i <= numMessages; i++) {
            System.out.println("\n--- Message " + i + " of " + numMessages + " ---");
            
            // Get recipient
            System.out.print("Enter recipient cell number: ");
            String recipient = input.nextLine();
            
            // Get message content
            System.out.print("Enter your message (max 250 characters): ");
            String messageContent = input.nextLine();
            
            // Check message length
            if(messageContent.length() > 250) {
                System.out.println("Message exceeds 250 characters! Message not sent.");
                System.out.println("Your message had " + messageContent.length() + " characters.");
                continue;
            }
            
            // Generate unique message ID
            String messageID = generateMessageID();
            
            // Calculate message number (sequential number for this message)
            int messageNumber = totalMessagesSent + 1;
            
            // Generate the correct message hash
            String messageHash = generateMessageHash(messageID, messageNumber, messageContent);
            
            // Store the message
            messages.add(messageContent);
            messageIDs.add(messageID);
            recipients.add(recipient);
            totalMessagesSent++;
            
            // Save to file
            saveMessagesToFile();
            
            // Show message info
            System.out.println("\n✓ Message sent successfully!");
            System.out.println("Message ID: " + messageID);
            System.out.println("Recipient: " + recipient);
            System.out.println("Message Hash: " + messageHash);
            System.out.println("Message length: " + messageContent.length() + "/250 characters");
            System.out.println("Total messages sent overall: " + totalMessagesSent);
            System.out.println("Message " + i + " of " + numMessages + " sent");
        }
        
        // Show summary
        System.out.println("\n=====================================");
        System.out.println("SUMMARY: You sent " + numMessages + " message(s)");
        System.out.println("Total messages in system: " + totalMessagesSent);
        System.out.println("=====================================");
        
        // Return to menu
        returnToMenu();
    }
    
    // Method to view all messages
    public static void viewMessages() throws IOException {
        System.out.println("\n========== ALL MESSAGES ==========");
        System.out.println("Total messages stored: " + totalMessagesSent);
        System.out.println("===================================");
        
        if(messages.isEmpty()) {
            System.out.println("No messages to display!");
        } else {
            for(int i = 0; i < messages.size(); i++) {
                System.out.println("\nMessage #" + (i+1));
                System.out.println("Message ID: " + messageIDs.get(i));
                System.out.println("Recipient: " + recipients.get(i));
                System.out.println("Content: " + messages.get(i));
                // Generate hash for display (using actual message number from file)
                String displayHash = generateMessageHash(messageIDs.get(i), i+1, messages.get(i));
                System.out.println("Hash: " + displayHash);
                System.out.println("Length: " + messages.get(i).length() + "/250");
                System.out.println("---------------------------");
            }
        }
        
        // Return to menu
        returnToMenu();
    }
    
    // Method to delete a message
    public static void deleteMessage() throws IOException {
        Scanner input = new Scanner(System.in);
        
        System.out.println("\n========== DELETE MESSAGE ==========");
        System.out.println("Total messages available: " + totalMessagesSent);
        System.out.println("====================================");
        
        if(messages.isEmpty()) {
            System.out.println("No messages to delete!");
            returnToMenu();
            return;
        }
        
        // Display all messages with numbers
        for(int i = 0; i < messages.size(); i++) {
            System.out.println((i+1) + ". Message ID: " + messageIDs.get(i) + " - To: " + recipients.get(i));
            String preview = messages.get(i).substring(0, Math.min(30, messages.get(i).length()));
            System.out.println("   Content: " + preview + "...");
        }
        
        System.out.print("\nEnter message number to delete (or press 0 to cancel): ");
        int deleteChoice = input.nextInt();
        
        if(deleteChoice == 0) {
            System.out.println("Delete cancelled. Returning to menu...");
        } else if(deleteChoice > 0 && deleteChoice <= messages.size()) {
            // Remove the message
            String removedMessage = messages.get(deleteChoice - 1);
            String removedID = messageIDs.get(deleteChoice - 1);
            messages.remove(deleteChoice - 1);
            messageIDs.remove(deleteChoice - 1);
            recipients.remove(deleteChoice - 1);
            totalMessagesSent--;
            
            // Save updated list to file
            saveMessagesToFile();
            
            System.out.println("\n✓ Message deleted successfully!");
            System.out.println("Deleted message ID: " + removedID);
            System.out.println("Deleted message content: " + removedMessage);
            System.out.println("Remaining messages: " + totalMessagesSent);
        } else {
            System.out.println("Invalid choice! No message deleted.");
        }
        
        // Return to menu
        returnToMenu();
    }
    
    // Method to generate unique message ID
    public static String generateMessageID() {
        String id = "MSG";
        id += System.currentTimeMillis();
        id += (int)(Math.random() * 1000);
        return id;
    }
    
    // Method to return to menu
    public static void returnToMenu() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("\nPress Enter to return to main menu...");
        input.nextLine();
        showMainMenu();
    }
    
    // Original validation methods
    public static boolean checkUsername(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    public static boolean checkPassword(String password) {
        return password.length() >= 8 && password.matches(".*[!@#$%^&*()_\\-+=|\\]\\[{}/?].*");
    }

    public static boolean checkPhone(String phone) {
        return phone.matches("\\+27.*");
    }

    public static String hidePhone(String phone) {
        String hidden = "";
        for (int i = 0; i < phone.length(); i++) {
            if (i < 3) {
                hidden += phone.charAt(i);
            } else {
                hidden += "*";
            }
        }
        return hidden;
    }
}