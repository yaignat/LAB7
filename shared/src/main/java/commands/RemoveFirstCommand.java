package commands;

import java.io.Serial;
import java.io.Serializable;

public class RemoveFirstCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public RemoveFirstCommand() {
        super("remove_first");
    }

    @Override
    public String execute(Object context) { return "";}
}
