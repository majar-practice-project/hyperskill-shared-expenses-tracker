package splitter;

import splitter.domain.BalanceSummary;
import splitter.domain.BalanceTracker;
import splitter.domain.GroupManager;
import splitter.view.CommandView;
import splitter.view.InvalidArgumentException;
import splitter.view.UnknownCommandException;
import splitter.view.CommandData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private CommandView view = new CommandView();
    private BalanceTracker tracker = new BalanceTracker();

    private GroupManager groupManager = new GroupManager();

    public void start(){
        while(true) {
            try{
                CommandData data = view.getCommand();
                switch (data.getCommand()){
                    case EXIT:
                        System.exit(0);
                    case HELP:
                        view.showAllCommands();
                        break;
                    case BORROW:
                        processTransaction(data.getArgs());
                        break;
                    case REPAY:
                        String temp = data.getArgs().get(1);
                        data.getArgs().set(1, data.getArgs().get(2));
                        data.getArgs().set(2, temp);
                        processTransaction(data.getArgs());
                        break;
                    case BALANCE:
                        processBalance(data.getArgs());
                    case GROUP:
                        switch(data.getArgs().get(0)){
                            case "CREATE":
                                groupManager.addGroup(data.getArgs().get(1),
                                        data.getArgs().stream().skip(2).collect(Collectors.toUnmodifiableList()));
                                break;
                            case "SHOW":
                                view.showGroupMembers(groupManager.getGroupMembers(data.getArgs().get(1)));
                                break;
                            default:
                                throw new RuntimeException("Shouldn't be here");
                        }
                }
            } catch (InvalidArgumentException | UnknownCommandException e) {
                view.showError(e);
            }
        }
    }

    private void processTransaction(List<String> data) throws InvalidArgumentException {
        try {
            LocalDate date = parseDate(data.get(0));
            BigDecimal amount = new BigDecimal(data.get(3));
            tracker.storeTransaction(date, data.get(1), data.get(2), amount);
        } catch(DateTimeParseException | NumberFormatException e){
            throw new InvalidArgumentException();
        }
    }

    private void processBalance(List<String> data) throws InvalidArgumentException {
        try {
            List<BalanceSummary> summaries;
            String close = "CLOSE";
            String status = data.get(1) != null ? data.get(1).toUpperCase() : "CLOSE";
            if (close.equals(status)) {
                summaries = tracker.getBalanceSummary(parseDate(data.get(0)));
            } else if ("OPEN".equals(status)) {
                LocalDate date = parseDate(data.get(0));
                summaries = tracker.getBalanceSummary(date.withDayOfMonth(1).minusDays(1));
            } else {
                throw new InvalidArgumentException();
            }
            view.showSummaries(summaries);
        } catch (DateTimeParseException e){
            throw new InvalidArgumentException();
        }
    }

    private LocalDate parseDate(String date){
        if(date == null) return LocalDate.now();
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
