package splitter.view;

import splitter.view.request.CommandData;

import java.util.List;
import java.util.Set;
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
                args[i] = null;
                return validatedArgs(Command.valueOf(command), line);
            }
        }
        throw new UnknownCommandException();
    }

    public CommandData validatedArgs(Command command, String line) throws InvalidArgumentException {
        String pattern;
        switch(command){
            case BORROW:
            case REPAY:
                pattern = "^(\\d{4}\\.\\d{2}\\.\\d{2} )?\\S+ \\S+ \\S+ \\d+$";
                if(line.matches(pattern)){
                    String[] args = line.split(" ");
                    if(args.length == 5) {
                        return new CommandData(command, IntStream.of(0,2,3,4).mapToObj(i -> args[i]).toArray(String[]::new));
                    } else{
                        args[0] = null;
                        return new CommandData(command, IntStream.of(0,1,2,3,4).mapToObj(i -> args[i]).toArray(String[]::new));
                    }
                }
                throw new InvalidArgumentException();
            case BALANCE:
                pattern = "^(\\d{4}\\.\\d{2}\\.\\d{2} )?\\S+ \\S+?$";
                if(line.matches(pattern)){
                    String[] args = line.split(" ");
                    if(args[0].matches("^\\d{4}\\.\\d{2}\\.\\d{2}$")){
                        if(args.length == 3){
                            return new CommandData(command, new String[]{args[0], args[2]});
                        }
                        return new CommandData(command, new String[]{args[0], null});
                    } else{
                        if(args.length == 2){
                            return new CommandData(command, new String[]{null, args[1]});
                        }
                        return new CommandData(command, new String[]{null, null});
                    }
                }
                throw new InvalidArgumentException();
        }
        return null;
    }
}
