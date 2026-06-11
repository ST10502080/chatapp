
package com.mycompany.poepart1;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;



public class PoePart1 {
    
     // Arrays for storing message data (Part 3 requirements)
    public static ArrayList<String> serialMessages = new ArrayList<>();      // Contains all messages sent
    public static ArrayList<String> disconnectedMessages = new ArrayList<>(); // Contains disconnected messages
    public static ArrayList<String> storedMessages = new ArrayList<>();      // Contains stored messages
    public static ArrayList<String> messageHash = new ArrayList<>();         // Contains all message hashes
    public static ArrayList<String> messageID = new ArrayList<>();           // Contains all message IDs
    
    // Additional data structures for tracking message details
    public static ArrayList<String> messageSenders = new ArrayList<>();      // Track senders
    public static ArrayList<String> messageRecipients = new ArrayList<>();   // Track recipients
    public static ArrayList<String> messageContents = new ArrayList<>();     // Track full message content
    
    // File handling
    public static final String MESSAGES_FILE = "messages.txt";
    public static String loggedInUser = "";
    public static int totalMessagesSent = 0;
    
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
                serialMessages.clear();
                messageID.clear();
                messageRecipients.clear();
                messageContents.clear();
                messageHash.clear();
                storedMessages.clear();
                messageSenders.clear();
                
