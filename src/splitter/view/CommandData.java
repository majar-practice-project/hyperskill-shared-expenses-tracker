package splitter.view;

import splitter.view.Command;

import java.util.List;

public class CommandData {
    private final Command command;
    private final List<String> args;

    public CommandData(Command command, List<String> args) {
        this.command = command;
        this.args = args;
    }

    public Command getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }
}
