package opensource.alzheimerdinger.core.global.metric;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseCaseMetric {
    String domain(); // UseCase 도메인
    String value(); // ex) get-period, save, login...
    String type() default "command"; // query or command
    String group() default "ad.usecase"; // metric group prefix
}