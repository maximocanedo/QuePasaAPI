package frgp.utn.edu.ar.quepasa.exception;

import org.springframework.http.HttpStatus;

public class Fail extends RuntimeException {

    private String message = "";
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public Fail(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public Fail(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
    public Fail() {
        this("Unknown error");
    }

    public String getMessage() { return message; }
    public HttpStatus getStatus() { return status; }

}
