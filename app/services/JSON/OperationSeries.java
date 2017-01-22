//package services.JSON;
//
///**
// * Created by seijihagawa on 2017/01/14.
// */
//
//import com.fasterxml.jackson.annotation.*;
//
//import java.util.ArrayList;
//
///**
// * Json形式で、このクラスを表す
// * {"id":"String", "newPosition":["x int", "y int"]}
// * このオブジェクトのArrayList、
// * ObjectMapper mapper = new ObjectMapper();
// * mapper.writeValue(System.out, このオブジェクトのArrayList);みたいな感じで、
// * トップレベルが配列のjsonにできる
// * <p>
// * このクラスは例外的に、変数名をjsonクラスの対応する要素と同じ名前にする
// */
//public class OperationSeries {
//    private String id;
//    //ここってリストにしなくて大丈夫なんだろうか
//    private ArrayList<Integer> newPosition = new ArrayList<>();
//
//    public OperationSeries() {
//    }
//
//    @JsonSetter("id")
//    public void setID(String aID) {
//        id = aID;
//    }
//
//    @JsonSetter("newPosition")
//    public void setNewPosition(int[] aNewPosition) {
//        newPosition[0] = aNewPosition[0];
//        newPosition[1] = aNewPosition[1];
//    }
//
//    @JsonGetter("id")
//    public String getID() {
//        return id;
//    }
//
//    @JsonGetter("newPosition")
//    public int[] getNewPosition() {
//        return newPosition;
//    }
//
//
//}
