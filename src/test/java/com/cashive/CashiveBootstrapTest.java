package com.cashive;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mkalyan on 8/26/16.
 */
public class CashiveBootstrapTest {

    private void test() {

    }

    private byte[] combineWithSalt(String password, byte[] salt) {

        byte[] passwordBytes = password.getBytes();
        byte[] allBytes = Arrays.copyOf(salt, salt.length + passwordBytes.length);

        for(int i=salt.length, j=0; i<allBytes.length; i++, j++) {
            allBytes[i] = passwordBytes[j];
        }

        System.out.println("all bytes length: "+allBytes.length+", passwordBytes length: "+passwordBytes.length);
        return allBytes;
    }

    private byte[] getHashed(byte[] bytes) {
        byte[] res = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(bytes);
            res = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String printBytes(byte[] bytes) {
        try {
            String str = new String(bytes, "US-ASCII");
            System.out.println("Salt is: "+str+", of length: "+str.length());
            return str;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String byteToHex(byte[] byteData) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    //http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    public static void main(String[] args) {
        Map<String, Object> cache = new HashMap<>();

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);

        String password = "123456";

        CashiveBootstrapTest test = new CashiveBootstrapTest();
        String stringedSalt = test.toHexString(salt);
        System.out.println("Salt is: "+stringedSalt);

        System.out.println(Arrays.equals(salt, test.toByteArray(stringedSalt)));

        String saltedPassword = password + stringedSalt;
        byte[] hashedPassword = test.getHashed(saltedPassword.getBytes());
        String stringedHashedPassword = test.toHexString(hashedPassword);
        System.out.println("Hashed Pwd is: "+stringedHashedPassword);

    }
}
