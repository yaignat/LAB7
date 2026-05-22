package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class PgPassReader {
    private static final Logger logger = LoggerFactory.getLogger(PgPassReader.class);

    public static String[] loadCredentials(String host, int port, String database) {
        String pgPassPath = System.getenv("PGPASSFILE");
        if (pgPassPath == null || pgPassPath.isEmpty()) {
            pgPassPath = Path.of(System.getProperty("user.home"), ".pgpass").toString();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(pgPassPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

                String[] parts = trimmed.split(":", 5);
                if (parts.length != 5) continue;

                if (!matches(parts[0], host) ||
                        !matches(parts[1], String.valueOf(port)) ||
                        !matches(parts[2], database)) {
                    continue;
                }

                logger.info("Credentials loaded from .pgpass for {}:{}", host, database);
                return new String[]{parts[3], parts[4]};
            }
        } catch (IOException e) {
            logger.error("Failed to read .pgpass: {}", e.getMessage());
        }
        return new String[]{"", ""};
    }

    private static boolean matches(String fileField, String required) {
        return "*".equals(fileField) || Objects.equals(fileField, required);
    }
}