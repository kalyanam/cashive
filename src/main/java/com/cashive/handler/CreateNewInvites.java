package com.cashive.handler;

import com.cashive.dao.InvitesDAO;
import com.cashive.exceptions.CashiveException;
import com.cashive.exceptions.ErrorCode;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mkalyan on 8/28/16.
 */
public class CreateNewInvites {
    private static final Logger logger = LoggerFactory.getLogger(CreateNewInvites.class);
    private InvitesDAO invitesDAO;

    public CreateNewInvites(InvitesDAO invitesDAO) {
        this.invitesDAO = invitesDAO;
    }

    public void handle(RoutingContext routingContext) {
        String body = routingContext.getBodyAsJson().encodePrettily();
        logger.info("Body is: {}", body);
        JsonObject jsonObject = new JsonObject(body);

        Integer groupId = jsonObject.getInteger("groupId");
        String inviterEmail = jsonObject.getString("inviterEmail");
        JsonArray invitedEmails = jsonObject.getJsonArray("invitedEmails");

        try {
            invitesDAO.createNewInvites(groupId, inviterEmail, invitedEmails.getList());
        } catch (CashiveException ce) {
            if(ce.getErrorCode() == ErrorCode.NO_GROUP_WITH_ID) {
                routingContext.response().setStatusCode(HttpStatus.BAD_REQUEST_400).end(new JsonObject().put("error", ce.getMessage()).encode());
                return;
            }
        }

        routingContext.response().setStatusCode(HttpStatus.CREATED_201).end("{ \"status\": \"Success\" }");
    }
}
