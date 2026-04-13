package commands;

import java.io.Serial;
import java.io.Serializable;

public class SaveCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public SaveCommand() {
        super("save");
    }

    @Override
    public String execute(Object context) { return "";}
}
