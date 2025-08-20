package in.koreatech.payment.acceptance.mock;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import in.koreatech.payment.util.OrderIdGenerator;

@Component
@Primary
class FakeOrderIdGenerator implements OrderIdGenerator {
    @Override
    public String generateOrderId() {
        return "FAKE_ORDER_123";
    }
}
