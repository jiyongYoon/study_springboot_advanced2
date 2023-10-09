package hello.proxy.jdkdynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 리플렉션을 사용하면 클래스와 메서드의 메타 정보를 사용하여 애플리케이션을 동적으로 유연하게 만들 수 있다. <br>
 * 그러나 리플렉션은 런타임에 동작하기 때문에 컴파일 시점에 오류를 잡을 수 없다. <br>
 * 따라서 일반적으로 사용하기 보다는 꼭 필요한 경우에만 사용하도록 한다.
 */
@Slf4j
public class ReflectionTest {

    /**
     * reflection 기능 사용 안함. 일반적인 소스코드
     */
    @Test
    void reflection0() {
        Hello target = new Hello();

        // 공통 로직1 시작
        log.info("start");
        String result1 = target.callA();
        log.info("result={}", result1);
        // 공통 로직1 종료

        // 공통 로직2 시작
        log.info("start");
        String result2 = target.callB();
        log.info("result={}", result2);
        // 공통 로직2 종료
    }

    /**
     * 메서드를 동적으로 호출하는 것이 가능해짐
     */
    @Test
    void reflection1()
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 클래스 정보 획득
        Class<?> classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello"); // $ 는 내부 클래스

        Hello target = new Hello();

        // callA 메서드 정보 획득
        Method methodCallA = classHello.getMethod("callA");
        Object result1 = methodCallA.invoke(target); // target 인스턴스의 methodCallA 메서드를 호출
        log.info("result1={}", result1);

        // callB 메서드 정보 획득
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target); // target 인스턴스의 methodCallB 메서드를 호출
        log.info("result2={}", result2);
    }

    /**
     * reflection1에서 공통로직 추출
     */
    @Test
    void reflection2()
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 클래스 정보 획득
        Class<?> classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello"); // $ 는 내부 클래스

        Hello target = new Hello();

        // callA 메서드 정보 획득
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        // callB 메서드 정보 획득
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }

    private void dynamicCall(Method method, Object target)
        throws InvocationTargetException, IllegalAccessException {
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}", result);
    }

    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }

        public String callB() {
            log.info("callB");
            return "B";
        }
    }
}
