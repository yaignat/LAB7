package commands;

import java.io.Serial;
import java.io.Serializable;

public class InfoCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public InfoCommand() {
        super("info");
    }

    @Override
    public String execute(Object context) {return "";}
}
