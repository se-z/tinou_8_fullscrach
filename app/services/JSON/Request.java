package services.JSON;

/**
 * Created by seijihagawa on 2017/01/22.
 */

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;

/**{
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
    private int[] X = new int[2];
    private int[] Y = new int[2];
    private ArrayList<block> blocks;
    private ArrayList<String> order;

    public Request() {
        blocks = new ArrayList<block>();
        order = new ArrayList<String>();
    }
    //blocksっていう配列に、オブジェクトをもたせている

    public class block {
        private String id;
        private String shape;
        private int[] coordinate = new int[2];
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
        public void setCoordinate(int[] aCoordinate) {
            coordinate[0] = aCoordinate[0];
            coordinate[1] = aCoordinate[1];
        }

        @JsonSetter("heavy")
        public void setHeavy(boolean aHeavy) {
            heavy = aHeavy;
        }

        @JsonSetter("color")
        public void setColor(String aColor) {
            color = aColor;
        }

    }


    @JsonSetter("X")
    public void setX(int[] aX) {
        X = aX;
    }

    @JsonSetter("Y")
    public void setY(int[] aY) {
        Y = aY;
    }

    @JsonSetter("blocks")
    public void setBlocks(ArrayList<block> aBlock) {
        blocks = aBlock;

    }

    @JsonSetter("color")
    public void setOrder(ArrayList<String> aOrder) {
        order = aOrder;
    }
}
