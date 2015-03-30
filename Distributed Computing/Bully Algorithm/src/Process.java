public class Process {

    int processId;
    int priority;
    boolean isProcessDown;
    boolean isProcessACoOrdinator;

    public Process(int processId, int priority) {
        this.processId = processId;
        this.isProcessDown = false;
        this.priority = priority;
        this.isProcessACoOrdinator = false;
    }

    public Process() {
    }

    public boolean isProcessACoOrdinator() {
        return isProcessACoOrdinator;
    }

    public void setProcessCoOrdinatorflag(boolean isCoOrdinator) {
        this.isProcessACoOrdinator = isCoOrdinator;
    }

    public boolean isProcessDown() {
        return isProcessDown;
    }

    public void setProcessDownflag(boolean downflag) {
        this.isProcessDown = downflag;
    }

    public int getPid() {
        return processId;
    }

    public void setPid(int pid) {
        this.processId = pid;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
