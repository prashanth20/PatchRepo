public class Bully {

    public static void main(String[] args) {
        // This is default number of processes, if user doesn't send any
        // argument.
        int defaultTotalProcesses = 5;
        int totalProcesses = args.length == 1 ? Integer.parseInt(args[0]) : defaultTotalProcesses;

        if (args.length != 1)
            System.out.println("Using default number of processes i.e " + defaultTotalProcesses);

        // Create Threads for each number of processes.
        RunningThread[] threads = new RunningThread[totalProcesses];

        for (int i = 0; i < totalProcesses; i++) {
            // Passing process id, priority, total no. of processes to running
            // thread
            threads[i] = new RunningThread(new Process(i + 1, i + 1), totalProcesses);
        }
        try {
            Election.initialElection(threads);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        for (int i = 0; i < totalProcesses; i++) {
            //start every thread
            new Thread(threads[i]).start();
        }
    }
}

