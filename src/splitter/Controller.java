package splitter;

import splitter.view.CommandView;
import splitter.view.InvalidArgumentException;
import splitter.view.UnknownCommandException;
import splitter.view.request.CommandData;

import java.util.Arrays;

public class Controller {
    private CommandView view = new CommandView();

    public void start(){
        while(true) {
            try{
                CommandData data = view.getCommand();
                switch (data.getCommand()){
                    case EXIT:
                        System.exit(0);
                    case HELP:
                        view.showAllCommands();
                }
            } catch (InvalidArgumentException | UnknownCommandException e) {
                view.showError(e);
            }
        }
    }


}
