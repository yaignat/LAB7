package commands;

import java.io.Serial;
import java.io.Serializable;

public class PrintFieldDescendingDifficultyCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public PrintFieldDescendingDifficultyCommand() {
        super("print_field_descending_difficulty");
    }

    @Override
    public String execute(Object context) { return "";}
}
