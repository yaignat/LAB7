package Exception;
/**
 * Исключение, предотвращающее бесконечную рекурсию при выполнении скриптов.
 * Выбрасывается, если скрипт пытается выполнить сам себя (прямо или косвенно).
 */
public class RecursiveScriptException extends RuntimeException {
    public RecursiveScriptException(String message) {
        super(message);
    }
}