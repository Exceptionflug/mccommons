package de.exceptionflug.mccommons.core.test;

import de.exceptionflug.mccommons.core.Converters;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.UUID;

public class ConverterTest {

    @Test
    public void run() {
        Converters.register(A.class, B.class, src -> new B());
        Converters.register(B.class, C.class, src -> new C());
        Converters.register(C.class, D.class, src -> new D());

        // Convert A -> D
        Assert.assertEquals(Converters.convert(new A(), D.class).getClass(), D.class);
    }

    private class A {}
    private class B {}
    private class C {}
    private class D {}

}
