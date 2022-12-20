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
                System.out.println(Arrays.toString(data.getArgs()));
            } catch (InvalidArgumentException | UnknownCommandException e) {
                view.showError(e);
            }
        }
    }
}
