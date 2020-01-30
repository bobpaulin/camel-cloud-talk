

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.osgi.activator.CamelRoutesActivatorConstants;
import org.osgi.service.component.annotations.Component;

@Component(service=RouteBuilder.class, property = {CamelRoutesActivatorConstants.PRE_START_UP_PROP_NAME + "=true"})
public class CoreRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		getContext().setStreamCaching(true);

        restConfiguration().component("netty-http").port(8080)
            .contextPath("/camel-cloud-talk")
            .bindingMode(RestBindingMode.off);

	}

}
