package hello.proxy.proxyfactory;

import static org.assertj.core.api.Assertions.*;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

@Slf4j
public class ProxyFactoryTest {

    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();
        /**
         * 23:17:03.288 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - targetClass=class hello.proxy.common.service.ServiceImpl
         * 23:17:03.294 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - proxyClass=class com.sun.proxy.$Proxy13  <-- JDK 동적 프록시 사용
         * 23:17:03.305 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
         * 23:17:03.305 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
         * 23:17:03.306 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료, resultTime=1
         */

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse();
    }

    @Test
    @DisplayName("인터페이스가 없으면(=구체 클래스만 있으면) CGLIB 동적 프록시 사용")
    void concreteProxy() {
        ConcreteService target = new ConcreteService();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();
        /**
         * 23:22:28.640 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - targetClass=class hello.proxy.common.service.ConcreteService
         * 23:22:28.644 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - proxyClass=class hello.proxy.common.service.ConcreteService$$EnhancerBySpringCGLIB$$cec4f031
         * 23:22:28.649 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
         * 23:22:28.662 [Test worker] INFO hello.proxy.common.service.ConcreteService - ConcreteService 호출
         * 23:22:28.662 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료, resultTime=13
         */

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용")
    void proxyTargetClass() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true); // 타겟 클래스를 기반으로 프록시를 만들거야!
        proxyFactory.addAdvice(new TimeAdvice());
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();
        /**
         * 23:24:55.417 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - targetClass=class hello.proxy.common.service.ServiceImpl
         * 23:24:55.419 [Test worker] INFO hello.proxy.proxyfactory.ProxyFactoryTest - proxyClass=class hello.proxy.common.service.ServiceImpl$$EnhancerBySpringCGLIB$$b9a8a13c
         * 23:24:55.424 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
         * 23:24:55.436 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
         * 23:24:55.436 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료, resultTime=11
         */

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

}
