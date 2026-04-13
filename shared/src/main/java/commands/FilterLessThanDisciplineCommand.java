package commands;

import java.io.Serial;
import java.io.Serializable;

public class FilterLessThanDisciplineCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String disciplineName;

    public FilterLessThanDisciplineCommand(String disciplineName) {
        super("filter_less_than_discipline");
        this.disciplineName = disciplineName;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    @Override
    public String execute(Object context) { return "";}
}
