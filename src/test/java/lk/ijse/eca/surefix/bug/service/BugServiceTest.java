package lk.ijse.eca.surefix.bug.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lk.ijse.eca.surefix.bug.dto.BugRequest;
import lk.ijse.eca.surefix.bug.entity.Bug;
import lk.ijse.eca.surefix.bug.entity.Bug.Status;
import lk.ijse.eca.surefix.bug.exception.BugNotFoundException;
import lk.ijse.eca.surefix.bug.exception.InvalidTransitionException;
import lk.ijse.eca.surefix.bug.repository.BugRepository;

class BugServiceTest {

    private BugRepository repo;
    private BugService service;

    @BeforeEach
    void setUp() {
        repo = mock(BugRepository.class);
        service = new BugService(repo);
        when(repo.save(any(Bug.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Bug bugIn(Status status) {
        Bug bug = new Bug();
        bug.setTitle("t");
        bug.setStatus(status);
        when(repo.findById(1L)).thenReturn(Optional.of(bug));
        return bug;
    }

    @Test
    void reproducedBugMovesToAwaitingApproval() {
        bugIn(Status.NEEDS_INFO);
        assertEquals(Status.AWAITING_APPROVAL, service.changeStatus(1L, Status.AWAITING_APPROVAL).getStatus());
    }

    @Test
    void closedBugCanOnlyBeReopened() {
        bugIn(Status.CLOSED);
        assertThrows(InvalidTransitionException.class, () -> service.changeStatus(1L, Status.FIXING));
        assertEquals(Status.NEEDS_INFO, service.changeStatus(1L, Status.NEEDS_INFO).getStatus());
    }

    @Test
    void fixedBugCannotGoBackToFixingWithoutReopening() {
        bugIn(Status.FIXED);
        assertThrows(InvalidTransitionException.class, () -> service.changeStatus(1L, Status.FIXING));
    }

    @Test
    void sameStatusIsANoOp() {
        Bug bug = bugIn(Status.FIXING);
        assertEquals(Status.FIXING, service.changeStatus(1L, Status.FIXING).getStatus());
        verify(repo).save(bug);
    }

    @Test
    void unknownBugRaisesNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BugNotFoundException.class, () -> service.get(99L));
        verify(repo, never()).save(any());
    }

    @Test
    void createTrimsTitleAndNormalisesTags() {
        Bug bug = service.create(new BugRequest("  Login broken ", null, null, "repo", "qa", null,
                List.of(" ui ", "ui", "", "safari")));
        assertEquals("Login broken", bug.getTitle());
        assertEquals(List.of("ui", "safari"), bug.getTags());
        assertEquals(Bug.Severity.MEDIUM, bug.getSeverity());
        assertEquals(Status.NEEDS_INFO, bug.getStatus());
    }
}
