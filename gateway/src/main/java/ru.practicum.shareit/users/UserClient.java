package ru.practicum.shareit.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.users.dto.UserGatewayDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    /**
     * возможно, в BaseClient нужен будет дополнительный метод
     *
     * @return
     */
    public ResponseEntity<Object> getAllUsers() {
        return get("", null);
    }

    public ResponseEntity<Object> createNewUser(UserGatewayDto user) {
        return post("", user);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserGatewayDto userGatewayDto) {
        return patch("/" + userId, userGatewayDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId, userId);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId, userId);
    }
}
