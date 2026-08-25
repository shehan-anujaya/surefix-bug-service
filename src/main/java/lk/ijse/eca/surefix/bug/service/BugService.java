package lk.ijse.eca.surefix.bug.service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lk.ijse.eca.surefix.bug.dto.BugRequest;
import lk.ijse.eca.surefix.bug.dto.BugStats;
import lk.ijse.eca.surefix.bug.entity.Bug;
import lk.ijse.eca.surefix.bug.entity.Bug.Status;
import lk.ijse.eca.surefix.bug.exception.BugNotFoundException;
import lk.ijse.eca.surefix.bug.exception.InvalidTransitionException;
import lk.ijse.eca.surefix.bug.repository.BugRepository;
import lk.ijse.eca.surefix.bug.repository.BugSpecifications;

@Service
@Transactional
public class BugService {

    /** Allowed status transitions (same-state changes are accepted as no-ops). */
    static final Map<Status, Set<Status>> TRANSITIONS = new EnumMap<>(Map.of(
            Status.NEEDS_INFO,        EnumSet.of(Status.AWAITING_APPROVAL, Status.FIXING, Status.FIXED, Status.CLOSED),
            Status.AWAITING_APPROVAL, EnumSet.of(Status.NEEDS_INFO, Status.FIXING, Status.FIXED, Status.CLOSED),
            Status.FIXING,            EnumSet.of(Status.NEEDS_INFO, Status.AWAITING_APPROVAL, Status.FIXED, Status.CLOSED),
            Status.FIXED,             EnumSet.of(Status.NEEDS_INFO, Status.CLOSED),
            Status.CLOSED,            EnumSet.of(Status.NEEDS_INFO)));

    private final BugRepository bugs;

    public BugService(BugRepository bugs) {
        this.bugs = bugs;
    }

    public Bug create(BugRequest request) {
        Bug bug = new Bug();
        apply(bug, request);
        return bugs.save(bug);
    }

    @Transactional(readOnly = true)
    public List<Bug> search(Status status, Bug.Severity severity, String repo, String q) {
        Specification<Bug> spec = Specification.allOf(
                BugSpecifications.hasStatus(status),
                BugSpecifications.hasSeverity(severity),
                BugSpecifications.hasRepo(repo),
                BugSpecifications.matches(q));
        return bugs.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional(readOnly = true)
    public Bug get(Long id) {
        return bugs.findById(id).orElseThrow(() -> new BugNotFoundException(id));
    }

    public Bug update(Long id, BugRequest request) {
        Bug bug = get(id);
        apply(bug, request);
        return bugs.save(bug);
    }

    public Bug changeStatus(Long id, Status target) {
        Bug bug = get(id);
        Status current = bug.getStatus();
        if (current != target) {
            if (!TRANSITIONS.get(current).contains(target)) {
                throw new InvalidTransitionException(current, target);
            }
            bug.setStatus(target);
        }
        return bugs.save(bug);
    }

    public void delete(Long id) {
        bugs.delete(get(id));
    }

    @Transactional(readOnly = true)
    public BugStats stats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Status s : Status.values()) byStatus.put(s.name(), 0L);
        bugs.countByStatus().forEach(r -> byStatus.put(((Status) r[0]).name(), (Long) r[1]));
        Map<String, Long> bySeverity = new LinkedHashMap<>();
        for (Bug.Severity s : Bug.Severity.values()) bySeverity.put(s.name(), 0L);
        bugs.countBySeverity().forEach(r -> bySeverity.put(((Bug.Severity) r[0]).name(), (Long) r[1]));
        long total = byStatus.values().stream().mapToLong(Long::longValue).sum();
        long open = total - byStatus.get(Status.FIXED.name()) - byStatus.get(Status.CLOSED.name());
        return new BugStats(total, open, byStatus, bySeverity);
    }

    private static void apply(Bug bug, BugRequest r) {
        bug.setTitle(r.title().trim());
        bug.setDescription(r.description());
        if (r.severity() != null) bug.setSeverity(r.severity());
        bug.setTargetRepo(r.targetRepo());
        bug.setReporter(r.reporter());
        bug.setAssignee(r.assignee());
        bug.setTags(r.tags() == null ? List.of() : r.tags().stream().map(String::trim).filter(t -> !t.isEmpty()).distinct().toList());
    }
}
