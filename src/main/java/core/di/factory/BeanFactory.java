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
import java.util.ArrayList;
import java.util.List;
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

    public void initialize() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            // preInstanticateBean을 생성할 수 있는 생성자를 찾는다. (@Inject 애노테이션이 설정되어있는 생성자) -> BeanFactoryUtils.getInjectedConstructor
            Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstanticateBean);

            if (injectedConstructor == null) {
                Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(preInstanticateBean, preInstanticateBeans);
                Constructor<?> constructor = concreteClass.getConstructors()[0];
                Object o = constructor.newInstance();
                beans.put(preInstanticateBean, o);
                continue;
            }


            // 생성자의 파라미터를 찾는다.
            Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
            // 찾은 파라미터 안에 @Inject 애노테이션이 붙은 생성자가 있는지 찾는다.
            List<Object> params = new ArrayList<>(); // 메서드 안으로 같이 이동
            for (Class<?> parameterType : parameterTypes) {
                Constructor<?> parameterConstructor = BeanFactoryUtils.getInjectedConstructor(parameterType);

                // 없다면 해당 파라미터의 생성자 인스턴스를 생성하고,
                if (parameterConstructor == null) {
                    Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(preInstanticateBean, preInstanticateBeans);
                    Constructor<?> constructor = concreteClass.getConstructors()[0];
                    Object o = constructor.newInstance();
                    params.add(o);
                } else {
                    // 있다면 그 하위에 파라미터도 같은 동작을 한다. (재귀호출)
                    Class<?>[] childClasses = parameterConstructor.getParameterTypes();
                    List<Object> childParams = new ArrayList<>();
                    for (Class<?> childClass : childClasses) {
                        Class<?> concreteChildClass = BeanFactoryUtils.findConcreteClass(childClass, preInstanticateBeans);
                        Constructor<?> grandChildConstructor = concreteChildClass.getConstructors()[0];
                        Object newInstance = grandChildConstructor.newInstance();
                        childParams.add(newInstance);
                    }

                    Object o = parameterConstructor.newInstance(childParams.toArray());
                    params.add(o);
                }
            }
            // 하위의 파라미터의 생성이 완료되었다면, 상위의 @Inject 애노테이션이 붙은 생성자에 파라미터를 전달해 인스턴스를 생성한다.
            Object object = injectedConstructor.newInstance(params.toArray());

            // 생성한 인스턴스를 map에 넣는다.
            beans.put(preInstanticateBean, object);
        }
    }
}
