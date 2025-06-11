package in.koreatech.payment.common.auth;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface AccessToken {

}
