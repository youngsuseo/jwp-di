package core.di.factory;

import core.annotation.ComponentScan;

import java.util.Map;

public class ApplicationContext {

    private final Class<?> baseConfigurationClass;
    private ConfigurationBeanScanner configurationBeanScanner;
    private ClasspathBeanScanner classpathBeanScanner;

    public ApplicationContext(Class<?> clazz) {
        this.baseConfigurationClass = clazz;
        doScan();
    }

    public void doScan() {
        ComponentScan componentScan = baseConfigurationClass.getAnnotation(ComponentScan.class);
        if (componentScan == null) {
            return;
        }

        String[] basePackages = componentScan.basePackages();

        BeanFactory beanFactory = new BeanFactory();
        configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);
        configurationBeanScanner.register(baseConfigurationClass);
        beanFactory.initialize();

        classpathBeanScanner = new ClasspathBeanScanner(beanFactory, (Object[]) basePackages);
    }

    public Map<Class<?>, Object> getFactoryController() {
        return classpathBeanScanner.getFactoryController();
    }
}
