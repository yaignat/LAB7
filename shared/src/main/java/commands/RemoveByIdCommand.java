package commands;

import java.io.Serializable;
import java.io.Serial;

public class RemoveByIdCommand extends Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id; // Лучше сделать final

    public RemoveByIdCommand(Long id) {
        super("remove_by_id");
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String execute(Object context) {
        return "";
    }
}