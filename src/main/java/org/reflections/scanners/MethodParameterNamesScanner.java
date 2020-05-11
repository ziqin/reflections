package org.reflections.scanners;

import javassist.*;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.reflections.Store;
import org.reflections.adapters.MetadataAdapter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.reflections.util.Utils.join;

/** Scans methods/constructors and indexes parameter names */
@SuppressWarnings("unchecked")
public class MethodParameterNamesScanner extends AbstractScanner {

    /**
     * Scans for the parameter name for given method in class.
     * @param cls the javassist.bytecode.ClassFile of a class
     * @param store store the result inside
     */
    @Override
    public void scan(Object cls, Store store) {
        final MetadataAdapter md = getMetadataAdapter();

        for (Object method : md.getMethods(cls)) {
            String key = md.getMethodFullKey(cls, method);
            if (acceptResult(key)) {

                try {
                    ClassPool pool = ClassPool.getDefault();
                    CtClass cc = pool.makeClass((ClassFile)cls);
                    CtMethod cm = CtMethod.make((MethodInfo) method, cc);

                    MethodInfo methodInfo = cm.getMethodInfo();
                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                    LocalVariableAttribute table = null;
                    if (codeAttribute != null) {
                       table = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                    }

                    if (table != null) {
                        int length = cm.getParameterTypes().length;
                        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
                        List<String> names = new ArrayList<>();
                        for (int i = pos; i < length + pos; i++) {
                            names.add(table.variableName(i));
                        }
                        if (names.size() > 0) {
                            put(store, key, join(names, ", "));
                        }
                    }
                } catch (CannotCompileException | NotFoundException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException ignored){
                }
            }
        }
    }
}
