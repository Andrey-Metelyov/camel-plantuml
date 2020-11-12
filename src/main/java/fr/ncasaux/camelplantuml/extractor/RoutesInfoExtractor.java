package fr.ncasaux.camelplantuml.extractor;

import fr.ncasaux.camelplantuml.model.ConsumerInfo;
import fr.ncasaux.camelplantuml.model.RouteInfo;
import org.apache.camel.util.URISupport;
import fr.ncasaux.camelplantuml.utils.ConsumerUtils;
import fr.ncasaux.camelplantuml.utils.EndpointUtils;
import fr.ncasaux.camelplantuml.utils.RouteUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RoutesInfoExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesInfoExtractor.class);

    public static void getRoutesInfo(MBeanServer mbeanServer,
                                     HashMap<String, RouteInfo> routesInfo,
                                     ArrayList<ConsumerInfo> consumersInfo) throws Exception {

        Set<ObjectName> routesSet = mbeanServer.queryNames(new ObjectName("org.apache.camel:type=routes,*"), null);
        List<ObjectName> routesList = new ArrayList<>();
        CollectionUtils.addAll(routesList, routesSet);

        for (int index = 0; index < routesList.size(); index++) {
            ObjectName on = routesList.get(index);

            String routeId = (String) mbeanServer.getAttribute(on, "RouteId");

            String endpointUri = (String) mbeanServer.getAttribute(on, "EndpointUri");

            String normalizedUri = URISupport.normalizeUri(endpointUri);
            String endpointBaseUri = URLDecoder.decode(EndpointUtils.getEndpointBaseUri(normalizedUri, LOGGER), "UTF-8");

            String actualDescription = (String) mbeanServer.getAttribute(on, "Description");
            String description = actualDescription != null ? actualDescription : "No description...";

            RouteInfo routeInfo = new RouteInfo(description, "route_".concat(String.valueOf(index)), endpointBaseUri);
            RouteUtils.addRouteInfo(routesInfo, routeId, routeInfo, LOGGER);

            ConsumerInfo consumerInfo = new ConsumerInfo(routeId, endpointBaseUri,"from",false);
            ConsumerUtils.addConsumerInfoIfNotInList(consumersInfo, consumerInfo, LOGGER);
        }
    }
}
