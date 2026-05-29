package org.example.databasetermpaper.model;

import java.util.LinkedHashMap;
import java.util.Map;

public record ReportRow(Map<String, Object> values) {
    public static ReportRow of(LinkedHashMap<String, Object> values) {
        return new ReportRow(values);
    }
}
