package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.*;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class BeanScanner {
    // scan을 할 때 config 디렉토리 하위의 빈들도 가져온다.
    // @Configuration 으로 설정파일 표시, @Bean으로 등록 빈 표시
    public Set<Class<?>> scan(Object... basePackage) { // basePackage를 하드코딩으로 가져왔는데, 설정파일의 @ComponentScan으로 설정할 수 있도로 한다.
        Set<Class<?>> beans = Sets.newHashSet();

        ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner();
        beans.addAll(configurationBeanScanner.scan());

        // 가져온 데이터를 기준으로
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        beans.addAll(getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class));

        return beans;

        // @Configuration 설정 파일을 통해 등록한 빈과 BeanScanner를 통해 등록한 빈 간에도 DI가 가능해야한다.
        //  -> 위의 로직이 BeanScanner를 통한 등록
        //  -> 추가적으로 Configuration 설정파일을 추가
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
