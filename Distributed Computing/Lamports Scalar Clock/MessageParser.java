/**
 * Created by PrashantH on 01-Mar-15.
 */

public class MessageParser {
    private String m_RawMessage;
    private String m_From;
    private String m_To;
    private String m_Message;
    private int m_SenderClockTime;

    public MessageParser(String rawMessage) {
        this.m_RawMessage = rawMessage;
    }

    public MessageParser parse() throws Exception {
        if(null == m_RawMessage) {
            throw new Exception("Message is null or empty.");
        }

        String[] parts = m_RawMessage.split("-");
        setFrom(parts[0]);
        setTo(parts[1]);
        setSenderClockTime(Integer.parseInt(parts[2]));
        setMessage(parts[3].trim());
        return this;
    }

    public static MessageParser parseMessage(String message) throws Exception {
        MessageParser msg = new MessageParser(message);
        return msg.parse();
    }

    public String getFrom() {
        return m_From;
    }

    public void setFrom(String from) {
        this.m_From = from;
    }

    public String getTo() {
        return m_To;
    }

    public void setTo(String to) {
        this.m_To = to;
    }

    public String getMessage() {
        return m_Message;
    }

    public void setMessage(String message) {
        this.m_Message = message;
    }

    public int getSenderClockTime() {
        return m_SenderClockTime;
    }

    public void setSenderClockTime(int senderClockTime) {
        this.m_SenderClockTime = senderClockTime;
    }
}

