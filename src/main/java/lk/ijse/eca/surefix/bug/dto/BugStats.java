package lk.ijse.eca.surefix.bug.dto;

import java.util.Map;

public record BugStats(long total, long open, Map<String, Long> byStatus, Map<String, Long> bySeverity) {
}
