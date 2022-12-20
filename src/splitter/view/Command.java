package splitter.view;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Command {
    HELP,
    EXIT,
    BORROW,
    REPAY,
    BALANCE;

    public static final Set<Command> COMMANDS = Set.of(values());
    public static final List<Set<String>> VALID_COMMANDS = List.of(
            Arrays.stream(values()).map(Enum::name).collect(Collectors.toUnmodifiableSet()),
            Stream.of(BORROW, REPAY, BALANCE).map(Enum::name).collect(Collectors.toUnmodifiableSet()));
}