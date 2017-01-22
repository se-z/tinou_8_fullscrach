package services.JSON;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;

/**
 * Created by seijihagawa on 2017/01/22.
 */
public class block_req {

    private String id;
    private String shape;
    private ArrayList<Integer> coordinate = new ArrayList<>();
    private boolean heavy;
    private String color;

    public block_req() {

    }

    @JsonSetter("id")
    public void setId(String aID) {
        id = aID;
    }

    @JsonSetter("shape")
    public void setShape(String aShape) {
        shape = aShape;
    }

    @JsonSetter("coordinate")
    public void setCoordinate(ArrayList<Integer> aCoordinate) {
        coordinate = aCoordinate;
    }

    @JsonSetter("heavy")
    public void setHeavy(boolean aHeavy) {
        heavy = aHeavy;
    }

    @JsonSetter("color")
    public void setColor(String aColor) {
        color = aColor;
    }

    public String getId() {
        return id;
    }

    public String getShape() {
        return shape;
    }

    public ArrayList<Integer> getCoordinate() {
        return coordinate;
    }

    public boolean getHeavy() {
        return heavy;
    }

    public String getColor() {
        return color;
    }
}
