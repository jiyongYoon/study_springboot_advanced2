package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.AImpl;
import hello.proxy.jdkdynamic.code.AInterface;
import hello.proxy.jdkdynamic.code.BImpl;
import hello.proxy.jdkdynamic.code.BInterface;
import hello.proxy.jdkdynamic.code.TimeInvocationHandler;
import java.lang.reflect.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class JdkDynamicProxyTest {

    @Test
    void dynamicA() {
        AInterface target = new AImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        // newProxyInstance() 메서드의 파라미터:
        // 1) 프록시가 어디에 생성될지,
        // 2) 어떤 인터페이스를 기반으로 프록시를 만들지(AImpl이 구현한 인터페이스를 넣어주기 때문에, 배열로 넘겨야 될 수도 있음),
        // 3) 프록시가 호출할 로직
//        Object proxy =
//            Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);
        AInterface proxy =
            (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);

        proxy.call(); // handler의 invoke() 호출
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        /**
         * 22:29:47.239 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler - TimeProxy 실행
         * 22:29:47.243 [Test worker] INFO hello.proxy.jdkdynamic.code.AImpl - A 호출
         * 22:29:47.243 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler - TimeProxy 종료, resultTime=0
         * 22:29:47.245 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest - targetClass=class hello.proxy.jdkdynamic.code.AImpl
         * 22:29:47.245 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest - proxyClass=class com.sun.proxy.$Proxy12
         */
    }

    @Test
    void dynamicB() {
        BInterface target = new BImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        BInterface proxy =
            (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);

        proxy.call(); // handler의 invoke() 호출
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }

}
