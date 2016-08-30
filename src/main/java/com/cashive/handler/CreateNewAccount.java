package com.cashive.handler;

import com.cashive.dao.AccountsDAO;
import com.cashive.exceptions.CashiveException;
import com.cashive.exceptions.ErrorCode;
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

        try {
            String email = jsonObject.getString("email");
            String password = jsonObject.getString("password");

            if(email == null || "".equals(email.trim())) {
                throw new CashiveException("Email cannot be empty", ErrorCode.EMAIL_CANNOT_BE_EMPTY);
            }

            if(password == null || "".equals(password.trim())) {
                throw new CashiveException("Password cannot be empty", ErrorCode.PASSWORD_CANNOT_BE_EMPTY);
            }

            accountsDAO.createNewAccount(email, password);
        } catch (CashiveException ce) {
            ce.printStackTrace();
            if(ce.getErrorCode() == ErrorCode.ACCOUNT_WITH_EMAIL_EXISTS ||
                    ce.getErrorCode() == ErrorCode.EMAIL_CANNOT_BE_EMPTY ||
                    ce.getErrorCode() == ErrorCode.PASSWORD_CANNOT_BE_EMPTY) {

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
