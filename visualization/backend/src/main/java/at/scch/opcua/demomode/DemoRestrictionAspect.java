package at.scch.opcua.demomode;

import at.scch.opcua.config.NodeDocConfiguration;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class DemoRestrictionAspect {

    private final NodeDocConfiguration config;

    @Around("@annotation(at.scch.opcua.demomode.RestrictInDemoMode)")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        if (config.isDemo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Demo restriction");
        } else {
            return pjp.proceed();
        }
    }
}
