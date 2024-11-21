package com.uncledavecode.ai_ticket_agent.services;

import com.uncledavecode.ai_ticket_agent.model.SqlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryExecutorService {

    private final JdbcTemplate jdbcTemplate;

    public String executeQuery(SqlResponse query) {
        String result = switch (query.operation()) {
            case SELECT -> this.executeSelect(query.sql());
            case INSERT -> this.executeInsert(query.sql());
            default -> "Unsupported operation";
        };

        return result;
    }

    private String executeInsert(String query) {
        try {
            jdbcTemplate.execute(query);
            return "Insert operation successful";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    private String executeSelect(String query) {

        try {
            List<List<Object>> results = jdbcTemplate.query(query, (rs, rowNum) -> {
                int columnCount = rs.getMetaData().getColumnCount();

                List<Object> row = new ArrayList<>();

                // Iterate over the columns
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                return row;
            });

            //Convert the results to a string
            String result = results.stream()
                    .map(row -> row.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining("\n")))
                    .collect(Collectors.joining("\n"));

            return result.isEmpty() ? "No results found" : result;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }
}
