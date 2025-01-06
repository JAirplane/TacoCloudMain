package com.tacocloud.data;

import com.tacocloud.domain.TacoOrder;
import com.tacocloud.domain.TacoUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<TacoOrder, Long> {

    List<TacoOrder> findByUserOrderByPlacedAtDesc(TacoUser user, Pageable page);
}
