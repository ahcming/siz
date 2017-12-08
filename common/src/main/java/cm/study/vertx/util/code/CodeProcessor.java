package cm.study.vertx.util.code;

import cm.study.vertx.util.ClazzUtil;
import com.sun.tools.javac.code.Symbol;
//import com.sun.tools.javac.code.Symbol;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Set;

@SupportedAnnotationTypes("cm.study.vertx.util.code.CodeGen")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CodeProcessor extends AbstractProcessor {

    private File outputDir;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        System.out.println("----------------------------- init ------------------------------");
        System.out.println("option: " + processingEnv.getOptions());
        String dir = processingEnv.getOptions().get("codegen.output");
        System.out.println("--> dir: " + dir);
        outputDir = new File(dir);
        System.out.println("----------------------------- init ------------------------------");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) { // 处理完了
            return false;
        }

        System.out.println("-----------------------------------------------------------");
        System.out.println(roundEnv.toString());

        for (TypeElement typeElement : annotations) {
            System.out.println("#: " + typeElement.getSimpleName());

            for(Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {

                System.out.println("#type#: " + element.asType());
                System.out.println("#kind#: " + element.getKind());
                System.out.println("#modifiers#: " + element.getModifiers());
                System.out.println("#simple name#: " + element.getSimpleName());
                System.out.println("#encloing elements#: " + element.getEnclosingElement());
                System.out.println("#enclosed elements#: " + element.getEnclosedElements());
                System.out.println("#annotation mirrors#: " + element.getAnnotationMirrors());
                System.out.println("#class#: " + element.getClass());

                if (element.getKind() == ElementKind.CLASS) {
                    Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) element;

                    try {
                        Class<?> clazz = ClazzUtil.loadClass(classSymbol);
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            System.out.println("#field#: " + field.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("-----------------------------------------------------------");

        return false;
    }

}
