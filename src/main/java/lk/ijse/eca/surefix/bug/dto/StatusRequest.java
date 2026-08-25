package lk.ijse.eca.surefix.bug.dto;

import jakarta.validation.constraints.NotNull;
import lk.ijse.eca.surefix.bug.entity.Bug;

public record StatusRequest(@NotNull Bug.Status status) {
}
