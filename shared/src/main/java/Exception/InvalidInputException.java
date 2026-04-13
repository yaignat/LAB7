package Exception;
/**
 * Исключение, возникающее при некорректном вводе данных пользователем.
 * Например, ввод букв вместо чисел или значений вне допустимого диапазона.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
    public InvalidInputException(String message, Throwable cause) { super(message, cause); }
}