package ru.practicum.shareit.users.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserGatewayDto {
    private long id;
    @NotNull(groups = OnCreate.class)
    @NotBlank(groups = OnCreate.class)
    private String name;
    @Email(groups = OnCreate.class)
    @NotNull(groups = OnCreate.class)
    @NotBlank(groups = OnCreate.class)
    private String email;

    public UserGatewayDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
