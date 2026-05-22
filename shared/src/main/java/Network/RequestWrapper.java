package Network;

import commands.Command;

import java.io.Serial;
import java.io.Serializable;

public class RequestWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String login;
    private final String passwordHash;
    private final Command command;

    public RequestWrapper(String login, String passwordHash, Command command) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.command = command;
    }

    public String getLogin() { return login; }
    public String getPasswordHash() { return passwordHash; }
    public Command getCommand() { return command; }
}