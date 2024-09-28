package frgp.utn.edu.ar.quepasa.data;

public class ResponseError {
    private String message;
    public ResponseError(String message) {
        this.message = message;
    }
    public ResponseError(Exception e) {
        setMessage(e.getMessage());
    }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
