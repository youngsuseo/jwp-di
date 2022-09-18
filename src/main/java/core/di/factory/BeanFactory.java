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
import java.util.*;

public class BeanFactory {
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
            beans.put(preInstanticateBean, instanticate(preInstanticateBean));
        }
    }

    Object instanticate(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // Bean 저장소에 clazz에 해당하는 인스턴스가 이미 존재하면 해당 인스턴스 반환
        if (beans.containsKey(clazz)) {
            return beans.get(clazz);
        }

        // clazz에 @Inject가 설정되어 있는 생성자를 찾는다. BeanFactoryUtils 활용
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        // @Inject로 설정한 생성자가 없으면 Default 생성자로 인스턴스 생성 후 Bean 저장소에 추가 후 반환
        if (injectedConstructor == null) {
            return instantiateClass(clazz);
        }

        // @Inject로 설정한 생성자가 있으면 찾은 생성자를 활용해 인스턴스 생성 후 Bean 저장소에 추가 후 반환
        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            parameters.add(instanticate(parameterType));
        }
        return instantiateConstructor(injectedConstructor, parameters);
    }

    private Object instantiateClass(Class<?> clazz) {
        Class<?> concreteChildClass = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
        return BeanUtils.instantiateClass(concreteChildClass);
    }

    private Object instantiateConstructor(Constructor<?> constructor, List<Object> parameters)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return constructor.newInstance(parameters.toArray());
    }

//
//    public void initialize() throws IllegalAccessException, InvocationTargetException, InstantiationException {
//        for (Class<?> preInstanticateBean : preInstanticateBeans) {
//            // preInstanticateBean을 생성할 수 있는 생성자를 찾는다. (@Inject 애노테이션이 설정되어있는 생성자) -> BeanFactoryUtils.getInjectedConstructor
//            Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstanticateBean);
//            instanticate(preInstanticateBean, injectedConstructor);
//        }
//    }
//
//    private void instanticate(Class<?> preInstanticateBean, Constructor<?> injectedConstructor)
//            throws InstantiationException, IllegalAccessException, InvocationTargetException {
//        if (injectedConstructor == null) {
//            beans.put(preInstanticateBean, getObject(preInstanticateBean));
//            return;
//        }
//
//        // 생성자의 파라미터를 찾는다.
//        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();
//        // 찾은 파라미터 안에 @Inject 애노테이션이 붙은 생성자가 있는지 찾는다.
//        List<Object> params = new ArrayList<>(); // 메서드 안으로 같이 이동
//        for (Class<?> parameterType : parameterTypes) {
//            Constructor<?> parameterConstructor = BeanFactoryUtils.getInjectedConstructor(parameterType);
//
////            extracted(parameterType, parameterConstructor);
//            // 없다면 해당 파라미터의 생성자 인스턴스를 생성하고,
//            if (parameterConstructor == null) {
//                params.add(getObject(parameterType));
//            } else {
//                // 있다면 그 하위에 파라미터도 같은 동작을 한다. (재귀호출)
//                Class<?>[] childClasses = parameterConstructor.getParameterTypes();
//                List<Object> childParams = new ArrayList<>();
//                for (Class<?> childClass : childClasses) {
//                    childParams.add(getObject(childClass));
//                }
//
//                Object o = parameterConstructor.newInstance(childParams.toArray());
//                params.add(o);
//            }
//        }
//        // 하위의 파라미터의 생성이 완료되었다면, 상위의 @Inject 애노테이션이 붙은 생성자에 파라미터를 전달해 인스턴스를 생성한다.
//        Object object = injectedConstructor.newInstance(params.toArray());
//
//        // 생성한 인스턴스를 map에 넣는다.
//        beans.put(preInstanticateBean, object);
//    }
//
//    private Object getObject(Class<?> childClass) {
//        Class<?> concreteChildClass = BeanFactoryUtils.findConcreteClass(childClass, preInstanticateBeans);
//        return BeanUtils.instantiateClass(concreteChildClass);
//    }
}
