package securechat.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;
    private String content;
    private String timestamp;

    public Message() {}

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getSender() { return sender; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + " : " + content;
    }
}
