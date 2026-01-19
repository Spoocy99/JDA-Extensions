package dev.spoocy.jdaextensions.commands.manager.impl

import dev.spoocy.jdaextensions.commands.annotations.Command
import dev.spoocy.utils.reflection.Reflection
import dev.spoocy.utils.reflection.accessor.MethodAccessor

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class KotlinAnnotationProcessor : StaticAnnotationProcessor() {
    override fun getExecutorInstance(clazz: Class<*>): Any? {
        return try {
            val instanceField = clazz.getField("INSTANCE")
            instanceField.get(null)
        } catch (_: NoSuchFieldException) {
            null
        }
    }

    public override fun getDefaultMethod(clazz: Class<*>): MethodAccessor? {
        val access = Reflection.builder()
                .forClass(clazz)
                .privateMembers()
                .buildAccess()

        // static method (for @JvmStatic or Java-style)
        val staticMethod = access.method(Reflection.method()
                .requireStatic()
                .requireAnnotation(Command.Default::class.java)
                .build()
        )
        if (staticMethod != null) return staticMethod

        // If this is a Kotlin `object`, there will be an `INSTANCE` field and the methods are instance methods
        return try {
            clazz.getField("INSTANCE")
            access.method(Reflection.method()
                    .requireAnnotation(Command.Default::class.java)
                    .build()
            )
        } catch (_: NoSuchFieldException) {
            null
        }
    }

    public override fun getSubCommandMethods(clazz: Class<*>): Set<MethodAccessor> {
        val access = Reflection.builder()
                .forClass(clazz)
                .privateMembers()
                .buildAccess()

        // First try static methods
        val staticMethods = access.methods(Reflection.method()
                .requireStatic()
                .requireAnnotation(Command.Sub::class.java)
                .build()
        )
        if (staticMethods != null && staticMethods.isNotEmpty()) return staticMethods

        // Fallback to instance methods on Kotlin object singletons (have INSTANCE)
        return try {
            clazz.getField("INSTANCE")
            val instanceMethods = access.methods(Reflection.method()
                    .requireAnnotation(Command.Sub::class.java)
                    .build()
            )
            instanceMethods ?: emptySet()
        } catch (_: NoSuchFieldException) {
            emptySet()
        }
    }


}