package lk.ijse.eca.surefix.bug.exception;

public class BugNotFoundException extends RuntimeException {
    public BugNotFoundException(Long id) {
        super("Bug not found: " + id);
    }
}
