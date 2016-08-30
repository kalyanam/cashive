package com.cashive.handler;

import com.cashive.dao.ActiveGroupInfoDAO;
import com.cashive.dto.ActiveGroupInfo;
import com.cashive.dto.Invite;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by mkalyan on 8/27/16.
 */
public class FindActiveGroupsForUser {
    private static final Logger logger = LoggerFactory.getLogger(FindActiveGroupsForUser.class);
    private ActiveGroupInfoDAO activeGroupInfoDAO;

    public FindActiveGroupsForUser(ActiveGroupInfoDAO activeGroupInfoDAO) {
        this.activeGroupInfoDAO = activeGroupInfoDAO;
    }

    public void handle(RoutingContext routingContext) {
        String email = routingContext.request().getParam("email");
        List<ActiveGroupInfo> activeGroups = activeGroupInfoDAO.findActiveGroupsForUser(email);
        if (activeGroups == null || activeGroups.size() == 0) {
            routingContext.response().setStatusCode(HttpStatus.OK_200).end(new JsonObject().encode());
            return ;
        }

        routingContext.response().setStatusCode(HttpStatus.OK_200).end(new JsonArray(activeGroups).encode());
    }
}
