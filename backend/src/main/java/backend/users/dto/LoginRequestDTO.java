package backend.users.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

