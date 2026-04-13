package data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
/**
 * Класс, описывающий учебную дисциплину, связанную с лабораторной работой.
 * Содержит название дисциплины и количество лекционных часов (может быть null).
 */


public class Discipline implements Serializable, Comparable<Discipline> {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private Integer lectureHours;
    /**
     * Создает объект дисциплины.
     *
     * @param name название дисциплины (не может быть null или пустым)
     * @param lectureHours количество лекционных часов (может быть null)
     * @throws IllegalArgumentException если имя пустое
     */
    public Discipline(String name, Integer lectureHours) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название дисциплины не может быть пустым");
        }
        this.name = name;
        this.lectureHours = lectureHours;
    }

    public String getName() { return name; }
    public Integer getLectureHours() { return lectureHours; }

    @Override
    public String toString() {
        return name + (lectureHours != null ? " (" + lectureHours + "ч)" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discipline that = (Discipline) o;
        return Objects.equals(name, that.name) && Objects.equals(lectureHours, that.lectureHours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lectureHours);
    }

    @Override
    public int compareTo(Discipline o) {
        return this.name.compareTo(o.name);
    }
}