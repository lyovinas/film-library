package ru.sbercourse.filmlibrary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import ru.sbercourse.filmlibrary.model.Order;

@Repository
public interface OrderRepository extends GenericRepository<Order>{

    Page<Order> getAllByUserIdAndIsDeletedFalse(Long userId, PageRequest pageRequest);
}
