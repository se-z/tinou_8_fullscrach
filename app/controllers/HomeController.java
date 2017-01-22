package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import services.JSON.*;
import services.naturalLanguage.*;
import play.mvc.*;
import views.html.*;

import java.io.IOException;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


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
        //String jsonString = json.toString();
        System.out.println(json);
        //ObjectMapper mapper = new ObjectMapper();
		FieldData fd = Json.fromJson(json,FieldData.class);
		System.out.println("fd="+fd);
        Request tRequest = Json.fromJson(json, Request.class);
		System.out.println("tRequest="+tRequest);
        System.out.println(tRequest);

        return ok();
    }


}


