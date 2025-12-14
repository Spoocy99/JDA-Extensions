package dev.spoocy.jdaextensions.commands.annotations;

import java.lang.annotation.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Choice {


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Texts.class)
    public @interface Text {

        String argument();

        String name();

        String value();

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Texts {
        Text[] value();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Integer {

        String argument();

        String name();

        int value();

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Integers {
        Integer[] value();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Number {

        String argument();

        String name();

        double value();

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Numbers {
        Number[] value();
    }

}
