package services;

/**
 * Created by seijihagawa on 2017/01/14.
 */

import  play.api.libs.json.*;
import com.fasterxml.jackson.annotation.*;

/**
 * Json形式で、このクラスを表す
 * {"id":"String", "newPosition":["x int", "y int"]}
 */
public class OperationSeries {
    private String mID;
    private int[] mNewPosition = new int[2];

    OperationSeries(){}

    @JsonSetter("id")
    public void setID(String aID){
        mID = aID;
    }

    //これでいいのか？
    @JsonSetter("newPosition")
    public void setNewPosition(int[] aNewPosition){
        mNewPosition[0] = aNewPosition[0];
        mNewPosition[1] = aNewPosition[1];
    }

    @JsonGetter("id")
    public String getID(){
        return mID;
    }

    @JsonGetter("newPosition")
    public int[] getNewPosition(){
        return mNewPosition;
    }


}
