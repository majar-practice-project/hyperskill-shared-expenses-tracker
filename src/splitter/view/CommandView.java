package splitter.view;

import splitter.view.request.CommandData;

import java.util.Arrays;
import java.util.Scanner;

public class CommandView {
    private Scanner scanner = new Scanner(System.in);
    private CommandParser parser = new CommandParser();

    public CommandData getCommand() throws InvalidArgumentException, UnknownCommandException {
        return parser.parse(scanner.nextLine());
    }

    public void showError(Exception e) {
        System.out.println(e.getMessage());
    }

    public void showAllCommands(){
        Arrays.stream(Command.values()).forEach(cmd -> System.out.println(cmd.toString().toLowerCase()));
    }
}
