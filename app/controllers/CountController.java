package controllers;

import play.*;
import play.mvc.*;

import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import views.html.*;
//import services.Counter;


public class CountController extends Controller {

//    private final Counter counter;
//
//    @Inject
//    public CountController(Counter counter) {
//        this.counter = counter;
//    }
//
//    /**
//     * An action that responds with the {@link Counter}'s current
//     * count. The result is plain text. This action is mapped to
//     * <code>GET</code> requests with a path of <code>/count</code>
//     * requests by an entry in the <code>routes</code> config file.
//     */
////    public Result count() {
////        return ok(Integer.toString(counter.nextCount()));
////    }

    public Result run() {
        System.out.println("############  run!!  ############");
        System.out.println(request().body().asJson());
        return ok((Content) badRequest("requesuがきました"));
    }

}
