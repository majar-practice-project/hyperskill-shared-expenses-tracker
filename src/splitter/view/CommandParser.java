package splitter.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandParser {
    private final List<Set<String>> commands = Command.VALID_COMMANDS;

    public CommandData parse(String line) throws UnknownCommandException, InvalidArgumentException {
        String[] args = line.split(" ");
        for(int i=0; i< commands.size(); i++){
            if(args.length <= i) {
                throw new UnknownCommandException();
            }
            String command = args[i].toUpperCase();
            if(commands.get(i).contains(command)){
                return validatedArgs(Command.valueOf(command), line);
            }
        }
        throw new UnknownCommandException();
    }

    public CommandData validatedArgs(Command command, String line) throws InvalidArgumentException {
        Matcher matcher;
        switch(command){
            case BORROW:
            case REPAY:
                matcher = Pattern.compile("(?i)^([\\d.]+)?\\b ?"+command+" (\\w+) (\\w+) (\\d+\\.?\\d*)$")
                        .matcher(line);
                if(!matcher.matches()) throw new InvalidArgumentException();

                return new CommandData(command, IntStream.range(1,5).mapToObj(matcher::group).collect(Collectors.toList()));
            case BALANCE:
                matcher = Pattern.compile("(?i)^([\\d.]+)?\\b ?"+command+" ?\\b(\\w+)?$")
                        .matcher(line);
                if(!matcher.matches()) throw new InvalidArgumentException();

                return new CommandData(command, IntStream.range(1,3).mapToObj(matcher::group).collect(Collectors.toList()));
            case GROUP:
                if(line.matches("(?i)^"+command+" (create|add|remove).*")){
                    matcher = Pattern.compile("(?i)^"+command+" (create|add|remove)(?-i) ([A-Z\\d]+) \\((([+-]?\\w+, ?)*[+-]?\\w+)\\)$")
                            .matcher(line);
                    if(matcher.matches()) {
                        List<String> args = new ArrayList<>(List.of(matcher.group(1).toUpperCase(), matcher.group(2)));
                        Collections.addAll(args, matcher.group(3).split(", ?"));
                        return new CommandData(command, args);
                    }
                } else if(line.matches("(?i)^"+command+" show.*")) {
                    matcher = Pattern.compile("(?i)^"+command+" (show)(?-i) ([A-Z\\d]+)$")
                            .matcher(line);
                    if(matcher.matches()) {
                        return new CommandData(command, List.of(matcher.group(1).toUpperCase(), matcher.group(2)));
                    }
                }

                throw new InvalidArgumentException();
            case PURCHASE:
                matcher = Pattern.compile("(?i)^([\\d.]+)?\\b ?"+command+"(?-i) (\\w+) \\w+ (\\d+.?\\d*) \\(([A-Z\\d]+)\\)$")
                        .matcher(line);
                if(!matcher.matches()) throw new InvalidArgumentException();

                return new CommandData(command, IntStream.range(1,5).mapToObj(matcher::group).collect(Collectors.toList()));

            default:
                return new CommandData(command, null);
        }
    }
}
