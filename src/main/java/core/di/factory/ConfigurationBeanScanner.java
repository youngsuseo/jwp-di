package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.*;
import core.annotation.web.Controller;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public class ConfigurationBeanScanner {

    private BeanFactory beanFactory;

    public ConfigurationBeanScanner() {
    }

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> configClass) {
        Set<Class<?>> beans = Sets.newHashSet();

        Method[] declaredMethods = configClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            Bean bean = declaredMethod.getAnnotation(Bean.class);
            if (bean != null) {
                beans.add(declaredMethod.getReturnType());
            }
        }

        // @ComponentScan 애노테이션이 붙어있으면 basePackage 위치를 가져온다.
        ComponentScan annotation = configClass.getAnnotation(ComponentScan.class);
        if (annotation != null) {
            String[] basePackages = annotation.basePackages();
            Reflections reflections2 = new Reflections(basePackages);
            beans.addAll(getTypesAnnotatedWith(reflections2, Controller.class, Service.class, Repository.class));
        }

        beanFactory.setPreInstanticateBeans(beans);
//        return beans;
    }

    public Set<Class<?>> scan() {
        Set<Class<?>> beans = Sets.newHashSet();

        // @Configuration 애노테이션이 붙은 클래스를 가져온다.
        Reflections reflections1 = new Reflections("next");
        Set<Class<?>> typesAnnotatedWith = reflections1.getTypesAnnotatedWith(Configuration.class);

        // 내부 메서드에 @Bean 애노테이션이 붙어있으면 해당 메서드 내부 리턴 값을 BeanFactory에 빈으로 등록한다.
        for (Class<?> clazz : typesAnnotatedWith) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                Bean bean = declaredMethod.getAnnotation(Bean.class);
                if (bean != null) {
                    beans.add(declaredMethod.getReturnType());
                }
            }
        }

        // @ComponentScan 애노테이션이 붙어있으면 basePackage 위치를 가져온다.
        for (Class<?> clazz : typesAnnotatedWith) {
            ComponentScan annotation = clazz.getAnnotation(ComponentScan.class);
            String[] basePackages = annotation.basePackages();
            Reflections reflections2 = new Reflections(basePackages);
            beans.addAll(getTypesAnnotatedWith(reflections2, Controller.class, Service.class, Repository.class));
        }
        return beans;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
