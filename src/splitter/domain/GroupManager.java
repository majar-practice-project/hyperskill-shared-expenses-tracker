package splitter.domain;

import splitter.view.InvalidArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class GroupManager {
    /*
    Using lists to store group members is a tradeoff.
     - Displaying group members and splitting amount among groups require ordering of the members in which list is the most
    efficient way. (Set requires conversion to list then sort which requires O(nlogn)).
     - Adding and removing group members requires Set datastructure to be process efficiently O(nlogn) which for list
    could potentially take O(n*n). However, during these operations we can convert list into set temporarily to achieve
    a better performance, the same as the set O(nlogn), with some overhead on conversion.
     - It's anticipated that accessing the group members would be much more frequent than modifying the members, so list
     is chosen as storage for group members.
     */
    private final Map<String, List<String>> groupMap = new HashMap<>();

    public void createGroup(String groupName, List<String> members) {
        members = getFinalSpecifiedMembers(members).stream()
                .sorted()
                .collect(Collectors.toList());
        groupMap.put(groupName, members);
    }

    public void addGroupMembers(String groupName, List<String> members) throws GroupNotFoundException{
        if(!groupMap.containsKey(groupName)) throw new GroupNotFoundException();

        List<String> finalMembers = groupMap.get(groupName);
        finalMembers.addAll(getFinalSpecifiedMembers(members));
        finalMembers = finalMembers.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        groupMap.put(groupName, finalMembers);
    }

    public void removeGroupMembers(String groupName, List<String> members) throws GroupNotFoundException{
        if(!groupMap.containsKey(groupName)) throw new GroupNotFoundException();

        Set<String> finalMembers = new HashSet<>(groupMap.get(groupName));
        getFinalSpecifiedMembers(members).forEach(finalMembers::remove);

        groupMap.put(groupName, finalMembers.stream().sorted().collect(Collectors.toList()));
    }

    private List<String> getFinalSpecifiedMembers(List<String> members) {
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
                if(groupMembers.isEmpty()) {
                    members.remove(i);
                    continue;
                }
                members.set(i, groupMembers.get(0));
                groupMembers.stream().skip(1).forEach(members::add);
            }
        }
        return members;
    }

    public List<String> getGroupMembers(String groupName) throws GroupNotFoundException, EmptyGroupException {
        if (!groupMap.containsKey(groupName)) throw new GroupNotFoundException();
        List<String> members = Collections.unmodifiableList(groupMap.get(groupName));
        if(members.isEmpty()) throw new EmptyGroupException();

        return members;
    }

    public List<Transaction> processPurchase(LocalDate date, List<String> members, String buyerName, BigDecimal amount) throws InvalidArgumentException, EmptyGroupException {
        if(members == null) throw new InvalidArgumentException();

        members = getFinalSpecifiedMembers(members);
        if(members.isEmpty()) throw new EmptyGroupException();
        Collections.sort(members);

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