                // Read each message
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("MSG:")) {
                        // Format: MSG:messageID|sender|recipient|content|hash|status
                        String[] parts = line.substring(4).split("\\|", 6);
                        if (parts.length >= 5) {
                            String msgID = parts[0];
                            String sender = parts[1];
                            String recipient = parts[2];
                            String content = parts[3];
                            String hash = parts[4];
                            String status = parts.length > 5 ? parts[5] : "stored";
                            
                            messageID.add(msgID);
                            messageSenders.add(sender);
                            messageRecipients.add(recipient);
                            messageContents.add(content);
                            messageHash.add(hash);
                            serialMessages.add(content);
                            
                            if (status.equals("stored")) {
                                storedMessages.add(content);
                            } else if (status.equals("disconnected")) {
                                disconnectedMessages.add(content);
                            }
                        }
                    }
                }
                
                reader.close();
                System.out.println("Loaded " + serialMessages.size() + " messages from file.");
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
        
        // Write each message with all details
        for (int i = 0; i < serialMessages.size(); i++) {
            String status = "stored";
            if (disconnectedMessages.contains(serialMessages.get(i))) {
                status = "disconnected";
            }
            writer.println("MSG:" + messageID.get(i) + "|" + messageSenders.get(i) + "|" + 
                          messageRecipients.get(i) + "|" + messageContents.get(i) + "|" + 
                          messageHash.get(i) + "|" + status);
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
    
    // Main menu method (updated with 4th option for Stored Messages)
    public static void showMainMenu() throws IOException {
        Scanner input = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Send New Message");
            System.out.println("2. View All Messages");
            System.out.println("3. Delete Message");
            System.out.println("4. Stored Messages Management");  // New option
            System.out.println("5. Exit");
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
                    storedMessagesMenu();  // New method
                    break;
                case 5:
                    System.out.println("Thank you for using QuickChat! Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while(choice != 5);
    }
    
    // NEW: Stored Messages Menu (Part 3 - option 4)
    public static void storedMessagesMenu() throws IOException {
        Scanner input = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n========== STORED MESSAGES ==========");
            System.out.println("1. Display sender and recipient of all stored messages");
            System.out.println("2. Display the longest stored message");
            System.out.println("3. Search for a message by ID");
            System.out.println("4. Search for all messages for a particular recipient");
            System.out.println("5. Delete a message using message hash");
            System.out.println("6. Display full report of all stored messages");
            System.out.println("7. Return to Main Menu");
            System.out.println("======================================");
            System.out.print("Enter your choice: ");
            choice = input.nextInt();
            input.nextLine(); // consume newline
            
            switch(choice) {
                case 1:
                    displaySendersAndRecipients();
                    break;
                case 2:
                    displayLongestMessage();
                    break;
                case 3:
                    searchMessageByID();
                    break;
                case 4:
                    searchMessagesByRecipient();
                    break;
                case 5:
                    deleteMessageByHash();
                    break;
                case 6:
                    displayFullReport();
                    break;
                case 7:
                    System.out.println("Returning to main menu...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
            
            if (choice != 7) {
                System.out.println("\nPress Enter to continue...");
                input.nextLine();
            }
        } while(choice != 7);
    }
    
    // 4a: Display sender and recipient of all stored messages
    public static void displaySendersAndRecipients() {
        System.out.println("\n=== SENDERS AND RECIPIENTS OF STORED MESSAGES ===");
        
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        
        System.out.printf("%-5s %-20s %-20s%n", "No.", "Sender", "Recipient");
        System.out.println("------------------------------------------------");
        
        int displayCount = 0;
        for (int i = 0; i < serialMessages.size(); i++) {
            if (storedMessages.contains(serialMessages.get(i))) {
                displayCount++;
                String sender = messageSenders.get(i);
                String recipient = messageRecipients.get(i);
                System.out.printf("%-5d %-20s %-20s%n", displayCount, sender, recipient);
            }
        }
        System.out.println("Total stored messages: " + displayCount);
    }
    
    // 4b: Display the longest stored message
    public static void displayLongestMessage() {
        System.out.println("\n=== LONGEST STORED MESSAGE ===");
        
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        
        String longestMessage = "";
        int longestIndex = -1;
        int maxLength = 0;
        
        for (int i = 0; i < serialMessages.size(); i++) {
            if (storedMessages.contains(serialMessages.get(i))) {
                String message = messageContents.get(i);
                if (message.length() > maxLength) {
                    maxLength = message.length();
                    longestMessage = message;
                    longestIndex = i;
                }
            }
        }
        
        if (longestIndex != -1) {
            System.out.println("Message ID: " + messageID.get(longestIndex));
            System.out.println("Sender: " + messageSenders.get(longestIndex));
            System.out.println("Recipient: " + messageRecipients.get(longestIndex));
            System.out.println("Length: " + maxLength + " characters");
            System.out.println("Content: " + longestMessage);
        }
    }
    
    // 4c: Search for a message ID and display corresponding recipient and message
    public static void searchMessageByID() {
        Scanner input = new Scanner(System.in);
        System.out.println("\n=== SEARCH MESSAGE BY ID ===");
        System.out.print("Enter Message ID to search: ");
        String searchID = input.nextLine();
        
        boolean found = false;
        for (int i = 0; i < messageID.size(); i++) {
            if (messageID.get(i).equals(searchID) && storedMessages.contains(serialMessages.get(i))) {
                System.out.println("\n✓ Message Found!");
                System.out.println("Message ID: " + messageID.get(i));
                System.out.println("Recipient: " + messageRecipients.get(i));
                System.out.println("Sender: " + messageSenders.get(i));
                System.out.println("Content: " + messageContents.get(i));
                System.out.println("Hash: " + messageHash.get(i));
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.out.println("Message ID not found or message is not stored.");
        }
    }
    
    // 4d: Search for all messages stored for a particular recipient
    public static void searchMessagesByRecipient() {
        Scanner input = new Scanner(System.in);
        System.out.println("\n=== SEARCH MESSAGES BY RECIPIENT ===");
        System.out.print("Enter recipient name/number: ");
        String searchRecipient = input.nextLine();
        
        boolean found = false;
        int count = 0;
        
        System.out.println("\nMessages for recipient: " + searchRecipient);
        System.out.println("=====================================");
        
        for (int i = 0; i < messageRecipients.size(); i++) {
            if (messageRecipients.get(i).toLowerCase().contains(searchRecipient.toLowerCase()) && 
                storedMessages.contains(serialMessages.get(i))) {
                count++;
                System.out.println("\nMessage #" + count);
                System.out.println("Message ID: " + messageID.get(i));
                System.out.println("Sender: " + messageSenders.get(i));
                System.out.println("Content: " + messageContents.get(i));
                System.out.println("Hash: " + messageHash.get(i));
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No messages found for recipient: " + searchRecipient);
        } else {
            System.out.println("\nTotal messages found: " + count);
        }
    }
    
    // 4e: Delete a message using the message hash
    public static void deleteMessageByHash() throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("\n=== DELETE MESSAGE BY HASH ===");
        
        // Display all hashes for reference
        System.out.println("\nAvailable message hashes:");
        System.out.println("------------------------");
        for (int i = 0; i < messageHash.size(); i++) {
            if (storedMessages.contains(serialMessages.get(i))) {
                System.out.println((i+1) + ". " + messageHash.get(i) + " - ID: " + messageID.get(i));
            }
        }
        
        System.out.print("\nEnter message hash to delete: ");
        String hashToDelete = input.nextLine().toUpperCase();
        
        boolean found = false;
        int indexToDelete = -1;
        
        for (int i = 0; i < messageHash.size(); i++) {
            if (messageHash.get(i).equals(hashToDelete) && storedMessages.contains(serialMessages.get(i))) {
                indexToDelete = i;
                found = true;
                break;
            }
        }
        
        if (found && indexToDelete != -1) {
            // Confirm deletion
            System.out.println("\nMessage found:");
            System.out.println("ID: " + messageID.get(indexToDelete));
            System.out.println("Content: " + messageContents.get(indexToDelete));
            System.out.print("Are you sure you want to delete this message? (yes/no): ");
            String confirm = input.nextLine();
            
            if (confirm.equalsIgnoreCase("yes")) {
                // Remove from stored messages
                storedMessages.remove(serialMessages.get(indexToDelete));
                
                System.out.println("\n✓ Message deleted successfully!");
                System.out.println("Deleted hash: " + hashToDelete);
                
                // Save changes to file
                saveMessagesToFile();
            } else {
                System.out.println("Deletion cancelled.");
            }
        } else {
            System.out.println("Message hash not found or message is not in stored messages.");
        }
    }
    
    // 4f: Display a report that lists the full details of all stored messages
    public static void displayFullReport() {
        System.out.println("\n========== FULL STORED MESSAGES REPORT ==========");
        System.out.println("Generated on: " + new Date());
        System.out.println("Total messages in system: " + serialMessages.size());
        System.out.println("Stored messages count: " + storedMessages.size());
        System.out.println("Disconnected messages count: " + disconnectedMessages.size());
        System.out.println("=================================================");
        
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages to display.");
            return;
        }
        
        System.out.printf("\n%-5s %-15s %-15s %-15s %-20s %s%n", 
                         "No.", "Message ID", "Sender", "Recipient", "Hash", "Content Preview");
        System.out.println("----------------------------------------------------------------------------------------");
        
        int displayCount = 0;
        for (int i = 0; i < serialMessages.size(); i++) {
            if (storedMessages.contains(serialMessages.get(i))) {
                displayCount++;
                String preview = messageContents.get(i).substring(0, Math.min(30, messageContents.get(i).length()));
                if (messageContents.get(i).length() > 30) preview += "...";
                
                System.out.printf("%-5d %-15s %-15s %-15s %-20s %s%n", 
                                 displayCount,
                                 messageID.get(i).substring(0, Math.min(15, messageID.get(i).length())),
                                 messageSenders.get(i).substring(0, Math.min(15, messageSenders.get(i).length())),
                                 messageRecipients.get(i).substring(0, Math.min(15, messageRecipients.get(i).length())),
                                 messageHash.get(i).substring(0, Math.min(20, messageHash.get(i).length())),
                                 preview);
            }
        }
        
        System.out.println("\n=== DETAILED MESSAGE CONTENTS ===");
        displayCount = 0;
        for (int i = 0; i < serialMessages.size(); i++) {
            if (storedMessages.contains(serialMessages.get(i))) {
                displayCount++;
                System.out.println("\n--- Message " + displayCount + " ---");
                System.out.println("Message ID: " + messageID.get(i));
                System.out.println("Sender: " + messageSenders.get(i));
                System.out.println("Recipient: " + messageRecipients.get(i));
                System.out.println("Hash: " + messageHash.get(i));
                System.out.println("Content: " + messageContents.get(i));
                System.out.println("Length: " + messageContents.get(i).length() + " characters");
                System.out.println("Status: Stored");
            }
        }
        
        System.out.println("\n=================================================");
        System.out.println("End of Report - Total stored messages: " + displayCount);
    }
    
    // Method to send messages (updated to populate all arrays)
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
            String msgID = generateMessageID();
            
            // Calculate message number (sequential number for this message)
            int messageNumber = totalMessagesSent + 1;
            
            // Generate the correct message hash
            String msgHash = generateMessageHash(msgID, messageNumber, messageContent);
            
            // POPULATE ALL ARRAYS (Part 3 requirement)
            serialMessages.add(messageContent);           // Contains all messages sent
            messageID.add(msgID);                         // Contains all message IDs
            messageHash.add(msgHash);                     // Contains all message hashes
            messageContents.add(messageContent);          // Store full content
            messageSenders.add(loggedInUser);             // Store sender
            messageRecipients.add(recipient);             // Store recipient
            storedMessages.add(messageContent);           // Initially all messages are stored
            totalMessagesSent++;
            
            // Save to file
            saveMessagesToFile();
            
            // Show message info
            System.out.println("\n✓ Message sent successfully!");
            System.out.println("Message ID: " + msgID);
            System.out.println("Recipient: " + recipient);
            System.out.println("Message Hash: " + msgHash);
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
    
    // Method to view all messages (updated)
    public static void viewMessages() throws IOException {
        System.out.println("\n========== ALL MESSAGES ==========");
        System.out.println("Total messages stored: " + totalMessagesSent);
        System.out.println("===================================");
        
        if(serialMessages.isEmpty()) {
            System.out.println("No messages to display!");
        } else {
            for(int i = 0; i < serialMessages.size(); i++) {
                System.out.println("\nMessage #" + (i+1));
                System.out.println("Message ID: " + messageID.get(i));
                System.out.println("Sender: " + messageSenders.get(i));
                System.out.println("Recipient: " + messageRecipients.get(i));
                System.out.println("Content: " + messageContents.get(i));
                System.out.println("Hash: " + messageHash.get(i));
                System.out.println("Length: " + messageContents.get(i).length() + "/250");
                String status = storedMessages.contains(serialMessages.get(i)) ? "Stored" : "Disconnected";
                System.out.println("Status: " + status);
                System.out.println("---------------------------");
            }
        }
        
        // Return to menu
        returnToMenu();
    }
    
    // Method to delete a message (updated)
    public static void deleteMessage() throws IOException {
        Scanner input = new Scanner(System.in);
        
        System.out.println("\n========== DELETE MESSAGE ==========");
        System.out.println("Total messages available: " + totalMessagesSent);
        System.out.println("====================================");
        
        if(serialMessages.isEmpty()) {
            System.out.println("No messages to delete!");
            returnToMenu();
            return;
        }
        
        // Display all messages with numbers
        for(int i = 0; i < serialMessages.size(); i++) {
            System.out.println((i+1) + ". Message ID: " + messageID.get(i) + " - To: " + messageRecipients.get(i));
            String preview = messageContents.get(i).substring(0, Math.min(30, messageContents.get(i).length()));
            System.out.println("   Content: " + preview + "...");
            System.out.println("   Hash: " + messageHash.get(i));
        }
        
        System.out.print("\nEnter message number to delete (or press 0 to cancel): ");
        int deleteChoice = input.nextInt();
        
        if(deleteChoice == 0) {
            System.out.println("Delete cancelled. Returning to menu...");
        } else if(deleteChoice > 0 && deleteChoice <= serialMessages.size()) {
            // Remove the message from all arrays
            String removedMessage = serialMessages.get(deleteChoice - 1);
            String removedID = messageID.get(deleteChoice - 1);
            
            serialMessages.remove(deleteChoice - 1);
            messageID.remove(deleteChoice - 1);
            messageHash.remove(deleteChoice - 1);
            messageContents.remove(deleteChoice - 1);
            messageSenders.remove(deleteChoice - 1);
            messageRecipients.remove(deleteChoice - 1);
            
            // Remove from stored messages if present
            storedMessages.remove(removedMessage);
            
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