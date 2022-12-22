package splitter.domain;

public class EmptyGroupException extends Exception {
    public EmptyGroupException() {
        super("Group is empty");
    }
}
