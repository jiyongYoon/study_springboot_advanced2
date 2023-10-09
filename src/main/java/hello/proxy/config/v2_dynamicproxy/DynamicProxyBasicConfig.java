package hello.proxy.config.v2_dynamicproxy;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.app.v1.OrderControllerV1Impl;
import hello.proxy.app.v1.OrderRepositoryV1;
import hello.proxy.app.v1.OrderRepositoryV1Impl;
import hello.proxy.app.v1.OrderServiceV1;
import hello.proxy.app.v1.OrderServiceV1Impl;
import hello.proxy.config.v2_dynamicproxy.handler.LogTraceBasicHandler;
import hello.proxy.trace.logtrace.LogTrace;
import java.lang.reflect.Proxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicProxyBasicConfig {

    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderControllerV1 orderControllerV1 = new OrderControllerV1Impl(orderServiceV1(logTrace));

        OrderControllerV1 proxy = (OrderControllerV1) Proxy.newProxyInstance(
            orderControllerV1.getClass().getClassLoader(),
            new Class[]{OrderControllerV1.class},
            new LogTraceBasicHandler(orderControllerV1, logTrace)); // target 객체가 다르기 때문에 각각 새로운 객체를 생성해주어야 한다.

        return proxy; // 프록시 객체가 스프링 빈에 등록됨
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderServiceV1 = new OrderServiceV1Impl(orderRepositoryV1(logTrace));

        OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(
            orderServiceV1.getClass().getClassLoader(),
            new Class[]{OrderServiceV1.class},
            new LogTraceBasicHandler(orderServiceV1, logTrace)); // target 객체가 다르기 때문에 각각 새로운 객체를 생성해주어야 한다.

        return proxy; // 프록시 객체가 스프링 빈에 등록됨
    }
    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepositoryV1 = new OrderRepositoryV1Impl();

        OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(
                orderRepositoryV1.getClass().getClassLoader(),
                new Class[]{OrderRepositoryV1.class},
                new LogTraceBasicHandler(orderRepositoryV1, logTrace)); // target 객체가 다르기 때문에 각각 새로운 객체를 생성해주어야 한다.

        return proxy; // 프록시 객체가 스프링 빈에 등록됨
    }
}
