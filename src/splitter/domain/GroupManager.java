package splitter.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import splitter.data.GroupRepository;
import splitter.data.SplitterGroup;
import splitter.data.Transaction;
import splitter.view.InvalidArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class GroupManager {

    @Autowired
    private final GroupRepository groupRepository;

    public GroupManager(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void createGroup(String groupName, List<String> members) {
        groupRepository.save(new SplitterGroup(groupName, new TreeSet<>(getFinalSpecifiedMembers(members))));
    }

    public void addGroupMembers(String groupName, List<String> members) throws GroupNotFoundException {
        SplitterGroup group = groupRepository.findById(groupName).orElseThrow(GroupNotFoundException::new);
        group.getMembers().addAll(getFinalSpecifiedMembers(members));
        groupRepository.save(group);
    }

    public void removeGroupMembers(String groupName, List<String> members) throws GroupNotFoundException {
        SplitterGroup group = groupRepository.findById(groupName).orElseThrow(GroupNotFoundException::new);
        Set<String> finalMembers = group.getMembers();
        getFinalSpecifiedMembers(members).forEach(finalMembers::remove);
        groupRepository.save(group);
    }

    private Set<String> getFinalSpecifiedMembers(List<String> members) {
        List<String> additionGroup = new ArrayList<>();
        List<String> removalGroup = new ArrayList<>();
        for (String member : members) {
            if (member.charAt(0) == '-') {
                removalGroup.add(member.substring(1));
            } else if (member.charAt(0) == '+') {
                additionGroup.add(member.substring(1));
            } else {
                additionGroup.add(member);
            }
        }

        Set<String> finalMembers = expandAllGroups(additionGroup);
        finalMembers.removeAll(expandAllGroups(removalGroup));

        return finalMembers;
    }

    private Set<String> expandAllGroups(List<String> members) {
        for (int i = members.size() - 1; i >= 0; i--) {
            String groupName = members.get(i);
            if (groupRepository.existsById(groupName)) {
                Set<String> groupMembers = groupRepository.findById(groupName).get().getMembers();
                if (groupMembers.isEmpty()) {
                    members.remove(i);
                    continue;
                }
                members.set(i, groupMembers.stream().findFirst().orElseThrow());
                groupMembers.stream().skip(1).forEach(members::add);
            }
        }
        return new HashSet<>(members);
    }

    public Set<String> getGroupMembers(String groupName) throws GroupNotFoundException, EmptyGroupException {
        SplitterGroup group = groupRepository.findById(groupName).orElseThrow(GroupNotFoundException::new);
        Set<String> members = Collections.unmodifiableSet(group.getMembers());
        if (members.isEmpty()) throw new EmptyGroupException();
        return members;
    }

    public List<Transaction> processPurchase(LocalDate date, List<String> members, String buyerName, BigDecimal amount) throws InvalidArgumentException, EmptyGroupException {
        if (members == null) throw new InvalidArgumentException();

        members = new ArrayList<>(getFinalSpecifiedMembers(members));
        if (members.isEmpty()) throw new EmptyGroupException();
        Collections.sort(members);

        BigDecimal payLessAmount = amount.divide(new BigDecimal(members.size()), RoundingMode.FLOOR);
        BigDecimal payMoreAmount = payLessAmount.add(new BigDecimal("0.01"));

        double priceDiff = amount.subtract(payLessAmount.multiply(new BigDecimal(members.size()))).doubleValue();
        int payMoreCount = (int) Math.round(priceDiff * 100);

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < payMoreCount; i++) {
            if (!buyerName.equals(members.get(i))) {
                transactions.add(new Transaction(date, members.get(i), buyerName, payMoreAmount));
            }
        }
        for (int i = payMoreCount; i < members.size(); i++) {
            if (!buyerName.equals(members.get(i))) {
                transactions.add(new Transaction(date, members.get(i), buyerName, payLessAmount));
            }
        }

        return transactions;
    }

    public List<String[]> processSecretSanta(String groupName) throws GroupNotFoundException {
        Set<String> members = groupRepository.findById(groupName).orElseThrow(GroupNotFoundException::new).getMembers();
        List<String> givers = new ArrayList<>(members);
        Collections.shuffle(givers);

        int size = givers.size();
        List<String[]> pairs = new ArrayList<>(size);

        for(int i=0; i<size; i++){
            pairs.add(new String[]{givers.get(i), givers.get((i+1)%size)});
        }
        return pairs;
    }
}