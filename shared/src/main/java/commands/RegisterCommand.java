package commands;

import java.io.Serial;

public class RegisterCommand extends Command {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String passwordHash;

    public RegisterCommand(String login, String passwordHash) {
        super("register");
        this.login = login;
        this.passwordHash= passwordHash;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String execute(Object context) {
        return "";
    }
}
