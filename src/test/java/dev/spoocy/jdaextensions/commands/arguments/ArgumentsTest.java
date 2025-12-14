package dev.spoocy.jdaextensions.commands.arguments;

import dev.spoocy.jdaextensions.commands.arguments.impl.StringArgument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgumentsTest {

    @Test
    public void factoryMethodsReturnCorrectTypes() {
        StringArgument s = Arguments.string("a", "b", true, false);
        assertNotNull(s);
        assertEquals("a", s.name());
        assertEquals("b", s.description());
        assertTrue(s.required());
    }
}

