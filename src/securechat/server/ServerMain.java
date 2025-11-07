package securechat.server;

public class ServerMain {
    public static void main(String[] args) {
        int port = 12345;
        if (args.length >= 1) {
            try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }
        SecureChatServer server = new SecureChatServer(port);
        server.start();
    }
}
