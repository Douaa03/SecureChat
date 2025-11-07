package securechat.server;


import securechat.crypto.RSAUtils;
import java.net.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SecureChatServer {
    private int port;
    private ServerSocket serverSocket;
    private KeyPair rsaKeyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    // thread-safe list
    private List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public SecureChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            // Generate RSA key pair for server
            rsaKeyPair = RSAUtils.generateKeyPair(2048);
            publicKey = rsaKeyPair.getPublic();
            privateKey = rsaKeyPair.getPrivate();

            serverSocket = new ServerSocket(port);
            System.out.println("SecureChatServer started on port " + port);
            System.out.println("Public key (Base64):\n" + RSAUtils.publicKeyToBase64(publicKey));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Incoming connection from " + clientSocket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(clientSocket, this, privateKey, RSAUtils.publicKeyToBase64(publicKey));
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String ciphertextForReceiver, ClientHandler from) {
        // ciphertextForReceiver represents content encrypted FOR the receiver already.
        for (ClientHandler ch : clients) {
            if (ch != null && ch.isAlive()) {
                // Send to all except maybe the sender (but keep it simple: include sender)
                try {
                    ch.sendEncryptedPayload(ciphertextForReceiver);
                } catch (Exception e) {
                    System.err.println("Failed to send to " + ch.getNickname());
                }
            }
        }
    }

    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
        System.out.println("Client removed: " + handler.getNickname());
    }
}



