package dev.spoocy.jdaextensions.commands.annotations;

import dev.spoocy.jdaextensions.commands.cooldown.CooldownScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cooldown {

    long value();

    TimeUnit unit();

    CooldownScope scope() default CooldownScope.USER;

}
