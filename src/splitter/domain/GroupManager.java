package splitter.domain;

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

    public List<String> getGroupMembers(String groupName) {
        if (!groupMap.containsKey(groupName)) throw new RuntimeException("Error handling not implemented");
        return groupMap.get(groupName);
    }
}
