package org.reflections;

import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


class TestClass{
    private void m1(String testParam1, String testParam2) {
        String testLocal = "local";
    }

    static public void m2(int testParam1, String testParam2) {
        String testLocal = "local";
    }
}


public class TestIssue256 {
    static Reflections reflections;

    @BeforeClass
    public static void init() {
        reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(Collections.singletonList(ClasspathHelper.forClass(TestIssue256.class)))
                .setScanners(
                        new MethodParameterNamesScanner()));
    }

    @Test
    public void testGetMethodParamNamesPrivate() throws NoSuchMethodException{
        Method method = TestClass.class.getDeclaredMethod("m1", String.class, String.class);

        List<String> expected = new ArrayList<>();
        expected.add("testParam1");
        expected.add("testParam2");

        List<String> found = reflections.getMethodParamNames(method);
        assertEquals(expected, found);
    }

    @Test
    public void testGetMethodParamNamesStaticPublic() throws NoSuchMethodException{
        Method method = TestClass.class.getDeclaredMethod("m2", int.class, String.class);

        List<String> expected = new ArrayList<>();
        expected.add("testParam1");
        expected.add("testParam2");

        List<String> found = reflections.getMethodParamNames(method);
        assertEquals(expected, found);
    }
}
