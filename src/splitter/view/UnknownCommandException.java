package splitter.view;

public class UnknownCommandException extends Exception{
    public UnknownCommandException() {
        super("Unknown command. Print help to show commands list");
    }
}
