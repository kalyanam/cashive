package com.cashive.handler;

import com.cashive.dao.AccountsDAO;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by mkalyan on 8/27/16.
 */
public class ValidateAccount {
    private static final Logger logger = LoggerFactory.getLogger(ValidateAccount.class);
    private AccountsDAO accountsDAO;

    public ValidateAccount(AccountsDAO accountsDAO) {
        this.accountsDAO = accountsDAO;
    }

    public void handle(RoutingContext routingContext) {
        String body = routingContext.getBodyAsJson().encodePrettily();
        logger.info("Body is: {}", body);
        JsonObject jsonObject = new JsonObject(body);

        boolean isValid = accountsDAO.validateAccount(jsonObject.getString("email"), jsonObject.getString("password"));

        if(isValid) {
            logger.info("User is valid: {}", jsonObject.getString("email"));
            routingContext.response().setStatusCode(HttpStatus.OK_200).end("{ \"status\": \"Success\" }");
        } else {
            logger.error("User is invalid: {}", jsonObject.getString("email"));
            routingContext.fail(HttpStatus.UNAUTHORIZED_401);
        }

    }
}
