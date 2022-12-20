package splitter.view;

import splitter.domain.BalanceSummary;

import java.util.Arrays;
import java.util.List;
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

    public void showSummaries(List<BalanceSummary> summaries){
        if(summaries.isEmpty()) {
            System.out.println("No repayments");
            return;
        }
        String format = "%s owes %s %d";
        summaries.stream()
                .map(summary -> String.format(format, summary.getPerson1(), summary.getPerson2(), summary.getAmount()))
                .sorted()
                .forEach(System.out::println);
    }
}
