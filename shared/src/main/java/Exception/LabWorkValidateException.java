package Exception;
/**
 * Исключение, возникающее при неудачной валидации данных лабораторной работы.
 * Например, при дублировании ID или нарушении целостности полей.
 */
public class LabWorkValidateException extends RuntimeException {
    public LabWorkValidateException(String message) {
        super(message);
    }
}
