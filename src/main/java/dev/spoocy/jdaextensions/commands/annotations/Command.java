package dev.spoocy.jdaextensions.commands.annotations;

import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();

    String description();

    boolean nsfw() default false;

    InteractionContextType[] context() default {
            InteractionContextType.GUILD,
            InteractionContextType.BOT_DM,
            InteractionContextType.PRIVATE_CHANNEL
    };

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DisableAcknowledge {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Default {

        boolean async() default false;

        boolean ephemeral() default false;

        boolean sendTyping() default false;

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Sub {

        String name();

        String description();

        boolean async() default false;

        boolean ephemeral() default false;

        boolean sendTyping() default false;

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Group {

        String name();

        String description();

    }

}
