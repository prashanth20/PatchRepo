import java.util.concurrent.locks.ReentrantLock;

public class Election {

    // ReEntrant Lock is used to ensure the fairness in mutual exclusion.
    public static ReentrantLock pingLock = new ReentrantLock();
    public static ReentrantLock electionLock = new ReentrantLock();
    //By default no election is going on
    private static boolean isElectionGoingOn = false;
    //By default processes are allowed to ping
    private static boolean isPingAllowed = true;
    // Process which identifies the election is necessary to maintain the Bully
    // algorithm.
    public static Process electionDetector;

    public static void initialElection(RunningThread[] threads) {
        Process process = new Process(-1, -1);
        for (int i = 0; i < threads.length; i++) {
            if (process.getPriority() < threads[i].getProcess().getPriority()) {
                process = threads[i].getProcess();
            }
        }
        // By default setting the process with higher pid as Leader/Co-Ordinator
        threads[process.processId - 1].getProcess().isProcessACoOrdinator = true;
    }

    public static Process getElectionDetector() {
        return electionDetector;
    }

    public static void setElectionDetector(Process electionDetector) {
        Election.electionDetector = electionDetector;
    }

    public static boolean isPingAllowed() {
        return isPingAllowed;
    }

    public static void setPingAllowedFlag(boolean pingFlag) {
        Election.isPingAllowed = pingFlag;
    }

    public static boolean isElectionGoingOn() {
        return isElectionGoingOn;
    }

    public static void setElectionGoingOnFlag(boolean electionFlag) {
        Election.isElectionGoingOn = electionFlag;
    }
}
