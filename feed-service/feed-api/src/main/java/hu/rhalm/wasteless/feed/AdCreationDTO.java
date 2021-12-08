package hu.rhalm.wasteless.feed;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AdCreationDTO {
    @NotEmpty
    private String title;

    @NotEmpty
    private String location;

    @NotEmpty
    private String category;

    private String description;
}
