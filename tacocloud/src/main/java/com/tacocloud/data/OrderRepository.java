package com.tacocloud.data;

import com.tacocloud.domain.TacoOrder;
import com.tacocloud.domain.TacoUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.List;

public interface OrderRepository extends ReactiveCrudRepository<TacoOrder, Long> {}
