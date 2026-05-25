
package com.mycompany.poepart1;

import java.util.Scanner; // Import Scanner class for keyboard input


public class PoePart2 {

    public static void main(String[] args) {
     
    
        // Ask name
        try (Scanner input = new Scanner(System.in)) {
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
            }   System.out.println("Username successfully captured");
            // Ask password
            System.out.print("Enter password (>=8 characters and must contain special character): ");
            String password = input.nextLine();
            boolean isValidPassword = checkPassword(password);
            if (!isValidPassword) {
                System.out.println("Password unsuccessful: must be >=8 characters AND contain special character");
                return;
            }   System.out.println("Password successfully captured");
            // Ask phone number
            System.out.print("Enter phone number (+27...): ");
            String phone = input.nextLine();
            boolean isValidPhone = checkPhone(phone);
            if (!isValidPhone) {
                System.out.println("Phone number unsuccessful: must start with +27");
                return;
            }   System.out.println("Phone number successfully captured");
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
                
                // NEW: Messaging feature after successful login
                messageMenu(input, fullName); // Call messaging system
            } else {
                System.out.println("Login failed, please try again.");
            }
            // Close Scanner
        }
    }

    // NEW: Messaging menu method
    public static void messageMenu(Scanner input, String fullName) {
        String[] sentMessages = new String[10]; // Array to store sent messages - max 10
        String[] receivedMessages = new String[10]; // Array to store received messages
        int sentCount = 0; // Track number of sent messages
        int receivedCount = 0; // Track number of received messages
        
        // Pre-load some received messages for demo
        receivedMessages[0] = "Hello " + fullName + ", welcome to QuickChat!";
        receivedMessages[1] = "Your account is now active.";
        receivedCount = 2;
        
        int choice = 0;
        // Loop menu until user chooses to exit
        while (choice!= 4) {
            System.out.println("\n--- QuickChat Menu ---");
            System.out.println("1. Send Message");
            System.out.println("2. View Sent Messages");
            System.out.println("3. View Received Messages");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");
            choice = input.nextInt();
            input.nextLine(); // Consume newline after nextInt()
            
            switch (choice) {
                case 1:
                    // Send Message option
                    if (sentCount < 10) {
                        System.out.print("Enter recipient username: ");
                        String recipient = input.nextLine();
                        System.out.print("Enter message: ");
                        String message = input.nextLine();
                        // Store message with recipient info
                        sentMessages[sentCount] = "To: " + recipient + " | Message: " + message;
                        sentCount++;
                        System.out.println("Message sent successfully.");
                    } else {
                        System.out.println("Message inbox full. Cannot send more messages.");
                    }
                    
                    
                case 2:
                    // View Sent Messages option
                    System.out.println("\n--- Sent Messages ---");
                    if (sentCount == 0) {
                        System.out.println("No messages sent yet.");
                    } else {
                        // Loop through sent messages array and display
                        for (int i = 0; i < sentCount; i++) {
                            System.out.println((i + 1) + ". " + sentMessages[i]);
                        }
                    }
                    
                    
                case 3:
                    // View Received Messages option
                    System.out.println("\n--- Received Messages ---");
                    if (receivedCount == 0) {
                        System.out.println("No messages received yet.");
                    } else {
                        // Loop through received messages array and display
                        for (int i = 0; i < receivedCount; i++) {
                            System.out.println((i + 1) + ". " + receivedMessages[i]);
                        }
                    }
                    
                    
                case 4:
                    // Logout option
                    System.out.println("Logging out. Goodbye " + fullName + "!");
                    
                    
                default:
                    System.out.println("Invalid option. Please choose 1-4.");
            }
        }
    }

    public static boolean checkUsername(String username) {
        // Validates username is 5 or fewer chars AND contains underscore
        return username.length() <= 5 && username.contains("_");
    }

    public static boolean checkPassword(String password) {
        // Validates password is 8+ chars AND contains special character
        return password.length() >= 8 && password.matches(".*[!@#$%^&*()_\\-+=|\\]\\[{}/?].*");
    }

    public static boolean checkPhone(String phone) {
        // Validates phone number starts with +27
        return phone.matches("\\+27.*");
    }

    public static String hidePhone(String phone) {
        // Hides phone number except first 3 characters for security
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


    