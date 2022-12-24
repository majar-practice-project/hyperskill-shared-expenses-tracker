package splitter.view;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public enum Command {
    BALANCE("balance"),
    BORROW("borrow"),
    EXIT("exit"),
    HELP("help"),
    REPAY("repay"),
    GROUP("group"),
    PURCHASE("purchase"),
    SECRET_SANTA("secretSanta"),
    CASH_BACK("cashBack"),
    WRITE_OFF("writeOff");
    public static final List<Set<String>> VALID_COMMANDS = List.of(
            Arrays.stream(values()).map(cmd -> cmd.getDisplayName().toUpperCase()).collect(Collectors.toUnmodifiableSet()),
            Stream.of(BALANCE, BORROW, CASH_BACK, PURCHASE, REPAY, WRITE_OFF).map(cmd -> cmd.getDisplayName().toUpperCase())
                    .collect(Collectors.toUnmodifiableSet()));

    public static final Map<String, Command> displayNameMap = Arrays.stream(values())
            .collect(Collectors.toMap(cmd -> cmd.getDisplayName().toUpperCase(), cmd->cmd));
    private final String displayName;

    Command(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Command displayNameOf(String displayName){
        return displayNameMap.get(displayName);
    }
}