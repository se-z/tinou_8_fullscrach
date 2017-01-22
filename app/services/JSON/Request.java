package services.JSON;

/**
 * Created by seijihagawa on 2017/01/22.
 */

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;

/**
 * {
 * "X": ["int min", "int max"],
 * "Y": ["int min", "int max"],
 * "blocks": [
 * {"id": "String", "shape": "String", "coodinate": [ "int", "int" ], "heavy": "boolean", "color": ""},
 * {},
 * {}
 * ],
 * "order": [ "String", "String", "String", ...]
 * }
 */
public class Request {
    private ArrayList<Integer> X = new ArrayList<>();
    private ArrayList<Integer> Y = new ArrayList<>();
    private ArrayList<block> blocks;
    private ArrayList<String> order;

    public Request() {
    }
    //blocksっていう配列に、オブジェクトをもたせている

    public static class block {
        private String id;
        private String shape;
        private ArrayList<Integer> coordinate = new ArrayList<>();
        private boolean heavy;
        private String color;

        public block() {

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


    @JsonSetter("X")
    public void setX(ArrayList<Integer> aX) {
        X = aX;
    }

    @JsonSetter("Y")
    public void setY(ArrayList<Integer> aY) {
        Y = aY;
    }

    @JsonSetter("blocks")
    public void setBlocks(ArrayList<block> aBlock) {
        blocks = aBlock;
    }

    @JsonSetter("order")
    public void setOrder(ArrayList<String> aOrder) {
        order = aOrder;
    }

    public ArrayList<Integer> getX() {
        return X;
    }

    public ArrayList<Integer> getY() {
        return Y;
    }

    public ArrayList<block> getBlocks() {
        return blocks;
    }

    public ArrayList<String> getOrder() {
        return order;
    }
}
