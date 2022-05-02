package fr.inria.diverse.model.exception;


public class BusinessCheckedException extends Exception {

    private static final long serialVersionUID = -8460356990632230194L;

    private final ErrorCode code;

    public BusinessCheckedException(ErrorCode code) {
        super();
        this.code = code;
    }

    public BusinessCheckedException(String message, Throwable cause, ErrorCode code) {
        super(message, cause);
        this.code = code;
    }

    public BusinessCheckedException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public BusinessCheckedException(Throwable cause, ErrorCode code) {
        super(cause);
        this.code = code;
    }

    public ErrorCode getCode() {
        return this.code;
    }
}