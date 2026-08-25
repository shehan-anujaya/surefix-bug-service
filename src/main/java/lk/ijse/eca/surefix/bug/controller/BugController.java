package lk.ijse.eca.surefix.bug.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lk.ijse.eca.surefix.bug.dto.BugRequest;
import lk.ijse.eca.surefix.bug.dto.StatusRequest;
import lk.ijse.eca.surefix.bug.entity.Bug;
import lk.ijse.eca.surefix.bug.repository.BugRepository;

@RestController
@RequestMapping("/api/v1/bugs")
public class BugController {

    private final BugRepository bugs;

    public BugController(BugRepository bugs) {
        this.bugs = bugs;
    }

    @PostMapping
    public ResponseEntity<Bug> create(@Valid @RequestBody BugRequest request) {
        Bug bug = new Bug();
        apply(bug, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bugs.save(bug));
    }

    @GetMapping
    public List<Bug> list() {
        return bugs.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    public Bug get(@PathVariable Long id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public Bug update(@PathVariable Long id, @Valid @RequestBody BugRequest request) {
        Bug bug = find(id);
        apply(bug, request);
        return bugs.save(bug);
    }

    @PatchMapping("/{id}/status")
    public Bug updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        Bug bug = find(id);
        bug.setStatus(request.status());
        bug.setUpdatedAt(Instant.now());
        return bugs.save(bug);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bugs.delete(find(id));
        return ResponseEntity.noContent().build();
    }

    private Bug find(Long id) {
        return bugs.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug not found: " + id));
    }

    private static void apply(Bug bug, BugRequest r) {
        bug.setTitle(r.title());
        bug.setDescription(r.description());
        if (r.severity() != null) bug.setSeverity(r.severity());
        bug.setTargetRepo(r.targetRepo());
        bug.setUpdatedAt(Instant.now());
    }
}
