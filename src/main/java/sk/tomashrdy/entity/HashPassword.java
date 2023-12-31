package sk.tomashrdy.entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface HashPassword {

    //Metoda na zašifrovanie hesla ( zašifrovanie je nevratné neskôr budem pri porovnávani šifrovať rovnako aj zadané heslo a porovnávať heslá po šifrovaní )
    static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    //V tejto metode len zariadim aby sa mi vracal po zašifrovaní String ( obidve metódy generovala UI )
    static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
