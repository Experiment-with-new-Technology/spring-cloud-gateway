package com.extremecoder.carservice.controller;

import com.extremecoder.carservice.model.Car;
import com.extremecoder.carservice.repository.CarRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping(value = "/cars")
public class CarController {

    private final CarRepository carRepository;

    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("")
    public Flux<Car> all() {
        return this.carRepository.findAll();
    }

    @PostMapping("")
    public Mono<Car> create(@RequestBody Car car) {
        return this.carRepository.save(car);
    }

    @GetMapping("/{id}")
    public Mono<Car> get(@PathVariable("id") Integer id) {
        return this.carRepository.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<Car> update(@PathVariable("id") Integer id, @RequestBody Car car) {
        return this.carRepository.findById(id)
                .map(p -> {
                    p.setName(car.getName());
                    return p;
                })
                .flatMap(p -> this.carRepository.save(p));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable("id") Integer id) {
        return this.carRepository.deleteById(id);
    }
}
