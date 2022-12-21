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
    private List<Set<String>> commands = Command.VALID_COMMANDS;

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

                return new CommandData(command, IntStream.range(1,5).mapToObj(matcher::group).collect(Collectors.toUnmodifiableList()));
            case BALANCE:
                matcher = Pattern.compile("(?i)^([\\d.]+)?\\b ?"+command+" ?\\b(\\w+)?$")
                        .matcher(line);
                if(!matcher.matches()) throw new InvalidArgumentException();

                return new CommandData(command, List.of(matcher.group(1), matcher.group(2)));
            case GROUP:
                if(line.matches("(?i)^"+command+" create.*")){
                    matcher = Pattern.compile("(?i)^"+command+" (create)(?-i) ([A-Z\\d]+) \\(((\\w+(, )?\\b)+)\\)$")
                            .matcher(line);
                    if(matcher.matches()) {
                        List<String> args = new ArrayList<>(List.of(matcher.group(1).toUpperCase(), matcher.group(2)));
                        Collections.addAll(args, matcher.group(3).split(", "));
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

            default:
                return new CommandData(command, null);
        }
    }
}
