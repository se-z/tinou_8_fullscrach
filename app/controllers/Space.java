/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package natural.language;
import java.util.HashMap;

/**
 *
 * @author YOSHINO KAI
 */
public class Space {
    HashMap<String,Integer[]> mBlocks;
    HashMap<Integer,Integer[]> mSpaceSize;
    HashMap<Integer,Integer> mXDepth;
    public Space(){
        mBlocks = new HashMap<String,Integer[]>();
        mSpaceSize = new  HashMap<Integer,Integer[]>();
        mXDepth = new HashMap<Integer,Integer>();
    }
}
