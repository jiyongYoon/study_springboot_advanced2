package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

@Slf4j
public class CglibTest {

    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ConcreteService.class); // 프록시 클래스가 상속받을 구체클래스
        enhancer.setCallback(new TimeMethodInterceptor(target)); // invoke() 메서드(프록시 로직)가 들어있는 MethodInterceptor를 구현한 클래스
        ConcreteService proxy = (ConcreteService) enhancer.create(); // 프록시 생성 (대상클래스$$EnhancerByCGLIB$$임의코드)
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        /**
         * 23:21:27.181 [Test worker] INFO hello.proxy.cglib.CglibTest - targetClass=class hello.proxy.common.service.ConcreteService
         * 23:21:27.186 [Test worker] INFO hello.proxy.cglib.CglibTest - proxyClass=class hello.proxy.common.service.ConcreteService$$EnhancerByCGLIB$$25d6b0e3
         */

        proxy.call();
        /**
         * 23:23:01.571 [Test worker] INFO hello.proxy.cglib.CglibTest - targetClass=class hello.proxy.common.service.ConcreteService
         * 23:23:01.575 [Test worker] INFO hello.proxy.cglib.CglibTest - proxyClass=class hello.proxy.common.service.ConcreteService$$EnhancerByCGLIB$$25d6b0e3
         * 23:23:01.576 [Test worker] INFO hello.proxy.cglib.code.TimeMethodInterceptor - TimeProxy 실행
         * 23:23:01.586 [Test worker] INFO hello.proxy.common.service.ConcreteService - ConcreteService 호출
         * 23:23:01.586 [Test worker] INFO hello.proxy.cglib.code.TimeMethodInterceptor - TimeProxy 종료, resultTime=10
         */
    }

}
