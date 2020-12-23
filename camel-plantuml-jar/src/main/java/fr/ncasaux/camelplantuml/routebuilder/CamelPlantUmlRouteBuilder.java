package fr.ncasaux.camelplantuml.routebuilder;

import fr.ncasaux.camelplantuml.processor.GetRoutesInfoProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;

public class CamelPlantUmlRouteBuilder extends RouteBuilder {

    private Integer port = 8090;
    private String host = "localhost";

    public CamelPlantUmlRouteBuilder() {
    }

    public CamelPlantUmlRouteBuilder(String host, Integer port) {
        this.port = port;
        this.host = host;
    }

    public void configure() {

        restConfiguration().component("netty-http").host(host).port(port);

        rest("camel-plantuml")
                .get("diagram.puml")
                    .param().name("connectRoutes").type(RestParamType.query).defaultValue("false").endParam()
                    .route().id("camelplantuml-http-trigger")
                    .process(new GetRoutesInfoProcessor())
                .endRest()
        ;
    }
}