package core.di.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class BeanScannerTest {

    @DisplayName("configuration 애노테이션이 붙은 클래스를 스캔한다.")
    @Test
    void scan() {
        BeanScanner beanScanner = new BeanScanner();
        beanScanner.scan();
    }


//        for (Class<?> clazz : typesAnnotatedWith) {
//        Method[] declaredMethods = clazz.getDeclaredMethods();
//        for (Method declaredMethod : declaredMethods) {
//            Annotation[] declaredAnnotations = declaredMethod.getDeclaredAnnotations();
//            for (Annotation declaredAnnotation : declaredAnnotations) {
//
//            }
//        }
//    }
}