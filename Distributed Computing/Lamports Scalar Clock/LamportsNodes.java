/**
 * Created by PrashantH on 01-Mar-15.
 */

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class LamportsNodes implements Runnable {
    private String INET_ADDR_GROUP = "228.5.6.7";
    private int PORT_NUMBER = 8080;
    MulticastSocket m_multiSocket;
    InetAddress m_multiSocketGroup;
    String m_ProcessName;
    private int nodeId;
    volatile int m_LocalClock = 0;
    int m_EventCount = -1;

    public LamportsNodes(int id) {
        this.nodeId = id;
        joinMultiCastGroup();
    }

    public void joinMultiCastGroup() {
        try {
            m_ProcessName = "P" + nodeId;
            m_multiSocketGroup = InetAddress.getByName(INET_ADDR_GROUP);
            m_multiSocket = new MulticastSocket(PORT_NUMBER);
            m_multiSocket.joinGroup(m_multiSocketGroup);
            System.out.println("Initialized - " + m_ProcessName + " (clock time: " + m_LocalClock + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while(true) {
                byte[] buf = new byte[100];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                m_multiSocket.receive(recv);
                String rawMessage = new String(buf);
                MessageParser msg = MessageParser.parseMessage(rawMessage);

                if(msg.getMessage().equalsIgnoreCase("STOP")) {
                    System.out.println("\n------ END of " + m_ProcessName + " (clock time: "+ m_LocalClock + ") ------" );
                    break;
                }

                int incomingMessage = Integer.parseInt(msg.getMessage());
                if(m_EventCount < incomingMessage) {
                    m_EventCount = incomingMessage;
                    if(msg.getTo().equals(msg.getFrom()) && (nodeId == (Integer.parseInt(msg.getTo())))) {
                        m_LocalClock += 1; //IR1
                        System.out.println("Local event at <P" + msg.getTo() + ">, clock time: " + m_LocalClock);
                    }

                    else if(nodeId == (Integer.parseInt(msg.getTo()))) {
                        m_LocalClock = Math.max(m_LocalClock, msg.getSenderClockTime()); //IR2
                        m_LocalClock += 1; //IR1
                        System.out.println("From: P" + msg.getFrom() + "(" + msg.getSenderClockTime() + "), To: P" + msg.getTo() + "(" + m_LocalClock + "), Message: <" + msg.getMessage().trim() + ">.");
                    }
                }
            }
        } catch (Exception ignored) {

        } finally {
            if(null != m_multiSocket && !m_multiSocket.isClosed())
                m_multiSocket.close();
        }

    }

    public int getLocalClock() {
        return m_LocalClock;
    }

    public int incrementClock() {
        m_LocalClock+=1;
        return m_LocalClock;
    }
}
