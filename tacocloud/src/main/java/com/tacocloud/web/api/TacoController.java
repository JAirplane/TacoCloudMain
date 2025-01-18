package com.tacocloud.web.api;

import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Taco;
import com.tacocloud.web.PagingProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "api/tacos", produces = "application/json")
@CrossOrigin(origins="http://tacocloud:8080")
public class TacoController {

    private final PagingProps props;

    private final TacoRepository tacoRepo;

    @Autowired
    public TacoController(TacoRepository tacoRepo, PagingProps props) {

        this.tacoRepo = tacoRepo;
        this.props = props;
    }

    @GetMapping(params = "recent")
    public Flux<Taco> recentTacos() {
        return tacoRepo.findAll()
                .take(props.getTacosPageSize());
    }

    @GetMapping(path = "/{id}")
    public Mono<Taco> getTaco(@PathVariable("id") Long id) {
        return tacoRepo.findById(id);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Taco> postTaco(@RequestBody Taco taco) {
        return tacoRepo.save(taco);
    }
}
