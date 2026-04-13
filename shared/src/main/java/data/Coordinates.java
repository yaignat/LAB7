package data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
/**
 * Класс для хранения координат лабораторной работы.
 * Координаты представляют собой пару чисел X и Y с ограничениями на минимальные значения.
 * Реализует Comparable для сортировки по координате X.
 */


public class Coordinates implements Serializable, Comparable<Coordinates> {
    @Serial
    private static final long serialVersionUID = 1L;

    private double x;
    private long y;
    /**
     * Создает объект координат с проверкой допустимых диапазонов.
     *
     * @param x координата по оси X (должна быть > -279)
     * @param y координата по оси Y (должна быть > -240)
     * @throws IllegalArgumentException если значения выходят за границы
     */
    public Coordinates(double x, long y) {
        if (x <= -279) throw new IllegalArgumentException("X должен быть больше -279");
        if (y <= -240) throw new IllegalArgumentException("Y должен быть больше -240");
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public long getY() { return y; }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(that.x, x) == 0 && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public int compareTo(Coordinates o) {
        return Double.compare(this.x, o.x);
    }
}