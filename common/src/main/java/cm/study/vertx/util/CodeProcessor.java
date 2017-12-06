package cm.study.vertx.util;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("cm.study.vertx.util.CodeGen")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CodeProcessor extends AbstractProcessor {

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
                System.out.println("##: " + element.getClass());
            }
        }
        System.out.println("-----------------------------------------------------------");

        return false;
    }

}
