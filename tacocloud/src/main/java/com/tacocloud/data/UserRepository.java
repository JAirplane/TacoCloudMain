package com.tacocloud.data;

import com.tacocloud.domain.TacoUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<TacoUser, Long> {

    TacoUser findByUsername(String username);
}
