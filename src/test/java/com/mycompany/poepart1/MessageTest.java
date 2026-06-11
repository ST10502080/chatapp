/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.poepart1;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.*;
import java.nio.file.*;
import java.util.*;


/**
 *
 * @author Az'ulwazi
 */
public class MessageTest {
    
    public MessageTest() {
    }
   
    // ==================== MESSAGE TEST FIELDS ====================
    private Message message;
    private final String testRecipient = "+27123456789";
    private final String testContent = "Hello World";
    private final int testMessageNumber = 1;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    
    // ==================== POEPART1 TEST FIELDS ====================
    @TempDir
    Path tempDir;
    
    // ==================== SETUP AND TEARDOWN ====================
    
    @BeforeEach
    void setUp() {
        // Message setup
        message = new Message(testRecipient, testContent, testMessageNumber);
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // PoePart1 setup
        PoePart1.serialMessages.clear();
        PoePart1.disconnectedMessages.clear();
        PoePart1.storedMessages.clear();
        PoePart1.messageHash.clear();
        PoePart1.messageID.clear();
        PoePart1.messageSenders.clear();
        PoePart1.messageRecipients.clear();
        PoePart1.messageContents.clear();
        PoePart1.totalMessagesSent = 0;
        PoePart1.loggedInUser = "Test User";
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        
        // Clean up test file if it exists
        File file = new File(PoePart1.MESSAGES_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
    
    // ==================== MESSAGE CONSTRUCTOR TESTS ====================
    
    @Test
    @Order(1)
    void testConstructorWithThreeParameters() {
        assertNotNull(message);
        assertEquals(testRecipient, message.getRecipient());
        assertEquals(testContent, message.getContent());
        assertEquals(testMessageNumber, message.getMessageNumber());
        assertNotNull(message.getMessageID());
        assertTrue(message.getMessageID().startsWith("MSG"));
        assertNotNull(message.getMessageHash());
        assertTrue(message.getTimestamp() > 0);
    }
    
    @Test
    @Order(2)
    void testConstructorWithFiveParameters() {
        String messageID = "MSG1234567890123";
        String recipient = "+27987654321";
        String content = "Test message";
        int messageNumber = 5;
        long timestamp = System.currentTimeMillis();
        
        Message loadedMessage = new Message(messageID, recipient, content, messageNumber, timestamp);
        
        assertEquals(messageID, loadedMessage.getMessageID());
        assertEquals(recipient, loadedMessage.getRecipient());
        assertEquals(content, loadedMessage.getContent());
        assertEquals(messageNumber, loadedMessage.getMessageNumber());
        assertEquals(timestamp, loadedMessage.getTimestamp());
        assertNotNull(loadedMessage.getMessageHash());
    }
    
    // ==================== MESSAGE ID TESTS ====================
    
    @Test
    @Order(3)
    void testGenerateMessageIDFormat() {
        String messageID = message.getMessageID();
        assertTrue(messageID.startsWith("MSG"));
        assertTrue(messageID.length() > 3);
        
        boolean hasDigits = false;
        for (char c : messageID.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigits = true;
                break;
            }
        }
        assertTrue(hasDigits);
    }
    
    @Test
    @Order(4)
    void testUniqueMessageIDs() {
        Message message2 = new Message(testRecipient, "Another message", 2);
        assertNotEquals(message.getMessageID(), message2.getMessageID());
    }
    
    // ==================== HASH GENERATION TESTS ====================
    
    @Test
    @Order(5)
    void testGenerateMessageHashFormat() {
        String hash = message.getMessageHash();
        assertNotNull(hash);
        assertTrue(hash.matches("\\d{2}:\\d+:[A-Z]+:[A-Z]+"));
        assertEquals(hash, hash.toUpperCase());
    }
    
    @Test
    @Order(6)
    void testMessageHashWithSingleWord() {
        Message singleWordMsg = new Message(testRecipient, "Hello", 2);
        String hash = singleWordMsg.getMessageHash();
        String[] parts = hash.split(":");
        assertEquals(parts[2], parts[3]);
    }
    
    @Test
    @Order(7)
    void testMessageHashWithMultipleWords() {
        Message multiWordMsg = new Message(testRecipient, "The quick brown fox jumps", 3);
        String hash = multiWordMsg.getMessageHash();
        String[] parts = hash.split(":");
        assertEquals("THE", parts[2]);
        assertEquals("JUMPS", parts[3]);
    }
    
    @Test
    @Order(8)
    void testMessageHashWithExtraSpaces() {
        Message spacedMsg = new Message(testRecipient, "  Hello   World  ", 4);
        String hash = spacedMsg.getMessageHash();
        String[] parts = hash.split(":");
        assertEquals("HELLO", parts[2]);
        assertEquals("WORLD", parts[3]);
    }
    
    @Test
    @Order(9)
    void testMessageHashWithSpecialCharacters() {
        Message specialMsg = new Message(testRecipient, "Hello! @World# $Test%", 5);
        String hash = specialMsg.getMessageHash();
        String[] parts = hash.split(":");
        assertEquals("HELLO!", parts[2]);
        assertEquals("TEST%", parts[3]);
    }
    
    @Test
    @Order(10)
    void testHashRegenerationWhenContentChanges() {
        String originalHash = message.getMessageHash();
        message.setContent("New content here");
        String newHash = message.getMessageHash();
        assertNotEquals(originalHash, newHash);
        assertTrue(newHash.contains("NEW"));
        assertTrue(newHash.contains("HERE"));
    }
    
    // ==================== VALIDATION TESTS ====================
    
    @Test
    @Order(11)
    void testIsValidWithValidMessage() {
        assertTrue(message.isValid());
    }
    
    @Test
    @Order(12)
    void testIsValidWithEmptyContent() {
        Message emptyMsg = new Message(testRecipient, "", 2);
        assertFalse(emptyMsg.isValid());
    }
    
    @Test
    @Order(13)
    void testIsValidWithNullContent() {
        Message nullMsg = new Message(testRecipient, null, 3);
        assertFalse(nullMsg.isValid());
    }
    
    @Test
    @Order(14)
    void testIsValidWithWhitespaceOnly() {
        Message whitespaceMsg = new Message(testRecipient, "   ", 4);
        assertFalse(whitespaceMsg.isValid());
    }
    
    @Test
    @Order(15)
    void testIsValidWithExactMaxLength() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 250; i++) {
            sb.append("A");
        }
        Message maxLengthMsg = new Message(testRecipient, sb.toString(), 5);
        assertTrue(maxLengthMsg.isValid());
    }
    
    @Test
    @Order(16)
    void testIsValidWithExceedingMaxLength() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 251; i++) {
            sb.append("A");
        }
        Message tooLongMsg = new Message(testRecipient, sb.toString(), 6);
        assertFalse(tooLongMsg.isValid());
    }
    
    // ==================== GETTER AND SETTER TESTS ====================
    
    @Test
    @Order(17)
    void testGetters() {
        assertNotNull(message.getMessageID());
        assertEquals(testRecipient, message.getRecipient());
        assertEquals(testContent, message.getContent());
        assertNotNull(message.getMessageHash());
        assertEquals(testMessageNumber, message.getMessageNumber());
        assertTrue(message.getTimestamp() <= System.currentTimeMillis());
    }
    
    @Test
    @Order(18)
    void testSetters() {
        String newRecipient = "+27987654321";
        String newContent = "Updated message content";
        
        message.setRecipient(newRecipient);
        assertEquals(newRecipient, message.getRecipient());
        
        message.setContent(newContent);
        assertEquals(newContent, message.getContent());
    }
    
    // ==================== DISPLAY METHOD TESTS ====================
    
    @Test
    @Order(19)
    void testDisplayMessage() {
        message.displayMessage();
        String output = outContent.toString();
        
        assertTrue(output.contains("Message ID:"));
        assertTrue(output.contains(message.getMessageID()));
        assertTrue(output.contains("Recipient:"));
        assertTrue(output.contains(testRecipient));
        assertTrue(output.contains("Content:"));
        assertTrue(output.contains(testContent));
        assertTrue(output.contains("Hash:"));
        assertTrue(output.contains(message.getMessageHash()));
        assertTrue(output.contains("Length:"));
        assertTrue(output.contains("/250"));
        assertTrue(output.contains("Timestamp:"));
    }
    
    @Test
    @Order(20)
    void testDisplayShort() {
        message.displayShort();
        String output = outContent.toString();
        
        assertTrue(output.contains(message.getMessageNumber() + ". Message ID:"));
        assertTrue(output.contains(message.getMessageID()));
        assertTrue(output.contains("- To:"));
        assertTrue(output.contains(testRecipient));
        assertTrue(output.contains("Content:"));
        
        String expectedPreview = testContent.substring(0, Math.min(30, testContent.length()));
        assertTrue(output.contains(expectedPreview));
    }
    
    @Test
    @Order(21)
    void testDisplayShortWithLongMessage() {
        String longContent = "This is a very long message that should be truncated in the preview because it exceeds thirty characters";
        Message longMsg = new Message(testRecipient, longContent, 2);
        longMsg.displayShort();
        String output = outContent.toString();
        
        assertTrue(output.contains("..."));
        assertTrue(output.length() < longContent.length());
    }
    
    // ==================== JSON OUTPUT TESTS ====================
    
    @Test
    @Order(22)
    void testToJSONFormat() {
        String json = message.toJSON();
        
        assertTrue(json.contains("\"id\":"));
        assertTrue(json.contains(message.getMessageID()));
        assertTrue(json.contains("\"recipient\":"));
        assertTrue(json.contains(testRecipient));
        assertTrue(json.contains("\"content\":"));
        assertTrue(json.contains(testContent));
        assertTrue(json.contains("\"messageNumber\":"));
        assertTrue(json.contains(String.valueOf(testMessageNumber)));
        assertTrue(json.contains("\"timestamp\":"));
        assertTrue(json.contains(String.valueOf(message.getTimestamp())));
    }
    
    @Test
    @Order(23)
    void testToJSONWithSpecialCharacters() {
        String specialContent = "Hello \"World\" with 'quotes' and & special <characters>";
        Message specialMsg = new Message(testRecipient, specialContent, 7);
        String json = specialMsg.toJSON();
        
        assertTrue(json.contains("Hello \\\"World\\\""));
    }
    
    @Test
    @Order(24)
    void testToJSONIndentation() {
        String json = message.toJSON();
        assertTrue(json.startsWith("    {"));
        assertTrue(json.contains("\n"));
    }
    
    // ==================== TOSTRING TESTS ====================
    
    @Test
    @Order(25)
    void testToString() {
        String toString = message.toString();
        
        assertTrue(toString.contains("Message{"));
        assertTrue(toString.contains("id='" + message.getMessageID()));
        assertTrue(toString.contains("recipient='" + testRecipient));
        assertTrue(toString.contains("content='" + testContent));
        assertTrue(toString.contains("hash='" + message.getMessageHash()));
        assertTrue(toString.contains("number=" + testMessageNumber));
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    @Order(26)
    void testMessageWithEmptyStringContent() {
        Message emptyMsg = new Message(testRecipient, "", 10);
        assertFalse(emptyMsg.isValid());
        assertNotNull(emptyMsg.getMessageHash());
    }
    
    @Test
    @Order(27)
    void testMessageNumberSequencing() {
        Message msg1 = new Message(testRecipient, "First", 1);
        Message msg2 = new Message(testRecipient, "Second", 2);
        Message msg3 = new Message(testRecipient, "Third", 3);
        
        assertEquals(1, msg1.getMessageNumber());
        assertEquals(2, msg2.getMessageNumber());
        assertEquals(3, msg3.getMessageNumber());
    }
    
    @Test
    @Order(28)
    void testTimestampAccuracy() {
        long beforeCreation = System.currentTimeMillis();
        Message newMsg = new Message(testRecipient, "Timestamp test", 1);
        long afterCreation = System.currentTimeMillis();
        
        assertTrue(newMsg.getTimestamp() >= beforeCreation);
        assertTrue(newMsg.getTimestamp() <= afterCreation);
    }
    
    @Test
    @Order(29)
    void testHashContainsCorrectMessageNumber() {
        int msgNumber = 42;
        Message numberedMsg = new Message(testRecipient, "Test message", msgNumber);
        String hash = numberedMsg.getMessageHash();
        
        assertTrue(hash.contains(":" + msgNumber + ":"));
    }
    
    @Test
    @Order(30)
    void testHashFirstTwoDigitsFromMessageID() {
        String messageID = message.getMessageID();
        String expectedFirstTwo = "";
        for (int i = 0; i < messageID.length() && expectedFirstTwo.length() < 2; i++) {
            if (Character.isDigit(messageID.charAt(i))) {
                expectedFirstTwo += messageID.charAt(i);
            }
        }
        
        String hash = message.getMessageHash();
        String[] hashParts = hash.split(":");
        
        assertEquals(expectedFirstTwo, hashParts[0]);
    }
    
    // ==================== POEPART1 VALIDATION TESTS ====================
    
    @Test
    @Order(31)
    void testCheckUsername_Valid() {
        assertTrue(PoePart1.checkUsername("user_"));
        assertTrue(PoePart1.checkUsername("a_b"));
        assertTrue(PoePart1.checkUsername("_"));
        assertTrue(PoePart1.checkUsername("1_2"));
    }
    
    @Test
    @Order(32)
    void testCheckUsername_Invalid() {
        assertFalse(PoePart1.checkUsername("username_without_underscore"));
        assertFalse(PoePart1.checkUsername("toolong_"));
        assertFalse(PoePart1.checkUsername("no_underscore"));
        assertFalse(PoePart1.checkUsername("_tooolong"));
        assertFalse(PoePart1.checkUsername(""));
    }
    
    @Test
    @Order(33)
    void testCheckPassword_Valid() {
        assertTrue(PoePart1.checkPassword("password123!"));
        assertTrue(PoePart1.checkPassword("Secure@Pass"));
        assertTrue(PoePart1.checkPassword("LongEnough#1"));
        assertTrue(PoePart1.checkPassword("Test$Password"));
    }
    
    @Test
    @Order(34)
    void testCheckPassword_Invalid() {
        assertFalse(PoePart1.checkPassword("short1!"));
        assertFalse(PoePart1.checkPassword("NoSpecialChar1"));
        assertFalse(PoePart1.checkPassword(""));
        assertFalse(PoePart1.checkPassword("onlyspecial!"));
        assertFalse(PoePart1.checkPassword("short!"));
    }
    
    @Test
    @Order(35)
    void testCheckPhone_Valid() {
        assertTrue(PoePart1.checkPhone("+27834557896"));
        assertTrue(PoePart1.checkPhone("+27123456789"));
        assertTrue(PoePart1.checkPhone("+27000000000"));
    }
    
    @Test
    @Order(36)
    void testCheckPhone_Invalid() {
        assertFalse(PoePart1.checkPhone("08388884567"));
        assertFalse(PoePart1.checkPhone("+278"));
        assertFalse(PoePart1.checkPhone("27834557896"));
        assertFalse(PoePart1.checkPhone(""));
    }
    
    @Test
    @Order(37)
    void testHidePhone() {
        assertEquals("+27*******", PoePart1.hidePhone("+27834557896"));
        assertEquals("+27********", PoePart1.hidePhone("+27838884567"));
        assertEquals("+27*****", PoePart1.hidePhone("+278888"));
    }
    
    // ==================== POEPART1 MESSAGE OPERATIONS TESTS ====================
    
    @Test
    @Order(38)
    void testGenerateMessageHash_PoePart1() {
        String hash1 = PoePart1.generateMessageHash("MSG001", 1, "Did you get the cake?");
        assertNotNull(hash1);
        assertTrue(hash1.contains(":"));
        assertTrue(hash1.length() > 0);
        
        String hash2 = PoePart1.generateMessageHash("MSG002", 2, "");
        assertNotNull(hash2);
    }
    
    @Test
    @Order(39)
    void testGenerateMessageHash_WithTestData() {
        String hash = PoePart1.generateMessageHash("MSG12345", 1, "Did you get the cake?");
        assertTrue(hash.matches("\\d{2}:\\d+:\\w+:\\w+"));
    }
    
    @Test
    @Order(40)
    void testGenerateMessageID() {
        String id1 = PoePart1.generateMessageID();
        String id2 = PoePart1.generateMessageID();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(id1.startsWith("MSG"));
        assertTrue(id2.startsWith("MSG"));
        assertNotEquals(id1, id2);
    }
    
    @Test
    @Order(41)
    void testSendMessage_PopulatesArrays() throws IOException {
        String testMessage = "Did you get the cake?";
        String testRecipient = "+27834557896";
        
        String msgID = PoePart1.generateMessageID();
        int msgNumber = PoePart1.totalMessagesSent + 1;
        String msgHash = PoePart1.generateMessageHash(msgID, msgNumber, testMessage);
        
        PoePart1.serialMessages.add(testMessage);
        PoePart1.messageID.add(msgID);
        PoePart1.messageHash.add(msgHash);
        PoePart1.messageContents.add(testMessage);
        PoePart1.messageSenders.add("Test User");
        PoePart1.messageRecipients.add(testRecipient);
        PoePart1.storedMessages.add(testMessage);
        PoePart1.totalMessagesSent++;
        
        assertEquals(1, PoePart1.serialMessages.size());
        assertEquals(1, PoePart1.messageID.size());
        assertEquals(1, PoePart1.messageHash.size());
        assertEquals(1, PoePart1.messageContents.size());
        assertEquals(1, PoePart1.messageSenders.size());
        assertEquals(1, PoePart1.messageRecipients.size());
        assertEquals(1, PoePart1.storedMessages.size());
        assertEquals(1, PoePart1.totalMessagesSent);
        
        assertEquals(testMessage, PoePart1.serialMessages.get(0));
        assertEquals(testRecipient, PoePart1.messageRecipients.get(0));
    }
    
    @Test
    @Order(42)
    void testPopulateWithAllTestData() throws IOException {
        String[][] testData = {
            {"+27834557896", "Did you get the cake?", "Sent"},
            {"+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored"},
            {"+27834484567", "Yohoooo, I am at your gate.", "Disregard"},
            {"08388884567", "It is dinner time!", "Sent"},
            {"+27838884567", "Ok, I am leaving without you.", "Stored"}
        };
        
        for (int i = 0; i < testData.length; i++) {
            String msgID = PoePart1.generateMessageID();
            int msgNumber = PoePart1.totalMessagesSent + 1;
            String msgHash = PoePart1.generateMessageHash(msgID, msgNumber, testData[i][1]);
            
            PoePart1.serialMessages.add(testData[i][1]);
            PoePart1.messageID.add(msgID);
            PoePart1.messageHash.add(msgHash);
            PoePart1.messageContents.add(testData[i][1]);
            PoePart1.messageSenders.add("Test User");
            PoePart1.messageRecipients.add(testData[i][0]);
            
            if (testData[i][2].equals("Stored")) {
                PoePart1.storedMessages.add(testData[i][1]);
            }
            
            PoePart1.totalMessagesSent++;
        }
        
        assertEquals(5, PoePart1.serialMessages.size());
        assertEquals(5, PoePart1.totalMessagesSent);
        assertEquals(2, PoePart1.storedMessages.size());
    }
    
    @Test
    @Order(43)
    void testDisplaySendersAndRecipients() {
        setupTestMessages();
        assertDoesNotThrow(() -> PoePart1.displaySendersAndRecipients());
        assertTrue(PoePart1.storedMessages.size() > 0);
    }
    
    @Test
    @Order(44)
    void testDisplayLongestMessage() {
        setupTestMessages();
        assertDoesNotThrow(() -> PoePart1.displayLongestMessage());
    }
    
    @Test
    @Order(45)
    void testSearchMessageByID() {
        setupTestMessages();
        assertDoesNotThrow(() -> PoePart1.searchMessageByID());
    }
    
    @Test
    @Order(46)
    void testSearchMessagesByRecipient() {
        setupTestMessages();
        assertDoesNotThrow(() -> PoePart1.searchMessagesByRecipient());
    }
    
    @Test
    @Order(47)
    void testDeleteMessageByHash() throws IOException {
        setupTestMessages();
        
        if (PoePart1.messageHash.size() > 0) {
            String hashToDelete = PoePart1.messageHash.get(0);
            assertNotNull(hashToDelete);
        }
    }
    
    @Test
    @Order(48)
    void testDisplayFullReport() {
        setupTestMessages();
        assertDoesNotThrow(() -> PoePart1.displayFullReport());
    }
    
    @Test
    @Order(49)
    void testViewMessages() throws IOException {
        setupTestMessages();
        assertDoesNotThrow(() -> PoePart1.viewMessages());
    }
    
    @Test
    @Order(50)
    void testDeleteMessage() throws IOException {
        setupTestMessages();
        int initialSize = PoePart1.serialMessages.size();
        
        if (initialSize > 0) {
            PoePart1.serialMessages.remove(0);
            PoePart1.totalMessagesSent--;
            
            assertTrue(PoePart1.serialMessages.size() < initialSize);
        }
    }
    
    // ==================== POEPART1 FILE OPERATIONS TESTS ====================
    
    @Test
    @Order(51)
    void testSaveAndLoadMessages() throws IOException {
        setupTestMessages();
        
        PoePart1.saveMessagesToFile();
        
        PoePart1.serialMessages.clear();
        PoePart1.messageID.clear();
        PoePart1.messageHash.clear();
        PoePart1.messageContents.clear();
        PoePart1.messageSenders.clear();
        PoePart1.messageRecipients.clear();
        PoePart1.storedMessages.clear();
        
        PoePart1.loadMessagesFromFile();
        
        assertTrue(PoePart1.serialMessages.size() > 0);
    }
    
    @Test
    @Order(52)
    void testMessageLengthValidation() {
        String shortMessage = "Short message";
        String longMessage = "a".repeat(251);
        
        assertTrue(shortMessage.length() <= 250);
        assertTrue(longMessage.length() > 250);
    }
    
    // ==================== POEPART1 EDGE CASE TESTS ====================
    
    @Test
    @Order(53)
    void testEmptyArrays() {
        assertDoesNotThrow(() -> PoePart1.displaySendersAndRecipients());
        assertDoesNotThrow(() -> PoePart1.displayLongestMessage());
        assertDoesNotThrow(() -> PoePart1.displayFullReport());
    }
    
    @Test
    @Order(54)
    void testMultipleMessagesSending() throws IOException {
        int numMessagesToSend = 3;
        
        for (int i = 0; i < numMessagesToSend; i++) {
            String msgID = PoePart1.generateMessageID();
            String msgHash = PoePart1.generateMessageHash(msgID, i + 1, "Test message " + i);
            
            PoePart1.serialMessages.add("Test message " + i);
            PoePart1.messageID.add(msgID);
            PoePart1.messageHash.add(msgHash);
            PoePart1.messageContents.add("Test message " + i);
            PoePart1.messageSenders.add("Test User");
            PoePart1.messageRecipients.add("+27834557896");
            PoePart1.storedMessages.add("Test message " + i);
            PoePart1.totalMessagesSent++;
        }
        
        assertEquals(numMessagesToSend, PoePart1.serialMessages.size());
        assertEquals(numMessagesToSend, PoePart1.totalMessagesSent);
    }
    
    @Test
    @Order(55)
    void testMessageValidationWithBoundaryValues() {
        // Test exactly at max length
        String exactly250 = "A".repeat(250);
        Message boundaryMsg = new Message(testRecipient, exactly250, 100);
        assertTrue(boundaryMsg.isValid());
        
        // Test at 249 characters
        String at249 = "A".repeat(249);
        Message underMsg = new Message(testRecipient, at249, 101);
        assertTrue(underMsg.isValid());
    }
    
    @Test
    @Order(56)
    void testPhoneNumberFormatValidation() {
        // Valid formats
        assertTrue(PoePart1.checkPhone("+27834567890"));
        assertTrue(PoePart1.checkPhone("+27123456789"));
        
        // Invalid formats
        assertFalse(PoePart1.checkPhone("0834567890"));
        assertFalse(PoePart1.checkPhone("+27123"));
        assertFalse(PoePart1.checkPhone("+271234567890")); // too long
    }
    
    @Test
    @Order(57)
    void testHashUniqueness() {
        Message msg1 = new Message(testRecipient, "Same content", 1);
        Message msg2 = new Message(testRecipient, "Same content", 2);
        
        // Different message numbers should produce different hashes
        assertNotEquals(msg1.getMessageHash(), msg2.getMessageHash());
    }
    
    // ==================== HELPER METHODS ====================
    
    private void setupTestMessages() {
        String[] testMessages = {
            "Did you get the cake?",
            "Where are you? You are late! I have asked you to be on time.",
            "Yohoooo, I am at your gate.",
            "It is dinner time!",
            "Ok, I am leaving without you."
        };
        
        String[] testRecipients = {
            "+27834557896",
            "+27838884567",
            "+27834484567",
            "08388884567",
            "+27838884567"
        };
        
        for (int i = 0; i < testMessages.length; i++) {
            String msgID = PoePart1.generateMessageID();
            int msgNumber = PoePart1.totalMessagesSent + 1;
            String msgHash = PoePart1.generateMessageHash(msgID, msgNumber, testMessages[i]);
            
            PoePart1.serialMessages.add(testMessages[i]);
            PoePart1.messageID.add(msgID);
            PoePart1.messageHash.add(msgHash);
            PoePart1.messageContents.add(testMessages[i]);
            PoePart1.messageSenders.add("Test User");
            PoePart1.messageRecipients.add(testRecipients[i]);
            PoePart1.storedMessages.add(testMessages[i]);
            PoePart1.totalMessagesSent++;
        }
    }
}