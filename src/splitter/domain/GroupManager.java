package splitter.domain;

import splitter.view.GroupNotFoundException;
import splitter.view.InvalidArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class GroupManager {
    private final Map<String, List<String>> groupMap = new HashMap<>();

    public void createGroup(String groupName, List<String> members) {
        members = getFinalMembers(members).stream()
                .sorted()
                .collect(Collectors.toList());
        groupMap.put(groupName, members);
    }

    private List<String> getFinalMembers(List<String> members) {
        List<String> additionGroup = new ArrayList<>();
        List<String> removalGroup = new ArrayList<>();
        for(String member: members){
            if(member.charAt(0) == '-'){
                removalGroup.add(member.substring(1));
            } else if(member.charAt(0) == '+') {
                additionGroup.add(member.substring(1));
            } else{
                additionGroup.add(member);
            }
        }

        return largeScaleRemoveAll(expandAllGroups(additionGroup), expandAllGroups(removalGroup));
    }

    private static <E> List<E> largeScaleRemoveAll(List<E> list1, List<E> list2) {
        Set<E> set1 = new HashSet<>(list1);
        list2.forEach(set1::remove);
        return new ArrayList<>(set1);
    }

    private List<String> expandAllGroups(List<String> members) {
        for(int i=members.size()-1; i>=0; i--){
            String group = members.get(i);
            if(groupMap.containsKey(group)) {
                List<String> groupMembers = groupMap.get(group);
                members.set(i, groupMembers.get(0));
                groupMembers.stream().skip(1).forEach(members::add);
            }
        }
        return members;
    }

    public List<String> getGroupMembers(String groupName) throws GroupNotFoundException {
        if (!groupMap.containsKey(groupName)) throw new GroupNotFoundException();
        return Collections.unmodifiableList(groupMap.get(groupName));
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