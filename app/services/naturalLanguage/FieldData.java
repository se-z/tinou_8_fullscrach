package services.naturalLanguage;

import java.util.ArrayList;

/**
 * Created by seijihagawa on 2017/01/22.
 */

/**
 * {
 * "X": ["int min", "int max"],
 * "Y": ["int min", "int max"],
 * "blocks": [
 * {"id": "String", "shape": "String", "coodinate": [ "int", "int" ]},
 * {},
 * {}
 * ],
 * "order": [ "String", "String", "String", ...]
 * }
 */
public class FieldData {
    private int[] mX = new int[2];
    private int[] mY = new int[2];
    private ArrayList<String> mOrder;
    private ArrayList<block_nl> mBlocks;


    public FieldData(int[] aX, int[] aY, ArrayList<String> aOrder, ArrayList<block_nl> aBlocks) {
        mX = aX;
        mY = aY;
        mOrder = aOrder;
        mBlocks = aBlocks;
    }


    public void setX(int[] aX) {
        mX = aX;
    }

    public void setY(int[] aY) {
        mY = aY;
    }

    public void setOrder(ArrayList<String> aOrder) {
        mOrder = aOrder;
    }

    public void setBlocks(ArrayList<block_nl> aBlock) {
        mBlocks = aBlock;

    }
}
