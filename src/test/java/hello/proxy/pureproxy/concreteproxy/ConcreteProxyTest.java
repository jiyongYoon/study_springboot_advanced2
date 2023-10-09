package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import org.junit.jupiter.api.Test;

public class ConcreteProxyTest {

    @Test
    void noProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        ConcreteClient client = new ConcreteClient(concreteLogic);
        client.execute();
    }

    @Test
    void addProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic(); // 핵심 로직을 가진 객체
        TimeProxy timeProxy = new TimeProxy(concreteLogic); // 프록시 객체가 핵심 로직을 가진 객체를 주입받아서 사용 (프록시 객체는 핵심 로직을 가진 객체를 상속한 객체)
        ConcreteClient client = new ConcreteClient(timeProxy); // 클라이언트는 프록시 객체를 주입받아서 사용
        client.execute();
    }
}
