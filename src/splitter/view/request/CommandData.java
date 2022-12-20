package splitter.view.request;

import splitter.view.Command;

public class CommandData {
    private Command command;
    private String[] args;

    public CommandData(Command command, String[] args) {
        this.command = command;
        this.args = args;
    }

    public Command getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
}
