package splitter.domain;

public class GroupNotFoundException extends Exception{
    public GroupNotFoundException(){
        super("Unknown group");
    }

    public GroupNotFoundException(String message) {
        super(message);
    }
}
