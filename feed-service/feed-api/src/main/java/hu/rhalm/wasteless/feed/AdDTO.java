package hu.rhalm.wasteless.feed;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class AdDTO {
    @NotNull
    private UUID id;

    @NotNull
    private String userId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String location;

    @NotEmpty
    private String category;

    private String description;

    private List<URL> images;

    @NotNull
    private Instant createdAt;
}
