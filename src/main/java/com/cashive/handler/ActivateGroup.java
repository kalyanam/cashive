package com.cashive.handler;

import com.cashive.dao.ActiveGroupInfoDAO;
import com.cashive.dao.GroupsDAO;
import com.cashive.dto.Group;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mkalyan on 8/27/16.
 */
public class ActivateGroup {
    private static final Logger logger = LoggerFactory.getLogger(ActivateGroup.class);
    private GroupsDAO groupsDAO;
    private ActiveGroupInfoDAO activeGroupInfoDAO;

    public ActivateGroup(GroupsDAO groupsDAO, ActiveGroupInfoDAO activeGroupInfoDAO) {
        this.groupsDAO = groupsDAO;
        this.activeGroupInfoDAO = activeGroupInfoDAO;
    }

    public void handle(RoutingContext routingContext) {
        String body = routingContext.getBodyAsJson().encodePrettily();
        logger.info("Body is: {}", body);
        JsonObject jsonObject = new JsonObject(body);

        Integer groupId = jsonObject.getInteger("groupId");
        Group group = groupsDAO.findGroupById(groupId);
        activeGroupInfoDAO.activateGroup(groupId, group.getMaxParticipants());

        //TODO: Also invalidate all the pending invites

        routingContext.response().setStatusCode(HttpStatus.CREATED_201).end("{ \"status\": \"Success\" }");
    }
}
