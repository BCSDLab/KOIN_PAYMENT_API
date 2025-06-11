package in.koreatech.payment.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class TossPaymentOrderIdGenerator implements OrderIdGenerator{

    private static final String ORDER_ID_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 64;
    private final SecureRandom random = new SecureRandom();

    public String generateOrderId() {
        int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH + 1);
        StringBuilder sb = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            sb.append(ORDER_ID_CHARACTERS.charAt(random.nextInt(ORDER_ID_CHARACTERS.length())));
        }
        return sb.toString();
    }
}
