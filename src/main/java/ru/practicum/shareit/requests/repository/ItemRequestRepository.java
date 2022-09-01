package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(long userId);

    @Query(value = "SELECT * " +
            "FROM requests AS r " +
            "JOIN items AS i ON r.id=i.request_id " +
            "WHERE r.requester_id = ?1",
            nativeQuery = true)
    List<ItemRequest> getItemRequestsByRequesterId(long userId);

    //    @Query(value = "SELECT * " +
//            "FROM requests AS r " +
//            "WHERE r.requester_id != ?1",
//            nativeQuery = true)
    Page<ItemRequest> findAllByRequesterIdNot(long userId, Pageable pageable);


}
