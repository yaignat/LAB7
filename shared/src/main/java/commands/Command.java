package commands;

import java.io.Serial;
import java.io.Serializable;
/**
 * Базовый класс для всех команд.
 * Все команды должны быть сериализуемы для передачи по сети.
 */
public abstract class Command implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String type;

    public Command(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    /**
     * Метод выполнения команды.
     * Реализуется в конкретных командах, но логика будет вызываться на сервере.
     * @param context Контекст выполнения (обычно CollectionManager)
     * @return Результат выполнения (сообщение для пользователя)
     */
    public abstract String execute(Object context);
}
