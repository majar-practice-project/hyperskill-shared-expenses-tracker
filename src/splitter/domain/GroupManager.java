package splitter.domain;

import splitter.view.GroupNotFoundException;
import splitter.view.InvalidArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupManager {
    private final Map<String, List<String>> groupMap = new HashMap<>();

    public void addGroup(String groupName, List<String> members) {
        if (groupMap.containsKey(groupName)) throw new RuntimeException("Error handling not implemented");
        members = members.stream().sorted().collect(Collectors.toUnmodifiableList());
        groupMap.put(groupName, members);
    }

    public List<String> getGroupMembers(String groupName) throws GroupNotFoundException {
        if (!groupMap.containsKey(groupName)) throw new GroupNotFoundException();
        return groupMap.get(groupName);
    }

    public List<Transaction> processPurchase(LocalDate date, String groupName, String buyerName, BigDecimal amount) throws InvalidArgumentException {
        List<String> members = groupMap.get(groupName);
        if(members == null) throw new InvalidArgumentException();

        BigDecimal payLessAmount = amount.divide(new BigDecimal(members.size()), RoundingMode.FLOOR);
        BigDecimal payMoreAmount = payLessAmount.add(new BigDecimal("0.01"));

        double priceDiff = amount.subtract(payLessAmount.multiply(new BigDecimal(members.size()))).doubleValue();
        int payMoreCount = (int) Math.round(priceDiff*100);

        List<Transaction> transactions = new ArrayList<>();
        for(int i=0; i<payMoreCount; i++) {
            if(!buyerName.equals(members.get(i))){
                transactions.add(new Transaction(date, members.get(i), buyerName, payMoreAmount));
            }
        }
        for(int i=payMoreCount; i<members.size(); i++) {
            if(!buyerName.equals(members.get(i))) {
                transactions.add(new Transaction(date, members.get(i), buyerName, payLessAmount));
            }
        }

        return transactions;
    }
}
