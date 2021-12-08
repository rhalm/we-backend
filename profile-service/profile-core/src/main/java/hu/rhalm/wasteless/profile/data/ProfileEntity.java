package hu.rhalm.wasteless.profile.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ProfileEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Column(unique = true)
    private String userId;

    @NotEmpty
    private String username;

    private String introduction;

    @OneToOne
    private ImageEntity image;
}
