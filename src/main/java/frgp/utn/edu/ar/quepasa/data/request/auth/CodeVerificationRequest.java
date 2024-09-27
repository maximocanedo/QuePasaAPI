package frgp.utn.edu.ar.quepasa.data.request.auth;

public class CodeVerificationRequest {

    private String subject;
    private String code;

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

}
