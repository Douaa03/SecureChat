package securechat.crypto;

import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAUtils {

    public static KeyPair generateKeyPair(int bits) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(bits);
        return kpg.generateKeyPair();
    }

    public static String publicKeyToBase64(PublicKey pub) {
        return Base64.getEncoder().encodeToString(pub.getEncoded());
    }

    public static PublicKey publicKeyFromBase64(String b64) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(b64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static byte[] decryptWithPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] encryptWithPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] base64ToBytes(String b64) {
        return Base64.getDecoder().decode(b64);
    }

    public static String bytesToBase64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }
}
