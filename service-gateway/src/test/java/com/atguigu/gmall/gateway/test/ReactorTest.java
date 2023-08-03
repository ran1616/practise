package com.atguigu.gmall.gateway.test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

public class ReactorTest {

    public static void main(String[] args) {

//        Mono<String> mono = getResultData();
//        mono.subscribe((s) -> {
//            System.out.println(s);
//        }) ;
//
//        System.out.println("ReactorTest...");
        Flux<String> flux = getFluxData();
        flux.subscribe(s -> System.out.println(s)) ;

    }

    // 响应式编程Flux
    public static Flux<String> getFluxData() {
        Flux<String> dataFlux = Flux.fromStream(Stream.of("atguigu" , "very")) ;
        return dataFlux ;
    }

    // 响应式编程
    public static Mono<String> getResultData() {
        Mono<String> mono = Mono.fromCallable(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "ok";
        });
        return mono ;

    }

    // 阻塞式编程
    public static String getData() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok" ;
    }

}
