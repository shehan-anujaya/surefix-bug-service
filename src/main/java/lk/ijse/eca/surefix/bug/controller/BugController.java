package lk.ijse.eca.surefix.bug.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ijse.eca.surefix.bug.dto.BugRequest;
import lk.ijse.eca.surefix.bug.dto.BugStats;
import lk.ijse.eca.surefix.bug.dto.StatusRequest;
import lk.ijse.eca.surefix.bug.entity.Bug;
import lk.ijse.eca.surefix.bug.service.BugService;

@RestController
@RequestMapping("/api/v1/bugs")
public class BugController {

    private final BugService service;

    public BugController(BugService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Bug> create(@Valid @RequestBody BugRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    /** List bugs, newest first. All filters are optional and combine with AND. */
    @GetMapping
    public List<Bug> list(@RequestParam(required = false) Bug.Status status,
                          @RequestParam(required = false) Bug.Severity severity,
                          @RequestParam(required = false) String repo,
                          @RequestParam(required = false) String q) {
        return service.search(status, severity, repo, q);
    }

    @GetMapping("/stats")
    public BugStats stats() {
        return service.stats();
    }

    @GetMapping("/{id}")
    public Bug get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Bug update(@PathVariable Long id, @Valid @RequestBody BugRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public Bug updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return service.changeStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
