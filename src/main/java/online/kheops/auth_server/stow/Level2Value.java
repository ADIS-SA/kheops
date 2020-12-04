package online.kheops.auth_server.stow;

public class Level2Value {

    private boolean newInDestination;
    private Integer numberOfNewInstances;
    private boolean sendUpload;

    public Level2Value(Integer numberOfNewInstances, boolean isNewInDestination, boolean isSend) {
        this.newInDestination = isNewInDestination;
        this.numberOfNewInstances = numberOfNewInstances;
        this.sendUpload = isSend;
    }

    public boolean isNewInDestination() { return newInDestination; }

    public Integer getNumberOfNewInstances() { return numberOfNewInstances; }

    public boolean isSendUpload() { return sendUpload; }

    public void add(Integer numberOfNewInstances, boolean isSend) {
        this.numberOfNewInstances += numberOfNewInstances;
        this.sendUpload = isSend;
    }
}
