package collection;

import data.LabWork;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции, управляющий списком объектов LabWork.
 * Реализует бизнес-логику добавления, удаления, обновления и фильтрации элементов.
 */
public class CollectionManager {
    private final LinkedList<LabWork> collection;
    private final Date creationDate;

    public CollectionManager(List<LabWork> initialData) {
        this.collection = new LinkedList<>(initialData);
        this.creationDate = new Date();
        Collections.sort(collection);
    }

    public LinkedList<LabWork> getCollection() {
        return new LinkedList<>(collection);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getSize() {
        return collection.size();
    }

    /**
     * Возвращает информацию о коллекции (тип, дата создания, количество).
     */
    public String getInfo() {
        return "Тип коллекции: " + collection.getClass().getSimpleName() +
                "\nДата инициализации: " + creationDate +
                "\nКоличество элементов: " + collection.size();
    }

    /**
     * Возвращает отсортированный по имени список элементов в виде строки.
     * Требование: сортировка по имени перед выводом.
     */
    public String showSortedByName() {
        if (collection.isEmpty()) return "Коллекция пуста.";

        return collection.stream()
                .sorted((l1, l2) -> l1.getName().compareTo(l2.getName()))
                .map(LabWork::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Возвращает дату создания элемента по ID (нужно для команды update).
     */
    public Date getCreationDateById(Long id) {
        return collection.stream()
                .filter(lw -> lw.getId().equals(id))
                .findFirst()
                .map(LabWork::getCreationDate)
                .orElse(new Date());
    }

    /**
     * Выводит значения поля difficulty всех элементов в порядке убывания.
     */
    public String printFieldDescendingDifficulty() {
        if (collection.isEmpty()) return "Коллекция пуста.";

        return collection.stream()
                .sorted((l1, l2) -> l2.getDifficulty().compareTo(l1.getDifficulty())) // Сортировка по убыванию
                .map(lw -> String.valueOf(lw.getDifficulty()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Возвращает отфильтрованные элементы в виде строки.
     */
    public String filterLessThanDiscipline(String name) {
        List<String> result = collection.stream()
                .filter(lw -> lw.getDiscipline().getName().compareTo(name) < 0)
                .map(LabWork::toString)
                .collect(Collectors.toList());

        if (result.isEmpty()) return "Элементы не найдены.";
        return String.join("\n", result);
    }

    public boolean add(LabWork lw) {
        boolean res = collection.add(lw);
        if (res) Collections.sort(collection);
        return res;
    }

    public void updateById(Long id, LabWork lw) {
        collection.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .ifPresent(item -> item.update(lw));
    }

    public boolean removeById(Long id) {
        return collection.removeIf(lw -> lw.getId().equals(id));
    }

    public void clear() {
        collection.clear();
    }

    public boolean addIfMax(LabWork lw) {
        if (collection.isEmpty()) {
            collection.add(lw);
            return true;
        }
        LabWork max = collection.stream()
                .max(LabWork::compareTo)
                .orElse(null);

        if (max != null && lw.compareTo(max) > 0) {
            collection.add(lw);
            Collections.sort(collection);
            return true;
        }
        return false;
    }

    public boolean removeFirst() {
        if (!collection.isEmpty()) {
            collection.removeFirst();
            return true;
        }
        return false;
    }

    public float sumOfMinimalPoint() {
        return collection.stream()
                .map(LabWork::getMinimalPoint)
                .reduce(0f, Float::sum);
    }
}