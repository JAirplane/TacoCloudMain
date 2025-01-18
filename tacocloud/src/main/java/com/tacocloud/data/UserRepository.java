package com.tacocloud.data;

import com.tacocloud.domain.TacoUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<TacoUser, Long> {

    Mono<TacoUser> findByUsername(String username);
}
