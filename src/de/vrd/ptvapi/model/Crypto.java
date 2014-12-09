package de.vrd.ptvapi.model;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	private Mac mac;
	
	public Crypto(String key) {
        // Get an hmac_sha1 key from the raw key bytes
        byte[] keyBytes = key.getBytes();           
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

        // Get an hmac_sha1 Mac instance and initialize with the signing key
        try {
			mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	public String hmacSha1(String value, String key) {
            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());
            // Convert raw bytes to Hex
            String hexstring = bytesToHex(rawHmac);

            //  Covert array of Hex bytes to a String
            return hexstring;
    }
}
