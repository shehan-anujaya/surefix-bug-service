package lk.ijse.eca.surefix.bug.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lk.ijse.eca.surefix.bug.entity.Bug;

public record BugRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 4000) String description,
        Bug.Severity severity,
        @Size(max = 200) String targetRepo,
        @Size(max = 100) String reporter,
        @Size(max = 100) String assignee,
        @Size(max = 10) List<@Size(max = 50) String> tags) {
}
