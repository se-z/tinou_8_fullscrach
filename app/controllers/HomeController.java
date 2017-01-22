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
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.*;

class Response{
	String id;
	ArrayList<Integer> newPosition;
	public Response(){
		
	}
	@JsonSetter("id")
	public void setId(String aId){
		id = aId;
	}
	@JsonSetter("newPosition")
	public void setnewPosition(int aX,int aY){
		newPosition = new ArrayList<Integer>();
		newPosition.add(aX);
		newPosition.add(aY);
	}
	
	public String getId(){
		return(id);
	}
	
	public ArrayList<Integer> getnewPosition(){
		return(newPosition);
	}
}

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
		ArrayList<Response> resList = new ArrayList<Response>();
		Response res1 = new Response();
		res1.setId("A");res1.setnewPosition(3,0);
		resList.add(res1);
		//System.out.println("List="+resList);
		JsonNode resJson = Json.toJson(resList);
		//System.out.println("Node="+resJson);
		String resJ = Json.stringify(resJson);
		//System.out.println("Res="+resJ);
        //ObjectMapper mapper = new ObjectMapper();
		//FieldData fd = Json.fromJson(json,FieldData.class);
		//System.out.println("fd="+fd);
        //Request tRequest = Json.fromJson(json, Request.class);
		//System.out.println("tRequest="+tRequest);
        //System.out.println(tRequest);

        return ok(resJ);
    }


}


