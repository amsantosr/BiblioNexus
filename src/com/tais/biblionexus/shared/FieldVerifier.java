package com.tais.biblionexus.shared;

public class FieldVerifier {

    /**
     * Verifies that the specified name is valid for our service.
     * 
     * In this example, we only require that the name is at least four
     * characters. In your application, you can use more complex checks to
     * ensure that usernames, passwords, email addresses, URLs, and other fields
     * have the proper syntax.
     * 
     * @param name the name to validate
     * @return true if valid, false if invalid
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        return name.length() > 3;
    }
    
    /**
     * Verifica que un código ISBN sea válido.
     * 
     * Realiza la verificación de un código ISBN de 10 dígitos.
     * Sería genial si lo hiciera con la nueva regla de los 13 dígitos.
     * 
     * @param isbn
     * @return true if valid, false if invalid
     */
    public static boolean isValidISBN(String isbn) {
        int suma = 0;
        int digitIndex = 0;
        for (int index = 0; index < isbn.length(); ++index) {
            char chdigit = isbn.charAt(index);
            int digit;
            if ('0' <= chdigit && chdigit <= '9') {
                digit = chdigit - '0';
            } else if (chdigit == 'X' || chdigit == 'x') {
                digit = 10;
            } else {
                return false;
            }
            suma += digit * (10 - digitIndex);
            ++digitIndex;
        }
        boolean valid = suma % 11 == 0;
        return valid;
    }
}
