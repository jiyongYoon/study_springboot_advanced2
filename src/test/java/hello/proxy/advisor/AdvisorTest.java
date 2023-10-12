package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

@Slf4j
public class AdvisorTest {

    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor =
            new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
        /**
         * 22:07:30.282 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
         * 22:07:30.288 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
         * 22:07:30.288 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료, resultTime=0
         * 22:07:30.291 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
         * 22:07:30.291 [Test worker] INFO hello.proxy.common.service.ServiceImpl - find 호출
         * 22:07:30.292 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료, resultTime=1
         */
    }

    @Test
    @DisplayName("직접 만든 포인트컷")
    void advisorTest2() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor =
            new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
        /**
         * 22:36:05.114 [Test worker] INFO hello.proxy.advisor.AdvisorTest - 포인트컷 호출, method=save, targetClass=class hello.proxy.common.service.ServiceImpl
         * 22:36:05.118 [Test worker] INFO hello.proxy.advisor.AdvisorTest - 포인트컷 결과, result=true
         * 22:36:05.121 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
         * 22:36:05.121 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
         * 22:36:05.123 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료, resultTime=0
         * 22:36:05.123 [Test worker] INFO hello.proxy.advisor.AdvisorTest - 포인트컷 호출, method=find, targetClass=class hello.proxy.common.service.ServiceImpl
         * 22:36:05.123 [Test worker] INFO hello.proxy.advisor.AdvisorTest - 포인트컷 결과, result=false
         * 22:36:05.123 [Test worker] INFO hello.proxy.common.service.ServiceImpl - find 호출
         */
    }

    @Test
    @DisplayName("스프링이 제공하는 포인트컷")
    void advisorTest3() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("save");
        DefaultPointcutAdvisor advisor =
            new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
        /**
         * 22:38:23.321 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 실행
         * 22:38:23.325 [Test worker] INFO hello.proxy.common.service.ServiceImpl - save 호출
         * 22:38:23.325 [Test worker] INFO hello.proxy.common.advice.TimeAdvice - TimeProxy 종료, resultTime=0
         * 22:38:23.327 [Test worker] INFO hello.proxy.common.service.ServiceImpl - find 호출
         */
    }

    /**
     * 포인트컷은 실습차 한번 구현해보지만, 실무에서는 스프링이 제공하는 더욱 추상화된 aspectJ 표현식을 사용하는 Pointcut을 사용하게 될 것.
     */
    static class MyPointcut implements Pointcut {

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    static class MyMethodMatcher implements MethodMatcher {

        // 어떤 메서드명에 적용할 것인지
        private String matchName = "save";

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            boolean result = method.getName().equals(matchName);
            log.info("포인트컷 호출, method={}, targetClass={}", method.getName(), targetClass);
            log.info("포인트컷 결과, result={}", result);
            return result;
        }

        @Override
        public boolean isRuntime() {
            return false;
            // 만약 return true면 아래 matches 메서드가 호출됨. 그렇게 중요한 부분은 아님..
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }
}
