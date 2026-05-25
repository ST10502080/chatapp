/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.poepart1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author lwazi
 */
public class PoePart1IT {
    
    public PoePart1IT() {
        }

    // Test username validation
    @Test
    public void testValidUsername() {
        assertTrue(PoePart1.checkUsername("ab_cd"));
    }

    @Test
    public void testInvalidUsername_NoUnderscore() {
        assertFalse(PoePart1.checkUsername("abcd"));
    }

    @Test
    public void testInvalidUsername_TooLong() {
        assertFalse(PoePart1.checkUsername("abc_de"));
    }

    // Test password validation
    @Test
    public void testValidPassword() {
        assertTrue(PoePart1.checkPassword("Pass@123"));
    }

    @Test
    public void testInvalidPassword_NoSpecialChar() {
        assertFalse(PoePart1.checkPassword("Password1"));
    }

    @Test
    public void testInvalidPassword_TooShort() {
        assertFalse(PoePart1.checkPassword("P@1"));
    }

    // Test phone validation
    @Test
    public void testValidPhone() {
        assertTrue(PoePart1.checkPhone("+27831234567"));
    }

    @Test
    public void testInvalidPhone() {
        assertFalse(PoePart1.checkPhone("0831234567"));
    }

    // Test phone hiding
    @Test
    public void testHidePhone() {
        String result = PoePart1.hidePhone("+27831234567");
        assertEquals("+27*********", result);
    }
}
