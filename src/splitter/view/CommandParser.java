package splitter.view;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

                return new CommandData(command, IntStream.range(1,5).mapToObj(matcher::group).toArray(String[]::new));
            case BALANCE:
                matcher = Pattern.compile("(?i)^([\\d.]+)?\\b ?"+command+" ?\\b(\\w+)?$")
                        .matcher(line);
                if(!matcher.matches()) throw new InvalidArgumentException();

                return new CommandData(command, new String[]{matcher.group(1), matcher.group(2)});
            default:
                return new CommandData(command, null);
        }
    }
}
