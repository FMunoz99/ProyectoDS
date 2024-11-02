package backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataBaseLoader implements CommandLineRunner {

    @Value("${db.backup.path}")
    private String backupFilePath;

    final private JdbcTemplate jdbcTemplate;
    final private ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {
        Boolean dataLoaded = checkDataLoaded();
        if (!dataLoaded) {
            loadDataFromFile();
        }
    }

    private Boolean checkDataLoaded() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users",
                    Integer.class
            );
            return count != null && count > 0;
        } catch (Exception e) {
            // Si la tabla no existe o cualquier otro error
            return false;
        }
    }

    private void loadDataFromFile() throws Exception {
        Resource resource = resourceLoader.getResource("file:" + backupFilePath);
        Path path = resource.getFile().toPath();

        List<String> lines = Files.readAllLines(path);
        StringBuilder sqlBuilder = new StringBuilder();
        for (String line : lines) {
            sqlBuilder.append(line);
            if (line.trim().endsWith(";")) {
                String sql = sqlBuilder.toString();
                jdbcTemplate.execute(sql);
                sqlBuilder = new StringBuilder();
            }
        }
    }
}
