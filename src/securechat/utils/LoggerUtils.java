package securechat.utils;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class LoggerUtils {
    private static final String LOGFILE = "securechat_history.log";

    public static synchronized void log(String line) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOGFILE, true))) {
            pw.println(LocalDateTime.now().toString() + " - " + line);
        } catch (Exception ignored) {}
    }
}
