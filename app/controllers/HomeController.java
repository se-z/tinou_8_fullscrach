package controllers;

import services.*;

import java.util.ArrayList;

import play.mvc.*;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    public Result index() {

        // activator run -> http://localhost:9000 -> index()が実行される
        // このクラスでテストする

//		Target tgt = new Target();
//        ArrayList<Space> answer = tgt.getTargetList2();
//		String dir = System.getProperty("user.dir");
//		System.out.println("CyrrentDirectry=： " + dir);
//		return ok(index.render(answer.toString()));

        String[] str = {"A", "B", "c"};
        System.out.println("Goals初期化");
        Goals tG = new Goals(str);
        System.out.println("初期化完了");

        return ok(index.render("Your new application is ready."));
    }

}
