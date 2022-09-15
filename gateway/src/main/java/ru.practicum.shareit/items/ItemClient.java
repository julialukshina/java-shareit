package ru.practicum.shareit.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.items.dto.CommentGatewayDto;
import ru.practicum.shareit.items.dto.ItemGatewayDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, ItemGatewayDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateItem(long userId, Long itemId, ItemGatewayDto itemGatewayDto) {
        return patch("/" + itemId, userId, itemGatewayDto);
    }

    public ResponseEntity<Object> getItemById(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getSearchableItem(String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> getItemsOfUser(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createNewComment(long userId, long itemId, CommentGatewayDto commentGatewayDto) {
        return post("/" + itemId + "/comment", userId, commentGatewayDto);
    }
}
