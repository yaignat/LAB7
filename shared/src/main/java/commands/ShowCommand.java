package commands;

import java.io.Serial;
import java.io.Serializable;

public class ShowCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public ShowCommand() {
        super("show");
    }

    @Override
    public String execute(Object context) {return "";}
}
