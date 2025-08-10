package opensource.alzheimerdinger.core.global.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UseCaseMetricAspect {

    private final MeterRegistry registry;

    @Around(value = "@within(anno) || @annotation(anno)", argNames = "pjp,anno")
    public Object around(ProceedingJoinPoint pjp, UseCaseMetric anno) throws Throwable {
        final String group   = anno.group();        // ad.usecase
        final String domain  = anno.domain();       // e.g. analysis
        final String usecase = anno.value();        // e.g. get-period
        final String type    = anno.type();         // query | command

        log.debug("UseCaseMetric AOP hit: domain={}, usecase={}, type={}", anno.domain(), anno.value(), anno.type());

        // 공통 타이머 (히스토그램 활성화는 yml에서)
        Timer.Builder durationBuilder = Timer.builder(group + ".duration")
                .tag("domain", domain)
                .tag("usecase", usecase)
                .tag("type", type);

        // 호출 카운터 (outcome tag 로 성공/실패 구분)
        Counter.Builder callsBuilder = Counter.builder(group + ".calls")
                .tag("domain", domain)
                .tag("usecase", usecase)
                .tag("type", type);

        Timer.Sample sample = Timer.start(registry);
        try {
            Object result = pjp.proceed();
            sample.stop(durationBuilder.tag("outcome", "success").register(registry));
            callsBuilder.tag("outcome", "success").register(registry).increment();
            return result;
        } catch (Throwable t) {
            // 실패도 같은 타이머에 outcome=fail 로 기록
            sample.stop(durationBuilder
                    .tag("outcome", "fail")
                    .tag("exception", t.getClass().getSimpleName()) // 클래스명 정도는 저카디널리티
                    .register(registry));
            callsBuilder.tag("outcome", "fail").register(registry).increment();
            throw t;
        }
    }
}