/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poepart1;

/**
 *
 * @author Az'ulwazi
 */
public class Message {
       public static final long serialVersionUID = 1L;
    
    public String messageID;
    public String recipient;
    public String content;
    public String messageHash;
    public int messageNumber;
    public long timestamp;
    
    // Constructor
    public Message(String recipient, String content, int messageNumber) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.content = content;
        this.messageNumber = messageNumber;
        this.timestamp = System.currentTimeMillis();
        this.messageHash = generateMessageHash();
    }
    
    // Constructor for loading from file
    public Message(String messageID, String recipient, String content, int messageNumber, long timestamp) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.content = content;
        this.messageNumber = messageNumber;
        this.timestamp = timestamp;
        this.messageHash = generateMessageHash();
    }
    
    // Generate unique message ID
    private String generateMessageID() {
        String id = "MSG";
        id += System.currentTimeMillis();
        id += (int)(Math.random() * 1000);
        return id;
    }
    
    // Generate message hash
    private String generateMessageHash() {
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
        String[] words = content.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        // Create hash: firstTwoNumbers:messageNumber:firstWord:lastWord
        String hash = firstTwoNumbers + ":" + messageNumber + ":" + firstWord + ":" + lastWord;
        
        // Return in uppercase
        return hash.toUpperCase();
    }
    
    // Getters
    public String getMessageID() {
        return messageID;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getMessageHash() {
        return messageHash;
    }
    
    public int getMessageNumber() {
        return messageNumber;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    // Setters
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.messageHash = generateMessageHash(); // Regenerate hash when content changes
    }
    
    // Display message info
    public void displayMessage() {
        System.out.println("Message ID: " + messageID);
        System.out.println("Recipient: " + recipient);
        System.out.println("Content: " + content);
        System.out.println("Hash: " + messageHash);
        System.out.println("Length: " + content.length() + "/250");
        System.out.println("Timestamp: " + new java.util.Date(timestamp));
    }
    
    public void displayShort() {
        String preview = content.substring(0, Math.min(30, content.length()));
        System.out.println(messageNumber + ". Message ID: " + messageID + " - To: " + recipient);
        System.out.println("   Content: " + preview + "...");
    }
    
    // Check if message is valid (not empty and within length limit)
    public boolean isValid() {
        return content != null && !content.trim().isEmpty() && content.length() <= 250;
    }
    
    @Override
    public String toString() {
        return String.format("Message{id='%s', recipient='%s', content='%s', hash='%s', number=%d}",
            messageID, recipient, content, messageHash, messageNumber);
    }
    
    // Convert to JSON format
    public String toJSON() {
        return String.format(
            "    {\n" +
            "      \"id\": \"%s\",\n" +
            "      \"recipient\": \"%s\",\n" +
            "      \"content\": \"%s\",\n" +
            "      \"messageNumber\": %d,\n" +
            "      \"timestamp\": %d\n" +
            "    }",
            messageID, 
            recipient, 
            content.replace("\"", "\\\""),
            messageNumber,
            timestamp
        );
    }
}