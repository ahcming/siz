package cm.study.vertx.util;

import com.sun.tools.javac.code.Symbol;

public class ClazzUtil {

    public static Class<?> loadClass(Symbol.ClassSymbol classSymbol) {
        try {
            String typeName = classSymbol.fullname.toString();
            System.out.println("#typeName# " + typeName);
            System.out.println("#source# " + classSymbol.sourcefile.getName());
            System.out.println("#class# " + classSymbol.classfile.getName());
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//            return classLoader.loadClass(clazzName);
            return Class.forName(typeName, true, classLoader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
