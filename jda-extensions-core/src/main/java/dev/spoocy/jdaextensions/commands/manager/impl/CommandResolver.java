package dev.spoocy.jdaextensions.commands.manager.impl;

import dev.spoocy.jdaextensions.commands.annotations.Command;
import dev.spoocy.jdaextensions.commands.annotations.Permissions;
import dev.spoocy.jdaextensions.commands.structure.impl.CommandData;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandResolver {

    @NotNull
    public static CommandData createData(@NotNull Class<?> clazz) {
        Command parentAnnotation = clazz.getAnnotation(Command.class);
        if (parentAnnotation == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @Command!");
        }

        DefaultMemberPermissions perm = DefaultMemberPermissions.ENABLED;
        Permissions.Default permissionsAnnotation = clazz.getAnnotation(Permissions.Default.class);
        if (permissionsAnnotation != null) {

            if (permissionsAnnotation.disable()) {
                perm = DefaultMemberPermissions.DISABLED;
            } else {
                List<Permission> permissionList = Arrays.asList(permissionsAnnotation.value());
                perm = DefaultMemberPermissions.enabledFor(permissionList);
            }

        }

        return new CommandData(
                parentAnnotation.name(),
                parentAnnotation.description(),
                parentAnnotation.nsfw(),
                perm,
                parentAnnotation.context()
        );
    }

    public static MethodAccessor getRootCommandMethod(@NotNull Class<?> clazz, boolean staticOnly) {
        return Reflection.builder()
                .forClass(clazz)
                .privateMembers()
                .buildAccess()
                .method(staticOnly
                        ? Reflection.method()
                        .requireStatic()
                        .requireAnnotation(Command.Default.class)
                        .build()

                        : Reflection.method()
                        .requireAnnotation(Command.Default.class)
                        .build()
                );

    }

    public static Set<MethodAccessor> getSubCommandMethods(@NotNull Class<?> clazz, boolean staticOnly) {
        return Reflection.builder()
                .forClass(clazz)
                .privateMembers()
                .buildAccess()
                .methods(staticOnly
                        ? Reflection.method()
                        .requireStatic()
                        .requireAnnotation(Command.Sub.class)
                        .build()
                        : Reflection.method()
                        .requireAnnotation(Command.Sub.class)
                        .build()

                );
    }


}
