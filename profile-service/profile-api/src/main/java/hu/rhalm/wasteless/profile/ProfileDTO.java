package hu.rhalm.wasteless.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private boolean mine = false;
    @NotNull
    private UUID id = UUID.randomUUID();

    private String userId;

    @NotEmpty
    private String username;

    private String introduction;

    private URL image;
}
