package com.cashive.handler;

import com.cashive.dao.GroupsDAO;
import com.cashive.exceptions.CashiveException;
import com.cashive.exceptions.ErrorCode;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mkalyan on 8/27/16.
 */
public class CreateNewGroup {
    private static final Logger logger = LoggerFactory.getLogger(CreateNewGroup.class);
    private GroupsDAO groupsDAO;

    public CreateNewGroup(GroupsDAO groupsDAO) {
        this.groupsDAO = groupsDAO;
    }

    public void handle(RoutingContext routingContext) {
        String body = routingContext.getBodyAsJson().encodePrettily();
        logger.info("Body is: {}", body);
        JsonObject jsonObject = new JsonObject(body);
        //TODO: Check for nulls and such before creating

        try {
            String groupName = jsonObject.getString("groupName");
            String email = jsonObject.getString("email");
            Long startDateMillis = jsonObject.getLong("startDate");
            Integer termLength = jsonObject.getInteger("termLength");
            String termFrequency = jsonObject.getString("termFrequency");
            Integer termAmount = jsonObject.getInteger("termAmount");
            Integer maxParticipants = jsonObject.getInteger("maxParticipants");

            groupsDAO.createNewGroup(groupName, email, startDateMillis, termLength, termFrequency, termAmount, maxParticipants);
        } catch (CashiveException ce) {
            ce.printStackTrace();
            if(ce.getErrorCode() == ErrorCode.NO_ACCOUNT_EXISTS_WITH_EMAIL) {
                routingContext.response().setStatusCode(HttpStatus.BAD_REQUEST_400).end(new JsonObject().put("error", ce.getMessage()).encode());
                return ;
            } else if (ce.getErrorCode() == ErrorCode.GROUP_WITH_NAME_ALREADY_EXISTS) {
                routingContext.response().setStatusCode(HttpStatus.BAD_REQUEST_400).end(new JsonObject().put("error", ce.getMessage()).encode());
                return ;
            } else {
                routingContext.fail(HttpStatus.INTERNAL_SERVER_ERROR_500);
                return ;
            }
        }

        routingContext.response().setStatusCode(HttpStatus.CREATED_201).end("{ \"status\": \"Success\" }");

    }
}
