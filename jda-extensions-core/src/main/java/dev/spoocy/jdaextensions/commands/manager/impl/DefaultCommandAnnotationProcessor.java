package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.annotations.Command;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DefaultCommandAnnotationProcessor extends StaticAnnotationProcessor {

    public static final DefaultCommandAnnotationProcessor INSTANCE = new DefaultCommandAnnotationProcessor();

    @Override
    protected @Nullable Object getExecutorInstance(@NotNull Class<?> clazz) {
        return null;
    }

    @Override
    public @Nullable MethodAccessor getDefaultMethod(@NotNull Class<?> clazz) {
        return Reflection.builder()
                .forClass(clazz)
                .privateMembers()
                .buildAccess()
                .method(Reflection.method()
                        .requireStatic()
                        .requireAnnotation(Command.Default.class)
                        .build()
                );
    }

    @Override
    public @NotNull Set<MethodAccessor> getSubCommandMethods(@NotNull Class<?> clazz) {
        return Reflection.builder()
                .forClass(clazz)
                .privateMembers()
                .buildAccess()
                .methods(Reflection.method()
                        .requireStatic()
                        .requireAnnotation(Command.Sub.class)
                        .build()
                );
    }


}
