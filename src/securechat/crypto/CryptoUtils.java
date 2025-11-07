package securechat.crypto;

import javax.crypto.SecretKey;
import java.security.KeyPair;

public class CryptoUtils {
    public static KeyPair generateRSA(int bits) throws Exception {
        return RSAUtils.generateKeyPair(bits);
    }

    public static SecretKey generateAES(int bits) throws Exception {
        return AESUtils.generateKey(bits);
    }
}
