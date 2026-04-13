package Exception;

/**
 * Исключение, возникающее при ошибке операции с файлом.
 * Используется для обработки ситуаций отсутствия файла или прав доступа.
 */
public class FileOperationException extends RuntimeException {
    public FileOperationException(String message) { super(message); }
    public FileOperationException(String message, Throwable cause) { super(message, cause); }
}