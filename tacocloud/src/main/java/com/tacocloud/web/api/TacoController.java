package com.tacocloud.web.api;

import com.tacocloud.data.TacoRepository;
import com.tacocloud.domain.Taco;
import com.tacocloud.services.TacoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/tacos", produces = "application/json")
@CrossOrigin(origins="http://tacocloud:8080")
public class TacoController {

    private final TacoService tacoService;
    private final TacoRepository tacoRepo;

    @Autowired
    public TacoController(TacoRepository tacoRepo, TacoService tacoService) {

        this.tacoRepo = tacoRepo;
        this.tacoService = tacoService;
    }

    @GetMapping(params = "recent")
    public Iterable<Taco> recentTacos() {
        return tacoService.getRecentTacos();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Taco> getTaco(@PathVariable("id") Long id) {
        var taco = tacoService.getTacoById(id);
        return taco.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody Taco taco) {
        return tacoRepo.save(taco);
    }
}
