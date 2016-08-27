package com.cashive.handler;

import com.cashive.dao.AccountsDAO;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mkalyan on 8/26/16.
 */
public class CreateNewAccount {
    private static final Logger logger = LoggerFactory.getLogger(CreateNewAccount.class);

    private AccountsDAO accountsDAO;

    public CreateNewAccount(AccountsDAO accountsDAO) {
        this.accountsDAO = accountsDAO;
    }

    public void handle(RoutingContext routingContext) {
        String body = routingContext.getBodyAsJson().encodePrettily();
        logger.info("Body is: {}", body);
        JsonObject jsonObject = new JsonObject(body);

        accountsDAO.createNewAccount(jsonObject.getString("email"), jsonObject.getString("password"));

        routingContext.response().setStatusCode(HttpStatus.CREATED_201).end("{ \"status\": \"Success\" }");

    }
}
