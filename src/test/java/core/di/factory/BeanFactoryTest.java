package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private Reflections reflections;
    private BeanFactory beanFactory;

    // preInstanticateBean을 생성할 수 있는 생성자를 찾는다. (@Inject 애노테이션이 설정되어있는 생성자) -> BeanFactoryUtils.getInjectedConstructor
    // 생성자의 파라미터를 찾는다.
    // 찾은 파라미터 안에 @Inject 애노테이션이 붙은 생성자가 있는지 찾는다.
    // 없다면 해당 파라미터의 생성자 인스턴스를 생성하고,
    // 있다면 그 하위에 파라미터도 같은 동작을 한다.
    // 하위의 파라미터의 생성이 완료되었다면, 상위의 @Inject 애노테이션이 붙은 생성자에 파라미터를 전달해 인스턴스를 생성한다.

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("core.di.factory.example");
        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory = new BeanFactory(preInstanticateClazz);
        try {
            beanFactory.initialize();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void di() throws Exception {
        // FIXME bean을 가져오는 과정에서 이미 객체는 주입이 완료되어있어야 한다.
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
