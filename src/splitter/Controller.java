package splitter;

import splitter.domain.BalanceSummary;
import splitter.domain.BalanceTracker;
import splitter.view.CommandView;
import splitter.view.InvalidArgumentException;
import splitter.view.UnknownCommandException;
import splitter.view.CommandData;

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
                        String temp = data.getArgs()[1];
                        data.getArgs()[1] = data.getArgs()[2];
                        data.getArgs()[2] = temp;
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

    private void processTransaction(String[] data) throws InvalidArgumentException {
        try {
            LocalDate date = parseDate(data[0]);
            int amount = Integer.parseInt(data[3]);
            tracker.storeTransaction(date, data[1], data[2], amount);
        } catch(DateTimeParseException e){
            throw new InvalidArgumentException();
        }
    }

    private void processBalance(String[] data) throws InvalidArgumentException {
        try {
            List<BalanceSummary> summaries;
            String close = "CLOSE";
            String status = data[1] != null ? data[1].toUpperCase() : "CLOSE";
            if (close.equals(status)) {
                summaries = tracker.getBalanceSummary(parseDate(data[0]));
            } else if ("OPEN".equals(status)) {
                LocalDate date = parseDate(data[0]);
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
