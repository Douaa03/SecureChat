package securechat.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;

public class AESUtils {

    public static SecretKey generateKey(int bits) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(bits);
        return kg.generateKey();
    }

    // Encrypt returns Base64 encoded string in format base64(iv) + ":" + base64(ciphertext)
    public static String encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
        String ivB64 = Base64.getEncoder().encodeToString(iv);
        String ctB64 = Base64.getEncoder().encodeToString(cipherBytes);
        return ivB64 + ":" + ctB64;
    }

    // Decrypt expects base64(iv) + ":" + base64(ciphertext)
    public static String decrypt(String b64ivAndCiphertext, SecretKey key) throws Exception {
        String[] parts = b64ivAndCiphertext.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Ciphertext format invalid");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] ct = Base64.getDecoder().decode(parts[1]);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] plain = cipher.doFinal(ct);
        return new String(plain, "UTF-8");
    }

    public static SecretKey fromBytes(byte[] raw) {
        return new SecretKeySpec(raw, 0, raw.length, "AES");
    }

    public static String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static SecretKey decodeKey(String b64) {
        byte[] raw = Base64.getDecoder().decode(b64);
        return fromBytes(raw);
    }
}

