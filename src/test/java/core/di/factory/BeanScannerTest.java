package core.di.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeanScannerTest {

    @DisplayName("Bean 스캔이 정상적으로 되는지 확인한다.")
    @Test
    void scan() {
        BeanScanner beanScanner = new BeanScanner();
        Set<Class<?>> scannedBeans = beanScanner.scan("next");
        assertThat(scannedBeans).hasSize(5);
    }
}