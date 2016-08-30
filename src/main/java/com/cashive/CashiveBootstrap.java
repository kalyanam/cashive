package com.cashive;

import com.cashive.dao.DAOFactory;
import com.cashive.db.DBMigrator;
import com.cashive.handler.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mkalyan on 8/26/16.
 *
 * https://morning-taiga-45200.herokuapp.com/
 */
public class CashiveBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(CashiveBootstrap.class);

    private DAOFactory factory;

    public void start() {
        doSetupDb();
        doStartVertx();
    }

    private void doSetupDb() {
        new DBMigrator().migrate();
        factory = DAOFactory.newBuilder()
                .withDriverName("org.postgresql.Driver")
                .withDbUrl(System.getenv("JDBC_DATABASE_URL"))
                .build();
    }

    private void doStartVertx() {
        String envPort = System.getenv("PORT");
        Integer port = 8081;

        if(envPort != null) {
            port = Integer.valueOf(envPort);
        }

        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        //http://stackoverflow.com/questions/25727306/request-header-field-access-control-allow-headers-is-not-allowed-by-access-contr
        router.options().handler(routingContext -> {
            logger.info("Options request ");
            routingContext.response().putHeader("Access-Control-Allow-Origin", "*");
            routingContext.response().putHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
            routingContext.response().end();
        });

        router.route().handler(CorsHandler.create("*"));
        router.route().handler(BodyHandler.create());

        router.post().path("/account").handler(this::createAccount);
        router.post().path("/account/validate").handler(this::validateAccount);

        router.post().path("/group").handler(this::createGroup);
        router.get().path("/group/:groupId").handler(this::findGroup);
        router.post().path("/group/activate").handler(this::activateGroup);
        router.get().path("/group/active/:email").handler(this::findActiveGroupsForUser);

        router.post().path("/invite").handler(this::createInvite);
        router.get().path("/invite/:email").handler(this::findAllInvitesForUser);
        router.post().path("/batch/invites").handler(this::createInvites);

        router.get().path("/ping").handler(this::handlePing);

        logger.info("Starting vertx on port: {}", port);
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
    }

    private void handlePing(RoutingContext routingContext) {
        routingContext.response().setStatusCode(HttpStatus.OK_200).end(new JsonObject().put("status","OK").encode());
    }

    private void createAccount(RoutingContext routingContext) {
        new CreateNewAccount(factory.getAccountsDAO()).handle(routingContext);
    }

    private void validateAccount(RoutingContext routingContext) {
        new ValidateAccount(factory.getAccountsDAO()).handle(routingContext);
    }

    private void createGroup(RoutingContext routingContext) {
        new CreateNewGroup(factory.getGroupsDAO()).handle(routingContext);
    }

    private void findGroup(RoutingContext routingContext) {
        new FindGroup(factory.getGroupsDAO()).handle(routingContext);
    }

    private void createInvite(RoutingContext routingContext) {
        new CreateNewInvite(factory.getInvitesDAO()).handle(routingContext);
    }

    private void createInvites(RoutingContext routingContext) {
        new CreateNewInvites(factory.getInvitesDAO()).handle(routingContext);
    }

    private void findAllInvitesForUser(RoutingContext routingContext) {
        new FindAllInvitesForUser(factory.getInvitesDAO()).handle(routingContext);
    }

    private void findActiveGroupsForUser(RoutingContext routingContext) {
        new FindActiveGroupsForUser(factory.getActiveGroupInfoDAO()).handle(routingContext);
    }

    private void activateGroup(RoutingContext routingContext) {
        new ActivateGroup(factory.getGroupsDAO(), factory.getActiveGroupInfoDAO()).handle(routingContext);
    }

    public static void main(String[] args) {
        CashiveBootstrap bootstrap = new CashiveBootstrap();
        bootstrap.start();
    }
}
