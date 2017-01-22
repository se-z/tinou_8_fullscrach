package services.JSON;

/**
 * Created by seijihagawa on 2017/01/14.
 */

import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;

/**
 * Json形式で、このクラスを表す
 * {"id":"String", "newPosition":["x int", "y int"]}
 * このオブジェクトのArrayList、
 * ObjectMapper mapper = new ObjectMapper();
 * mapper.writeValue(System.out, このオブジェクトのArrayList);みたいな感じで、
 * トップレベルが配列のjsonにできる
 * <p>
 * このクラスは例外的に、変数名をjsonクラスの対応する要素と同じ名前にする
 */
public class OperationSeries {
    private String id;
    private ArrayList<Integer> newPosition;

    public OperationSeries() {

    }

    @JsonSetter("id")
    public void setId(String aId) {
        id = aId;
    }

    @JsonSetter("newPosition")
    public void setnewPosition(int aX, int aY) {
        newPosition = new ArrayList<Integer>();
        newPosition.add(aX);
        newPosition.add(aY);
    }

    public String getId() {
        return (id);
    }

    public ArrayList<Integer> getnewPosition() {
        return (newPosition);
    }

}


//class Response {
//    String id;
//    ArrayList<Integer> newPosition;
//
//    public Response() {
//
//    }
//
//    @JsonSetter("id")
//    public void setId(String aId) {
//        id = aId;
//    }
//
//    @JsonSetter("newPosition")
//    public void setnewPosition(int aX, int aY) {
//        newPosition = new ArrayList<Integer>();
//        newPosition.add(aX);
//        newPosition.add(aY);
//    }
//
//    public String getId() {
//        return (id);
//    }
//
//    public ArrayList<Integer> getnewPosition() {
//        return (newPosition);
//    }
//}
