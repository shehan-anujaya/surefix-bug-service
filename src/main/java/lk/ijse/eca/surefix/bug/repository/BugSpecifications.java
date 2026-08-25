package lk.ijse.eca.surefix.bug.repository;

import org.springframework.data.jpa.domain.Specification;

import lk.ijse.eca.surefix.bug.entity.Bug;

/** Composable filters for the bug list endpoint. */
public final class BugSpecifications {

    private BugSpecifications() {}

    public static Specification<Bug> hasStatus(Bug.Status status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Bug> hasSeverity(Bug.Severity severity) {
        return (root, query, cb) -> severity == null ? null : cb.equal(root.get("severity"), severity);
    }

    public static Specification<Bug> hasRepo(String repo) {
        return (root, query, cb) -> repo == null || repo.isBlank() ? null : cb.equal(root.get("targetRepo"), repo);
    }

    /** Case-insensitive match on title or description. */
    public static Specification<Bug> matches(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;
            String like = "%" + q.trim().toLowerCase() + "%";
            return cb.or(cb.like(cb.lower(root.get("title")), like),
                         cb.like(cb.lower(cb.coalesce(root.get("description"), "")), like));
        };
    }
}
