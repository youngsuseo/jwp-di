package core.di.factory;

import com.google.common.collect.Maps;
import next.controller.QnaController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        // FIXME 전달받은 class<?> 타입을 기준으로 bean을 생성해서 beans map 에 담는다.
        //  그리고, bean을 생성하면서 주입을 완료한다 -> spring 실행되는 과정에서 이미 객체는 주입 되어있어야 하는 것 같다.¬
        System.out.println("logger = " + logger);
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            // controller를 constructor로 생성한다.

            // 생성자에서 필요한 파라미터를 확인하고 해당 파라미터도 생성하여 등록해둔다.
            Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstanticateBean);

//            Parameter[] parameters = constructor.getParameters();
//            // 파라미터 타입을 확인하고 생성한다.
//            for (Parameter parameter : parameters) {
//
//            }
            // 생성자에 필요한 파라미터 객체 생성
            Parameter[] parameters = injectedConstructor.getParameters();
            for (Parameter parameter : parameters) {
                Class<?> type = parameter.getType();
                Constructor<?> injectedConstructor1 = BeanFactoryUtils.getInjectedConstructor(type);
                System.out.println("injectedConstructor1 = " + injectedConstructor1);

                Parameter[] parameters1 = injectedConstructor1.getParameters();
                for (Parameter parameter1 : parameters1) {
                    Class<?> type1 = parameter1.getType();
                    Constructor<?> injectedConstructor2 = BeanFactoryUtils.getInjectedConstructor(type1);
                    System.out.println("injectedConstructor2 = " + injectedConstructor2);

                    // 하위 객체의 주입받는 것이 없으면 인스턴스를 생성하고,
                    // 생성한 인스턴스로 상위 인스턴스 생성
                    // 같은 로직으로 맨 위 인스턴스 생성하고 beans map에 저장
                }

            }
            Object o;
            try {
                o = injectedConstructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

//
//            Object object;
//            try {
//                object = constructor.newInstance();
//            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//            beans.put(preInstanticateBean, object);
        }
    }
}
