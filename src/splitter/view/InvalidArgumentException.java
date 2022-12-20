package splitter.view;

public class InvalidArgumentException extends Exception {
    public InvalidArgumentException() {
        super("Illegal command arguments");
    }
}
