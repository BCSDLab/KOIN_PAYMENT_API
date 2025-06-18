package in.koreatech.payment.client.exception;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum TossPaymentErrorCode {

    // 정의되지 않은 에러 코드인 경우 해당 에러코드를 내린다.
    UNKNOWN_ERROR("UNKNOWN_ERROR", "서버 에러가 발생했습니다. 관리자에게 문의해주세요.", 500),

    // 결제 승인 API 에러 코드
    // 400
    ALREADY_PROCESSED_PAYMENT("ALREADY_PROCESSED_PAYMENT", "이미 처리된 결제입니다.", 400),
    PROVIDER_ERROR("PROVIDER_ERROR", "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", 400),
    EXCEED_MAX_CARD_INSTALLMENT_PLAN("EXCEED_MAX_CARD_INSTALLMENT_PLAN", "설정 가능한 최대 할부 개월 수를 초과했습니다.", 400),
    INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다.", 400),
    NOT_ALLOWED_POINT_USE("NOT_ALLOWED_POINT_USE", "포인트 사용이 불가한 카드로 카드 포인트 결제에 실패했습니다.", 400),
    INVALID_API_KEY("INVALID_API_KEY", "결제 서버에 오류가 발생했습니다.", 400),
    INVALID_REJECT_CARD("INVALID_REJECT_CARD", "카드 사용이 거절되었습니다. 카드사 문의가 필요합니다.", 400),
    BELOW_MINIMUM_AMOUNT("BELOW_MINIMUM_AMOUNT", "결제금액이 최소 허용 금액보다 적습니다.", 400),
    INVALID_CARD_EXPIRATION("INVALID_CARD_EXPIRATION", "카드 유효기간을 다시 확인해주세요.", 400),
    INVALID_STOPPED_CARD("INVALID_STOPPED_CARD", "정지된 카드입니다.", 400),
    EXCEED_MAX_DAILY_PAYMENT_COUNT("EXCEED_MAX_DAILY_PAYMENT_COUNT", "하루 결제 가능 횟수를 초과했습니다.", 400),
    NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT("NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT", "할부가 지원되지 않는 카드 또는 가맹점입니다.", 400),
    INVALID_CARD_INSTALLMENT_PLAN("INVALID_CARD_INSTALLMENT_PLAN", "할부 개월 정보가 잘못되었습니다.", 400),
    NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN("NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN", "할부가 지원되지 않는 카드입니다.", 400),
    EXCEED_MAX_PAYMENT_AMOUNT("EXCEED_MAX_PAYMENT_AMOUNT", "하루 결제 가능 금액을 초과했습니다.", 400),
    INVALID_AUTHORIZE_AUTH("INVALID_AUTHORIZE_AUTH", "결제 서버에 오류가 발생했습니다.", 400),
    INVALID_CARD_LOST_OR_STOLEN("INVALID_CARD_LOST_OR_STOLEN", "분실 또는 도난 카드입니다.", 400),
    RESTRICTED_TRANSFER_ACCOUNT("RESTRICTED_TRANSFER_ACCOUNT", "계좌는 등록 후 12시간 뒤부터 결제할 수 있습니다.", 400),
    INVALID_CARD_NUMBER("INVALID_CARD_NUMBER", "카드번호를 다시 확인해주세요.", 400),
    INVALID_ACCOUNT_INFO_RE_REGISTER("INVALID_ACCOUNT_INFO_RE_REGISTER", "유효하지 않은 계좌입니다. 계좌 재등록 후 시도해주세요.", 400),
    INVALID_UNREGISTERED_SUBMALL("INVALID_UNREGISTERED_SUBMALL", "결제 서버에 오류가 발생했습니다.", 400),
    INVALID_PASSWORD("INVALID_PASSWORD", "결제 비밀번호가 일치하지 않습니다.", 400),
    INCORRECT_BASIC_AUTH_FORMAT("INCORRECT_BASIC_AUTH_FORMAT", "결제 서버에 오류가 발생했습니다.", 400),

    // 401
    UNAUTHORIZED_KEY("UNAUTHORIZED_KEY", "결제 서버에 오류가 발생했습니다.", 401),

    // 403
    REJECT_ACCOUNT_PAYMENT("REJECT_ACCOUNT_PAYMENT", "잔액부족으로 결제에 실패했습니다.", 403),
    REJECT_CARD_PAYMENT("REJECT_CARD_PAYMENT", "한도초과 혹은 잔액부족으로 결제에 실패했습니다.", 403),
    REJECT_CARD_COMPANY("REJECT_CARD_COMPANY", "결제 승인이 거절되었습니다.", 403),
    FORBIDDEN_REQUEST("FORBIDDEN_REQUEST", "허용되지 않은 요청입니다.", 403),
    REJECT_TOSSPAY_INVALID_ACCOUNT("REJECT_TOSSPAY_INVALID_ACCOUNT", "출금이체 등록이 되어 있지 않아요. 계좌를 다시 등록해 주세요.", 403),
    EXCEED_MAX_AUTH_COUNT("EXCEED_MAX_AUTH_COUNT", "최대 인증 횟수를 초과했습니다. 카드사로 문의해주세요.", 403),
    EXCEED_MAX_ONE_DAY_AMOUNT("EXCEED_MAX_ONE_DAY_AMOUNT", "일일 한도를 초과했습니다.", 403),
    NOT_AVAILABLE_BANK("NOT_AVAILABLE_BANK", "은행 서비스 시간이 아닙니다.", 403),
    FDS_ERROR("FDS_ERROR", "위험 거래가 감지되어 결제가 제한됩니다. 문자 링크로 본인인증 후 이용해주세요.", 403),

    // 404
    NOT_FOUND_TERMINAL_ID("NOT_FOUND_TERMINAL_ID", "단말기 정보가 없습니다.", 404),
    NOT_FOUND_PAYMENT("NOT_FOUND_PAYMENT", "존재하지 않는 결제 정보입니다.", 404),
    NOT_FOUND_PAYMENT_SESSION("NOT_FOUND_PAYMENT_SESSION", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.", 404),
    NOT_REGISTERED_BUSINESS("NOT_REGISTERED_BUSINESS", "등록되지 않은 사업자 번호입니다.", 404),

    // 500
    CARD_PROCESSING_ERROR("CARD_PROCESSING_ERROR", "카드사에서 오류가 발생했습니다.", 500),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING", "결제가 완료되지 않았어요. 다시 시도해주세요.", 500),
    FAILED_INTERNAL_SYSTEM_PROCESSING("FAILED_INTERNAL_SYSTEM_PROCESSING", "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.", 500),
    UNKNOWN_PAYMENT_ERROR("UNKNOWN_PAYMENT_ERROR", "결제에 실패했어요. 반복되면 카드사로 문의해주세요.", 500);

    private final String code;
    private final String message;
    private final int statusCode;

    TossPaymentErrorCode(String code, String message, int statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private static final Map<String, TossPaymentErrorCode> ERROR_CODE_MAP =
        Stream.of(values()).collect(Collectors.toMap(TossPaymentErrorCode::getCode, e -> e));

    public static TossPaymentErrorCode fromCode(String code) {
        return ERROR_CODE_MAP.getOrDefault(code, UNKNOWN_ERROR);
    }
}
