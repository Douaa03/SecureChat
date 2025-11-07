package securechat.client;

public class ClientMain {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try { port = Integer.parseInt(args[1]); } catch (Exception ignored) {}
        }
        SecureChatClient client = new SecureChatClient(host, port);
        client.start();
    }
}
