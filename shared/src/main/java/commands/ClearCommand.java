package commands;

import java.io.Serial;
import java.io.Serializable;

public class ClearCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public ClearCommand() {
        super("clear");
    }

    @Override
    public String execute(Object context) {return "";}
}
