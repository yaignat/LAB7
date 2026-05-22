import security.PasswordUtils;

public class HashGen {
    public static void main(String[] args) {
        String password = "123";
        String hash = PasswordUtils.hashPassword(password);
        System.out.println("Длина хэша: " + hash.length());
        System.out.println("Хэш: " + hash);
        System.out.println("\nSQL для вставки:");
        System.out.println("INSERT INTO users (login, password_hash) VALUES ('admin', '" + hash + "');");
    }
}