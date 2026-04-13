package utility;

import data.*;
import Exception.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Утилитный класс для валидации объектов LabWork.
 * Содержит статические методы проверки корректности данных (уникальность ID, диапазоны значений).
 * Класс не предназначен для создания экземпляров.
 */
public class LabWorkValidator {
    private LabWorkValidator() {}
    /**
     * Выполняет валидацию массива объектов LabWork.
     * Проверяет уникальность ID, отсутствие null-полей и соответствие числовых значений диапазонам.
     * Обновляет счетчик следующего доступного ID.
     *
     * @param works массив объектов LabWork для проверки
     * @throws LabWorkValidateException если найдены ошибки валидации
     */
    public static void validateLabWorks(LabWork[] works) {
        Set<Long> ids = new HashSet<>();
        long maxId = 0;

        for (LabWork lw : works) {
            if (lw.getId() <= 0 || !ids.add(lw.getId())) {
                throw new LabWorkValidateException("Ошибка валидации: некорректный или дублирующийся ID");
            }
            if (lw.getName() == null || lw.getCoordinates() == null || lw.getDifficulty() == null || lw.getDiscipline() == null) {
                throw  new LabWorkValidateException("Ошибка валидации: null поля");
            }
            maxId = Math.max(maxId, lw.getId());
        }
        LabWork.setNextId(maxId + 1);
    }
}
