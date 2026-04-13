package commands;

import java.io.Serial;
import java.io.Serializable;

public class ExitCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public ExitCommand() {
        super("exit");
    }

    @Override
    public String execute(Object context) { return "";}
}
