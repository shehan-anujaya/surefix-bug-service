package lk.ijse.eca.surefix.bug.exception;

import lk.ijse.eca.surefix.bug.entity.Bug;

public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException(Bug.Status from, Bug.Status to) {
        super("Cannot move a bug from " + from + " to " + to);
    }
}
