package in.koreatech.payment.common.exception;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class ErrorResponse {

    @JsonIgnore
    private final int status;
    private final String code;
    private final String message;
    private final String errorTraceId;

    public ErrorResponse(int status, String code, String message, String errorTraceId) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.errorTraceId = errorTraceId;
    }
}
