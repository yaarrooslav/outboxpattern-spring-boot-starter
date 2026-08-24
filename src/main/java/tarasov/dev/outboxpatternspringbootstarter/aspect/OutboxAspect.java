package tarasov.dev.outboxpatternspringbootstarter.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import tarasov.dev.outboxpatternspringbootstarter.annotation.Outbox;
import tarasov.dev.outboxpatternspringbootstarter.service.OutboxService;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class OutboxAspect {

    private final ObjectMapper mapper;
    private final OutboxService service;

    @Around("@annotation(outbox)")
    public Object handleOutbox(
            ProceedingJoinPoint joinPoint,
            Outbox outbox
    ) throws Throwable {

        String spellExpression = outbox.payload();

        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();

        String[] paramNames = new DefaultParameterNameDiscoverer().getParameterNames(method);

        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        ExpressionParser parser = new SpelExpressionParser();
        Object target = parser.parseExpression(spellExpression).getValue(context);

        String payload = mapper.writeValueAsString(target);

        service.saveOutboxMessage(outbox.eventType(), payload);

        return joinPoint.proceed();
    }

}
