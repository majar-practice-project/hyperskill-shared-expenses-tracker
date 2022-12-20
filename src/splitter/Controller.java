package splitter;

import splitter.view.CommandView;
import splitter.view.InvalidArgumentException;
import splitter.view.UnknownCommandException;

public class Controller {
    private CommandView view = new CommandView();

    public void start(){
        while(true) {
            try{
                view.getCommand();
            } catch (InvalidArgumentException | UnknownCommandException e) {
                view.showError(e);
            }
        }
    }
}
