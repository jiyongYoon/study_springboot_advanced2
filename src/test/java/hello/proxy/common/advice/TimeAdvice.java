package hello.proxy.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

// MethodInterceptor 는 최초에 Advice Interface를 상속한다.
@Slf4j
public class TimeAdvice implements MethodInterceptor {

    // 프록시 팩토리에서 이미 target을 넣어준다.

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = invocation.proceed();; // 타겟 메서드 실행

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료, resultTime={}", resultTime);

        return result;
    }
}
