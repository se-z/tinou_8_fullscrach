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
 *
 * block -> block_reqに変更
 */
public class Request {
    private ArrayList<Integer> X = new ArrayList<>();
    private ArrayList<Integer> Y = new ArrayList<>();
    private ArrayList<block_req> blocks;
    private ArrayList<String> order;

    public Request() {
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
    public void setBlocks(ArrayList<block_req> aBlock) {
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

    public ArrayList<block_req> getBlocks() {
        return blocks;
    }

    public ArrayList<String> getOrder() {
        return order;
    }
}
