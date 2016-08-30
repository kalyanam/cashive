package com.cashive.handler;

import com.cashive.dao.GroupsDAO;
import com.cashive.dto.Group;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mkalyan on 8/28/16.
 */
public class FindGroup {
    private static final Logger logger = LoggerFactory.getLogger(FindGroup.class);
    private GroupsDAO groupsDAO;

    public FindGroup(GroupsDAO groupsDAO) {
        this.groupsDAO = groupsDAO;
    }

    public void handle(RoutingContext routingContext) {
        String groupIdParam = routingContext.request().getParam("groupId");
        Integer groupId = Integer.parseInt(groupIdParam);

        Group group = groupsDAO.findGroupById(groupId);
        if(group == null) {
            routingContext.response().setStatusCode(HttpStatus.OK_200).end(new JsonArray().encode());
        } else {
            routingContext.response().setStatusCode(HttpStatus.OK_200).end(Json.encode(group));
        }
    }
}
