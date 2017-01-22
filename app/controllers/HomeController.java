package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import services.Application;
import services.JSON.*;
import play.mvc.*;
import views.html.*;

import java.util.ArrayList;
import java.io.IOException;

import play.libs.Json;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    public Result index() {
        return ok(index.render("Your new application is ready."));
    }


    public Result run() throws IOException {
        System.out.println("############  run!!  ############");

        JsonNode json = request().body().asJson();
        System.out.println(json);
        Request tRequest = Json.fromJson(json, Request.class);
        Application tApp = new Application(tRequest);
        ArrayList<OperationSeries> tResult = tApp.run();
        JsonNode tResultJson = Json.toJson(tResult);
        String tResultString = Json.stringify(tResultJson);
        return ok(tResultString);

//        System.out.println(tRequest.getX());
//        System.out.println(tRequest.getY());
//        System.out.println(tRequest.getBlocks());
//        System.out.println(tRequest.getOrder());
//        ArrayList<String> tOrder = tRequest.getOrder();
//        for (int i = 0; i < tOrder.size(); i++) {
//            System.out.println(tOrder.get(i));
//        }
//
//
//        System.out.println(json);
//        ArrayList<OperationSeries> resList = new ArrayList<OperationSeries>();
//        OperationSeries res1 = new OperationSeries();
//        res1.setId("A");
//        res1.setnewPosition(3, 0);
//        resList.add(res1);
//        JsonNode resJson = Json.toJson(resList);
//        String resJ = Json.stringify(resJson);
//        return ok(resJ);

    }


}


