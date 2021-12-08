package hu.rhalm.wasteless.feed;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CategoryDTO {
    @NotNull
    private long id;

    @NotNull
    private String name;
}
