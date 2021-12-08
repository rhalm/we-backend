package hu.rhalm.wasteless.feed.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
public class CategoryEntity {
    @NotNull
    @Id
    private Integer id;

    @NotNull
    private String name;
}
