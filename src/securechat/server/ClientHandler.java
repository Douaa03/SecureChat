package securechat.server;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;

public class ClientHandler implements Runnable {
    private Socket socket;
    private securechat.server.SecureChatServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private SecretKey aesKey;
    private String nickname;
    private boolean alive = true;
    private PrivateKey serverPrivateKey;
    private String serverPublicKeyB64;

    public ClientHandler(Socket socket, securechat.server.SecureChatServer server, PrivateKey serverPrivateKey, String serverPublicKeyB64) throws IOException {
        this.socket = socket;
        this.server = server;
        this.serverPrivateKey = serverPrivateKey;
        this.serverPublicKeyB64 = serverPublicKeyB64;
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            // 1) send server public key
            out.writeUTF(serverPublicKeyB64);
            out.flush();

            // 2) read nickname (plain)
            this.nickname = in.readUTF();

            // 3) read encrypted AES key (Base64)
            String encryptedAesB64 = in.readUTF();
            byte[] encryptedBytes = RSAUtils.base64ToBytes(encryptedAesB64);
            byte[] aesRaw = RSAUtils.decryptWithPrivateKey(encryptedBytes, serverPrivateKey);
            this.aesKey = AESUtils.fromBytes(aesRaw);

            System.out.println("[" + nickname + "] connected. IP: " + socket.getRemoteSocketAddress());

            // Notify others (optional)
            broadcastSystemMessage(nickname + " has joined.");

            // 4) loop: receive encrypted messages (Base64 iv:ct)
            while (alive && !socket.isClosed()) {
                String incoming = in.readUTF(); // encrypted by client's AES
                if (incoming == null) break;
                // decrypt with this client's AES
                String plaintext = AESUtils.decrypt(incoming, aesKey);
                if (plaintext.equalsIgnoreCase("/quit") || plaintext.equalsIgnoreCase("/exit")) {
                    alive = false;
                    break;
                }
                Message msg = new Message(nickname, plaintext);
                System.out.println("Received: " + msg.toString());

                // Forward to all: for each client, encrypt msg.content with that client's AES and send
                for (ClientHandler target : server.clients) {
                    if (target == null || !target.isAlive()) continue;
                    try {
                        // Build plaintext to send: include sender + content + timestamp
                        String outboundPlain = msg.toString();
                        String encryptedForTarget = AESUtils.encrypt(outboundPlain, target.aesKey);
                        target.sendEncryptedPayload(encryptedForTarget);
                    } catch (Exception e) {
                        System.err.println("Failed to forward to " + target.getNickname());
                    }
                }
            }
        } catch (EOFException eof) {
            // client disconnected
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void broadcastSystemMessage(String text) {
        try {
            Message m = new Message("SERVER", text);
            for (ClientHandler target : server.clients) {
                if (target == null || !target.isAlive()) continue;
                String enc = AESUtils.encrypt(m.toString(), target.aesKey);
                target.sendEncryptedPayload(enc);
            }
        } catch (Exception ignored) {}
    }

    public void sendEncryptedPayload(String b64Payload) throws IOException {
        out.writeUTF(b64Payload);
        out.flush();
    }

    public String getNickname() { return nickname; }
    public boolean isAlive() { return alive; }

    private void cleanup() {
        try {
            alive = false;
            server.removeClient(this);
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connection closed for " + nickname);
            broadcastSystemMessage(nickname + " has left.");
        } catch (IOException ignored) {}
    }
}
