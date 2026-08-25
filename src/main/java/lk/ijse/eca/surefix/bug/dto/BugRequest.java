package lk.ijse.eca.surefix.bug.dto;

import jakarta.validation.constraints.NotBlank;
import lk.ijse.eca.surefix.bug.entity.Bug;

public record BugRequest(
        @NotBlank String title,
        String description,
        Bug.Severity severity,
        String targetRepo) {
}
