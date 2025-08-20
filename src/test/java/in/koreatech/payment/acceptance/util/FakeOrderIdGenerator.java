package in.koreatech.payment.acceptance.util;

import in.koreatech.payment.util.OrderIdGenerator;

public class FakeOrderIdGenerator implements OrderIdGenerator {
    @Override
    public String generateOrderId() {
        return "a4CWyWY5m89PNh7xJwhk1";
    }
}
