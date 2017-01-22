package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import services.JSON.*;
import services.naturalLanguage.*;
import play.mvc.*;
import views.html.*;

import java.io.IOException;
import java.util.ArrayList;

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

//        System.out.println(tRequest.getX());
//        System.out.println(tRequest.getY());
//        System.out.println(tRequest.getBlocks());
//        System.out.println(tRequest.getOrder());
//        ArrayList<String> tOrder = tRequest.getOrder();
//        for (int i = 0; i < tOrder.size(); i++) {
//            System.out.println(tOrder.get(i));
//        }

        return ok();
    }


}


