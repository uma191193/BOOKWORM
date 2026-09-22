package com.bookworm.service.impl;

import com.bookworm.service.SeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code sample-data.sql} from the classpath and executes every
 * non-empty, non-comment statement via a raw JDBC connection.
 *
 * <p>The SQL uses H2 {@code MERGE … KEY(…)} syntax which is idempotent —
 * rows are inserted on first run and updated on subsequent runs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeedServiceImpl implements SeedService {

    private static final String SQL_FILE = "sample-data.sql";

    private final DataSource dataSource;

    @Override
    public SeedResult execute() {
        String sql = loadSqlFile();
        List<String> statements = splitStatements(sql);

        int executed = 0;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String statement : statements) {
                stmt.execute(statement);
                executed++;
                log.debug("Seed statement executed: {}...",
                        statement.substring(0, Math.min(60, statement.length())).replace('\n', ' '));
            }
        } catch (SQLException e) {
            log.error("Seed execution failed after {} statements: {}", executed, e.getMessage());
            throw new RuntimeException("Seed execution failed: " + e.getMessage(), e);
        }

        String message = "Seed completed: %d statements executed from %s.".formatted(executed, SQL_FILE);
        log.info(message);
        return new SeedResult(executed, message);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private String loadSqlFile() {
        try {
            ClassPathResource resource = new ClassPathResource(SQL_FILE);
            try (InputStream is = resource.getInputStream()) {
                return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot load " + SQL_FILE + " from classpath", e);
        }
    }

    /**
     * Splits the SQL text into individual executable statements by semicolon,
     * skipping blank lines and full-line comments (-- …).
     */
    private List<String> splitStatements(String sql) {
        List<String> result = new ArrayList<>();
        for (String raw : sql.split(";")) {
            // Remove comment-only lines, then trim whitespace
            String cleaned = removeCommentLines(raw).trim();
            if (!cleaned.isEmpty()) {
                result.add(cleaned);
            }
        }
        return result;
    }

    private String removeCommentLines(String block) {
        StringBuilder sb = new StringBuilder();
        for (String line : block.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--") && !trimmed.isEmpty()) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }
}
