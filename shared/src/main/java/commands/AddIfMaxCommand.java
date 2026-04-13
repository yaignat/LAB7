package commands;

import data.LabWork;

import java.io.Serial;
import java.io.Serializable;

public class AddIfMaxCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private LabWork labWork;

    public AddIfMaxCommand(LabWork labWork) {
        super("add_if_max");
        this.labWork = labWork;
    }

    public LabWork getLabWork() {
        return labWork;
    }

    @Override
    public String execute(Object object) { return "";}
}
