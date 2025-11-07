package securechat.client;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Scanner;

public class SecureChatClient {
    private String host;
    private int port;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SecretKey aesKey;
    private String nickname;

    public SecureChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            socket = new Socket(host, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            // 1) read server public key
            String serverPubB64 = in.readUTF();
            PublicKey serverPub = RSAUtils.publicKeyFromBase64(serverPubB64);

            // 2) ask nickname
            System.out.print("Enter your nickname: ");
            nickname = scanner.nextLine().trim();
            if (nickname.isEmpty()) nickname = "Anon";

            // 3) generate AES key and send
            aesKey = AESUtils.generateKey(128);
            byte[] aesRaw = aesKey.getEncoded();
            byte[] encryptedAes = RSAUtils.encryptWithPublicKey(aesRaw, serverPub);
            String encAesB64 = RSAUtils.bytesToBase64(encryptedAes);

            // send nickname (plain) and encrypted AES
            out.writeUTF(nickname);
            out.writeUTF(encAesB64);
            out.flush();

            // 4) start listener thread
            ClientListener listener = new ClientListener(in, aesKey);
            new Thread(listener).start();

            System.out.println("Connected. Type messages, '/quit' to exit.");

            // 5) read user input, encrypt with AES and send
            while (true) {
                String line = scanner.nextLine();
                if (line == null) break;
                String encrypted = AESUtils.encrypt(line, aesKey);
                out.writeUTF(encrypted);
                out.flush();
                if (line.equalsIgnoreCase("/quit") || line.equalsIgnoreCase("/exit")) break;
            }

            // cleanup
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
