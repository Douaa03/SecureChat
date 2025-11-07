package securechat.client;

import javax.crypto.SecretKey;
import java.io.DataInputStream;

public class ClientListener implements Runnable {
    private DataInputStream in;
    private SecretKey aesKey;
    private boolean running = true;

    public ClientListener(DataInputStream in, SecretKey aesKey) {
        this.in = in;
        this.aesKey = aesKey;
    }

    @Override
    public void run() {
        try {
            while (running) {
                String enc = in.readUTF(); // encrypted with this client's AES
                if (enc == null) break;
                String plain = AESUtils.decrypt(enc, aesKey);
                System.out.println(plain);
            }
        } catch (Exception e) {
            // connection closed or error
            running = false;
        }
    }
}
