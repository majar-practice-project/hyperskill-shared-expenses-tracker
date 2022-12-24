package splitter.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import splitter.domain.BalanceSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
public class CommandView {
    private Scanner scanner = new Scanner(System.in);
    @Autowired
    private CommandParser parser;

    public CommandData getCommand() throws InvalidArgumentException, UnknownCommandException {
        return parser.parse(scanner.nextLine());
    }

    public void showError(Exception e) {
        System.out.println(e.getMessage());
    }

    public void showAllCommands(){
        Arrays.stream(Command.values())
                .map(Command::getDisplayName)
                .sorted()
                .forEach(System.out::println);
    }

    public void showSummaries(List<BalanceSummary> summaries){
        if(summaries.isEmpty()) {
            System.out.println("No repayments");
            return;
        }
        String format = "%s owes %s %s";
        summaries.stream()
                .map(summary -> String.format(format, summary.getPerson1(), summary.getPerson2(), formatAmount(summary.getAmount())))
                .sorted()
                .forEach(System.out::println);
    }

    public void showGroupMembers(Set<String> members){
        for(String member: members) {
            System.out.println(member);
        }
    }

    public void showSecretSanta(List<String[]> pairs){
        String giftFormat = "%s gift to %s";
        pairs.stream()
                .map(pair -> String.format(giftFormat, pair[0], pair[1]))
                .sorted()
                .forEach(System.out::println);
    }

    private static String formatAmount(BigDecimal n) {
        return n.setScale(2, RoundingMode.UNNECESSARY).toPlainString();
    }
}
