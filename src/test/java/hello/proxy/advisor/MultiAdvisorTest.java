package hello.proxy.advisor;

import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class MultiAdvisorTest {

    @Test
    @DisplayName("여러 프록시")
    void multiAdvisorTest1() {
        //client -> proxy2(advisor2) -> proxy1(advisor1) -> target

        //프록시 1 생성
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();

        //프록시 2 생성
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

        //실행
        proxy2.save();
        /**
         * 22:44:38.025 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice2 - advice2 호출
         * 22:44:38.030 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice1 - advice1 호출
         * 22:44:38.030 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
         */
    }

    /**
     * 스프링은 AOP를 적용할 때 최적화를 진행해서 지금처럼 프록시는 하나만 만들고 하나의 프록시에 여러 어드바이저를 적용한다. <br>
     * 즉, `target` 마다의 프록시가 생성되는 것이고, 해당 프록시에 여러 어드바이저가 적용되게 된다.
     */
    @Test
    @DisplayName("하나의 프록시, 여러 어드바이저")
    void multiAdvisorTest2() {
        //client -> proxy -> advisor2 -> advisor1 -> target

        //advisor 생성
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());

        //프록시 생성
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);

        proxyFactory1.addAdvisor(advisor2); // 먼저 호출할것을 먼저 넣어줌
        proxyFactory1.addAdvisor(advisor1);

        ServiceInterface proxy = (ServiceInterface) proxyFactory1.getProxy();

        //실행
        proxy.save();
        /**
         * 22:48:39.957 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice2 - advice2 호출
         * 22:48:39.961 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice1 - advice1 호출
         * 22:48:39.962 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
         */
    }

    @Slf4j
    static class Advice1 implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice1 호출");
            return invocation.proceed();
        }
    }

    @Slf4j
    static class Advice2 implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice2 호출");
            return invocation.proceed();
        }
    }
}
