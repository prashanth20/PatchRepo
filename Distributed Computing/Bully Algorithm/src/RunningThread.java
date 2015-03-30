import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class RunningThread implements Runnable {

    private Process process;
    private int totalProcesses;
    private static boolean messageFlag[];
    ServerSocket[] serverSockets;
    Random random;

    public RunningThread(Process process, int totalProcesses) {
        this.process = process;
        this.totalProcesses = totalProcesses;
        this.random = new Random();
        this.serverSockets = new ServerSocket[totalProcesses];
        RunningThread.messageFlag = new boolean[totalProcesses];
        for (int i = 0; i < totalProcesses; i++) {
            // Don't start messaging initially.
            RunningThread.messageFlag[i] = false;
        }
    }

    // This method is called when process which was crashed/hung recovers.
    synchronized private void recovery() {
        //if election is going on then wait
        while (Election.isElectionGoingOn()) ;

        System.out.println("Process[" + this.process.getPid() + "]: -> Recovered from Crash");

        //Find current co-ordinator and bully it to gain the CoOrdinator Status.
        try {
            // Lock the Election Process and find the suitable CoOrdinator.
            Election.pingLock.lock();
            Election.setPingAllowedFlag(false);

            Socket outgoing = new Socket(InetAddress.getLocalHost(), 12345);
            Scanner scan = new Scanner(outgoing.getInputStream());
            PrintWriter out = new PrintWriter(outgoing.getOutputStream(), true);
            System.out.println("Process[" + this.process.getPid() + "]:-> Who is the co-ordinator?");
            out.println("Who is the co-ordinator?");
            out.flush();

            // This reads the current coordinator process from Console.
            String pid = scan.nextLine();
            String priority = scan.nextLine();

            //Bully Condition
            if (this.process.getPriority() > Integer.parseInt(priority)) {
                out.println("Resign");
                out.flush();
                System.out.println("Process[" + this.process.getPid() + "]: Resign -> Process[" + pid + "]");
                String resignStatus = scan.nextLine();
                if (resignStatus.equals("Successfully Resigned")) {
                    this.process.setProcessCoOrdinatorflag(true);
                    serverSockets[this.process.getPid() - 1] = new ServerSocket(10000 + this.process.getPid());
                    System.out.println("Process[" + this.process.getPid() + "]: -> " +
                            "Bullyed current co-ordinator Process[" + pid + "]");
                }
            } else {
                // If recovered process priority is less than current co-ordinator
                // then ignore.
                out.println("Don't Resign");
                out.flush();
            }
            Election.pingLock.unlock();
            return;

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    synchronized private void pingCoOrdinator() {
        try {
            Election.pingLock.lock();
            if (Election.isPingAllowed()) {
                System.out.println("Process[" + this.process.getPid() + "]: Are you alive?");
                Socket outgoing = new Socket(InetAddress.getLocalHost(), 12345);
                outgoing.close();
            }
        } catch (Exception ex) {
            // Ping Failed lets Re-Elect the Leader.
            Election.setPingAllowedFlag(false);
            Election.setElectionGoingOnFlag(true);
            Election.setElectionDetector(this.process);
            //Initiate Election
            System.out.println("process[" + this.process.getPid() + "]: -> Co-Ordinator is down\n" + "process[" +
                    this.process.getPid() + "]: ->Initiating Election");
        } finally {
            Election.pingLock.unlock();
        }
    }

    private void executeJob() {
        int temp = random.nextInt(20);
        for (int i = 0; i <= temp; i++) {
            try {
                Thread.sleep((temp + 1) * 100);
            } catch (InterruptedException e) {
                System.out.println("Error Executing Thread:" + process.getPid());
                System.out.println(e.getMessage());
            }
        }
    }

    synchronized private boolean sendMessage() {
        boolean response = false;
        try {
            Election.electionLock.lock();
            // Condition to send back the message/response:
            // Election must be Happening.
            // Messaging between thread should be allowed.
            // And Replying Process should have higher priority than pinged Process.
            if (Election.isElectionGoingOn() && !RunningThread.isMessageFlag(this.process.getPid() - 1) &&
                    this.process.priority >= Election.getElectionDetector().getPriority()) {

                for (int i = this.process.getPid() + 1; i <= this.totalProcesses; i++) {
                    try {
                        Socket electionMessage = new Socket(InetAddress.getLocalHost(), 10000 + i);
                        System.out.println("Process[" + this.process.getPid() + "] -> Process[" + i + "]  responded " +
                                "to election message successfully");
                        electionMessage.close();
                        response = true;
                    } catch (IOException ex) {
                        System.out.println("Process[" + this.process.getPid() + "] -> Process[" + i + "] did not " +
                                "respond to election message");
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                //My message sending is done
                this.setMessageFlag(true, this.process.getPid() - 1);
                Election.electionLock.unlock();
                return response;
            } else {
                throw new Exception();
            }
        } catch (Exception ex1) {
            Election.electionLock.unlock();
            return true;
        }
    }

    public static boolean isMessageFlag(int index) {
        return RunningThread.messageFlag[index];
    }

    public static void setMessageFlag(boolean messageFlag, int index) {
        RunningThread.messageFlag[index] = messageFlag;
    }

    synchronized private void serve() {
        try {
            boolean done = false;
            Socket incoming = null;
            ServerSocket s = new ServerSocket(12345);
            Election.setPingAllowedFlag(true);
            // min 5 requests and max 10 requests
            int temp = this.random.nextInt(5) + 5;
            for (int counter = 0; counter < temp; counter++) {
                incoming = s.accept();
                if (Election.isPingAllowed()) {
                    System.out.println("Process[" + this.process.getPid() + "]:Yes");
                }

                Scanner scan = new Scanner(incoming.getInputStream());
                PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
                while (scan.hasNextLine() && !done) {
                    String line = scan.nextLine();
                    if (line.equals("Who is the co-ordinator?")) {
                        System.out.println("Process[" + this.process.getPid() + "]:-> " + this.process.getPid());
                        out.println(this.process.getPid());
                        out.flush();
                        out.println(this.process.getPriority());
                        out.flush();
                    } else if (line.equals("Resign")) {
                        this.process.setProcessCoOrdinatorflag(false);
                        out.println("Successfully Resigned");
                        out.flush();
                        incoming.close();
                        s.close();
                        System.out.println("Process[" + this.process.getPid() + "]:-> Successfully Resigned");
                        return;
                    } else if (line.equals("Don't Resign")) {
                        done = true;
                    }
                }
            }
            //after serving 5-10 requests go down for random time
            this.process.setProcessCoOrdinatorflag(false);
            this.process.setProcessDownflag(true);
            try {
                incoming.close();
                s.close();
                serverSockets[this.process.getPid() - 1].close();
                // Recover after some time
                Thread.sleep(15000);
                recovery();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            serverSockets[this.process.getPid() - 1] = new ServerSocket(10000 + this.process.getPid());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        while (true) {
            if (process.isProcessACoOrdinator()) {
                //serve other processes
                serve();
            } else {
                while (true) {
                    //Execute some task
                    executeJob();
                    //Ping the co-ordinator
                    pingCoOrdinator();
                    //Do Election
                    if (Election.isElectionGoingOn()) {
                        if (!sendMessage()) {//elect self as co-ordinator
                            Election.setElectionGoingOnFlag(false);//Election is Done
                            System.out.println("New Co-Ordinator: Process[" + this.process.getPid() + "]");
                            this.process.setProcessCoOrdinatorflag(true);
                            for (int i = 0; i < totalProcesses; i++) {
                                RunningThread.setMessageFlag(false, i);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
