package ru.practicum.shareit.item.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {
    private long id;
    @NotBlank (groups = OnCreate.class)
    private String name;
    @NotBlank (groups = OnCreate.class)
    private String description;
    @NotNull (groups = OnCreate.class)
    private Boolean available;
    private long ownerId;
    private Long request;

    public ItemDto(long id, String name, String description, Boolean available, long ownerId, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
        this.request = request;
    }
}
