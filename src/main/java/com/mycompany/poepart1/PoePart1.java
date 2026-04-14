/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.poepart1;

import java.util.Scanner;

/**
 *
 * @author lwazi
 */
public class PoePart1 {

    public static void main(String[] args) {
     


    
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
        } else {
            System.out.println("Login failed, please try again.");
        }
    }

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
    

