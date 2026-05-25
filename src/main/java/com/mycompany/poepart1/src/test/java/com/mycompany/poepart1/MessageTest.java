/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.poepart1;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;


/**
 *
 * @author Az'ulwazi
 */
public class MessageTest {
    
    public MessageTest() {
    }
    
    public Message message;
    public final String testRecipient = "+27123456789";
    public final String testContent = "Hello World";
    public final int testMessageNumber = 1;
    
    public ByteArrayOutputStream outContent;
    public PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        message = new Message(testRecipient, testContent, testMessageNumber);
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    // ==================== CONSTRUCTOR TESTS ====================
    
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
        
        // Check that messageID contains digits
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
        
        // Check that hash is uppercase
        assertEquals(hash, hash.toUpperCase());
    }
    
    @Test
    @Order(6)
    void testMessageHashWithSingleWord() {
        Message singleWordMsg = new Message(testRecipient, "Hello", 2);
        String hash = singleWordMsg.getMessageHash();
        
        // First and last word should be the same for single word
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
        
        // Check preview (first 30 chars or less)
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
        
        // Should be truncated with "..."
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
        
        // Quotes should be escaped
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
        
        // Hash should still be generated
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
        // Create a message and extract its first two digits from ID
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
}