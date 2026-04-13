package commands;

import java.io.Serial;
import java.io.Serializable;

public class SumOfMinimalPointCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public SumOfMinimalPointCommand() {
        super("sum_of_minimal_point");
    }

    @Override
    public String execute(Object context) { return "";}
}
