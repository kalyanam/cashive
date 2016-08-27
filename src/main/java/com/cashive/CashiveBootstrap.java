package com.cashive;

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
 */
public class CashiveBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(CashiveBootstrap.class);

    public void start() {
        doStartVertx();
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

        router.get().path("/ping").handler(this::handlePing);
        logger.info("Starting vertx on port: {}", port);
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
    }

    private void handlePing(RoutingContext routingContext) {
        routingContext.response().setStatusCode(HttpStatus.OK_200).end(new JsonObject().put("status","OK").encode());
    }

    public static void main(String[] args) {
        CashiveBootstrap bootstrap = new CashiveBootstrap();
        bootstrap.start();
    }
}
