package splitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import splitter.data.Transaction;
import splitter.domain.*;
import splitter.view.CommandData;
import splitter.view.CommandView;
import splitter.view.InvalidArgumentException;
import splitter.view.UnknownCommandException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Controller {
    @Autowired
    private CommandView view;
    @Autowired
    private BalanceTracker tracker;
    @Autowired
    private GroupManager groupManager;

    public void start() {
        while (true) {
            try {
                CommandData data = view.getCommand();
                switch (data.getCommand()) {
                    case EXIT:
                        return;
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
                        break;
                    case WRITE_OFF:
                        processWriteOff(data.getArgs());
                        break;
                    case GROUP:
                        switch (data.getArgs().get(0)) {
                            case "CREATE":
                                groupManager.createGroup(data.getArgs().get(1),
                                        data.getArgs().stream().skip(2).collect(Collectors.toUnmodifiableList()));
                                break;
                            case "SHOW":
                                view.showGroupMembers(groupManager.getGroupMembers(data.getArgs().get(1)));
                                break;
                            case "ADD":
                                groupManager.addGroupMembers(data.getArgs().get(1),
                                        data.getArgs().stream().skip(2).collect(Collectors.toUnmodifiableList()));
                                break;
                            case "REMOVE":
                                groupManager.removeGroupMembers(data.getArgs().get(1),
                                        data.getArgs().stream().skip(2).collect(Collectors.toUnmodifiableList()));
                                break;
                            default:
                                System.out.println(data.getArgs());
                                throw new RuntimeException("Shouldn't be here");
                        }
                        break;
                    case CASH_BACK:
                        data.getArgs().set(2, "-" + data.getArgs().get(2));
                    case PURCHASE:
                        processGroupPurchase(data.getArgs());
                        break;
                    case SECRET_SANTA:
                        view.showSecretSanta(groupManager.processSecretSanta(data.getArgs().get(0)));
                }
            } catch (InvalidArgumentException | UnknownCommandException | GroupNotFoundException |
                     EmptyGroupException e) {
                view.showError(e);
            }
        }
    }

    private void processWriteOff(List<String> args) {
        tracker.dropTransactionByDate(parseDate(args.get(0)));
    }

    private void processGroupPurchase(List<String> data) throws InvalidArgumentException, EmptyGroupException, GroupNotFoundException {
        try {
            LocalDate date = parseDate(data.get(0));
            BigDecimal amount = new BigDecimal(data.get(2));
            List<Transaction> transactions = groupManager.processPurchase(date, data.stream().skip(3).collect(Collectors.toList()), data.get(1), amount);

            transactions.forEach(tracker::storeTransaction);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new InvalidArgumentException();
        }
    }

    private void processTransaction(List<String> data) throws InvalidArgumentException {
        try {
            LocalDate date = parseDate(data.get(0));
            BigDecimal amount = new BigDecimal(data.get(3));
            tracker.storeTransaction(date, data.get(1), data.get(2), amount);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new InvalidArgumentException();
        }
    }

    private void processBalance(List<String> data) throws InvalidArgumentException, EmptyGroupException, GroupNotFoundException {
        try {
            List<BalanceSummary> summaries;
            String status = data.get(1) != null ? data.get(1).toUpperCase() : "CLOSE";
            List<String> restrictedMembers = data.stream().skip(2).collect(Collectors.toList());
            if ("CLOSE".equals(status)) {
                summaries = tracker.getBalanceSummary(parseDate(data.get(0)), restrictedMembers);
            } else if ("OPEN".equals(status)) {
                LocalDate date = parseDate(data.get(0));
                summaries = tracker.getBalanceSummary(date.withDayOfMonth(1).minusDays(1), restrictedMembers);
            } else {
                throw new InvalidArgumentException();
            }
            view.showSummaries(summaries);
        } catch (DateTimeParseException e) {
            throw new InvalidArgumentException();
        }
    }

    private LocalDate parseDate(String date) {
        if (date == null) return LocalDate.now();
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}