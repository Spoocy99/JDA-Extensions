package dev.spoocy.jdaextensions.commands.annotations;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissions {

    Permission[] value();

    Scope scope() default Scope.CHANNEL;

    enum Scope {
        CHANNEL,
        GUILD
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Default {
        Permission[] value() default {};

        boolean disable() default false;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Owner { }

}
