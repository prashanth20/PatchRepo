/**
 * Created by Prashant Hiremath on 01-Mar-15.
 */

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class LamportsScalarClock {
    private static final int DEFAULT_PROCESS_COUNT = 3;
    private static final int DEFAULT_EVENT_COUNT = 10;
    private String INET_ADDR_GROUP = "228.5.6.7";
    private int PORT_NUMBER = 8080;
    private int m_ProcessCount;
    private int m_EventCount;
    MulticastSocket m_MultiCastSocket;
    InetAddress m_MultiCastGroup;
    LamportsNodes[] m_MsgRecievers;

    public static void main(String[] args) {
        LamportsScalarClock messageSender = new LamportsScalarClock();

        messageSender.setProcessNEventCount(args);
        System.out.println(messageSender.m_ProcessCount);
        System.out.println(messageSender.m_EventCount);
        messageSender.setMulticastGroup();
        messageSender.initializeLamportNodes(messageSender.m_ProcessCount);
        messageSender.sendMessages(messageSender.m_ProcessCount, messageSender.m_EventCount);
    }

    private void setProcessNEventCount(String[] args) {
        m_ProcessCount = DEFAULT_PROCESS_COUNT;
        m_EventCount = DEFAULT_EVENT_COUNT;
        if(null != args && args.length != 0) {
            try {
                int inputProcessCount = Integer.parseInt(args[0]);
                if(inputProcessCount > 2 || inputProcessCount < 10) {
                    m_ProcessCount = inputProcessCount;
                }
            } catch (Exception ignored) {
            }
            System.out.println("Process count: " + m_ProcessCount);
        }

        if(null != args && args.length == 2) {
            try {
                int inputEventCount = Integer.parseInt(args[1]);
                if(inputEventCount > 2 || inputEventCount < 100) {
                    m_EventCount = inputEventCount;
                }
            } catch (Exception ignored) {
            }
            System.out.println("Event count: " + m_EventCount);
        }
    }

    private void setMulticastGroup() {
        try {
            m_MultiCastGroup = InetAddress.getByName(INET_ADDR_GROUP);
            m_MultiCastSocket = new MulticastSocket(PORT_NUMBER);
            m_MultiCastSocket.setTimeToLive(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeLamportNodes(int numProcesses) {
        m_MsgRecievers = new LamportsNodes[numProcesses];
        Thread[] receiverThreads = new Thread[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            m_MsgRecievers[i] = new LamportsNodes(i+1);
            receiverThreads[i] = new Thread(m_MsgRecievers[i]);
            receiverThreads[i].start();
        }
    }

    private void sendMessages(int numProcesses, int numEvents) {
        for (int i = 1; i <= numEvents; i++) {
            try {
                int msgSender = getRandom(numProcesses);
                int msgReciever = getRandom(numProcesses);

                if(msgSender != msgReciever) {
                    m_MsgRecievers[msgSender-1].incrementClock();
                }
                int msgSenderClock = m_MsgRecievers[msgSender-1].getLocalClock();

                System.out.println("\nEvent #"+i);
                String msg = msgSender + "-" + msgReciever + "-" + msgSenderClock;
                System.out.println("Sending message [Sender-Receiver-ClockValueAtSender]: " + msg);
                msg += "-" + i;
                DatagramPacket packet = new DatagramPacket(msg.getBytes(),
                        msg.length(), m_MultiCastGroup, PORT_NUMBER);
                m_MultiCastSocket.send(packet);
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sendTerminationMessageToNodes();

        m_MultiCastSocket.close();
    }

    private int getRandom(int numProcesses) {
        Random rand = new Random();
        return rand.nextInt(numProcesses) + 1;
    }

    private void sendTerminationMessageToNodes() {
        try {
            String msg = "0-0-0-STOP";
            DatagramPacket packet = new DatagramPacket(msg.getBytes(),
                    msg.length(), m_MultiCastGroup, PORT_NUMBER);
            m_MultiCastSocket.send(packet);
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
