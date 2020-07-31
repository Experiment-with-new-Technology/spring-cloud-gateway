package com.extremecoder.carservice.repository;

import com.extremecoder.carservice.model.Car;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends ReactiveCrudRepository<Car, Integer> {
}