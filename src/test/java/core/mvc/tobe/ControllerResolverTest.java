package core.mvc.tobe;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerResolverTest {

    @DisplayName("HandlerExecution이 정상적으로 추가되었는지 확인한다.")
    @Test
    void addHandlerExecution() {
        ControllerResolver controllerResolver = new ControllerResolver();
        Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();
        controllerResolver.addHandlerExecution(handlerExecutions, new MyController(),
                MyController.class.getDeclaredMethods());
        assertThat(handlerExecutions).hasSize(2);
    }
}