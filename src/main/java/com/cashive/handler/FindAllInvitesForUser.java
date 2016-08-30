package com.cashive.handler;

import com.cashive.dao.InvitesDAO;
import com.cashive.dto.Invite;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by mkalyan on 8/27/16.
 */
public class FindAllInvitesForUser {
    private static final Logger logger = LoggerFactory.getLogger(FindAllInvitesForUser.class);
    private InvitesDAO invitesDAO;

    public FindAllInvitesForUser(InvitesDAO invitesDAO) {
        this.invitesDAO = invitesDAO;
    }

    public void handle(RoutingContext routingContext) {
        String email = routingContext.request().getParam("email");
        List<Invite> invites = invitesDAO.findAllInvitesForUser(email);
        if (invites == null || invites.size() == 0) {
            routingContext.response().setStatusCode(HttpStatus.OK_200).end(new JsonObject().encode());
            return ;
        }

        routingContext.response().setStatusCode(HttpStatus.OK_200).end(new JsonArray(invites).encode());
    }
}
