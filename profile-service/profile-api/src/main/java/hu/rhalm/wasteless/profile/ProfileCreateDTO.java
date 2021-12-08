package hu.rhalm.wasteless.profile;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ProfileCreateDTO {
    @NotEmpty
    private String username;
}
