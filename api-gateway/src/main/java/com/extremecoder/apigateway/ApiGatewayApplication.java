package com.extremecoder.apigateway;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@EnableEurekaClient
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("", r -> r.path("/cars/**")
                        .filters(
                                f -> f.hystrix(c -> c.setName("carsFallback")
                                        .setFallbackUri("forward:/car-fallback"))
                        )
                        .uri("lb://car-service"))
                .build();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

@Data
class Car {
    private Integer id;
    private String name;
}

@RestController
class FavoriteCarsController {
    private final WebClient.Builder carClient;

    FavoriteCarsController(WebClient.Builder carClient) {
        this.carClient = carClient;
    }

    @GetMapping("/fav-cars")
    public Flux<Car> favCars() {
        return carClient.build().get().uri("lb://car-service/cars")
                .retrieve().bodyToFlux(Car.class)
                .filter(this::isFavorite);
    }

    private boolean isFavorite(Car car) {
        return car.getName().equals("test");
    }
}

@RestController
class FallBackController {
    @GetMapping("/car-fallback")
    public Flux<Car> noCars() {
        return Flux.empty();
    }
}