package splitter;

import splitter.domain.BalanceSummary;
import splitter.domain.BalanceTracker;
import splitter.view.CommandView;
import splitter.view.InvalidArgumentException;
import splitter.view.UnknownCommandException;
import splitter.view.CommandData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Controller {
    private CommandView view = new CommandView();
    private BalanceTracker tracker = new BalanceTracker();

    public void start(){
        while(true) {
            try{
                CommandData data = view.getCommand();
                System.out.println(data.getArgs());
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
