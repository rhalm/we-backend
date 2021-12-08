package hu.rhalm.wasteless.messaging;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MessageCreateDTO {
    @NotEmpty
    private String content;
}
