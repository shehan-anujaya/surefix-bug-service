package lk.ijse.eca.surefix.bug.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/** A defect reported against a target repository and tracked through the SureFix pipeline. */
@Entity
@Table(name = "bugs", indexes = {
        @Index(name = "idx_bugs_status", columnList = "status"),
        @Index(name = "idx_bugs_severity", columnList = "severity")
})
public class Bug {

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    /**
     * Lifecycle: NEEDS_INFO (reported) -> AWAITING_APPROVAL (reproduced) -> FIXING -> FIXED -> CLOSED.
     * A bug can be reopened (-> NEEDS_INFO) from any state; CLOSED can only be reopened.
     */
    public enum Status { NEEDS_INFO, AWAITING_APPROVAL, FIXING, FIXED, CLOSED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity = Severity.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Status status = Status.NEEDS_INFO;

    @Column(length = 200)
    private String targetRepo;

    @Column(length = 100)
    private String reporter;

    @Column(length = 100)
    private String assignee;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bug_tags", joinColumns = @JoinColumn(name = "bug_id"))
    @Column(name = "tag", length = 50)
    private List<String> tags = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getTargetRepo() { return targetRepo; }
    public void setTargetRepo(String targetRepo) { this.targetRepo = targetRepo; }
    public String getReporter() { return reporter; }
    public void setReporter(String reporter) { this.reporter = reporter; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
