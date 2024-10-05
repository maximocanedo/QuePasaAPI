package frgp.utn.edu.ar.quepasa.data.request.auth;

import java.util.UUID;

public class PasswordResetAttempt {
    private UUID id;
    private String code;
    private String newPassword;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
