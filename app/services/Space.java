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
    void show(){
        System.out.print("Blocks=");
        for(HashMap.Entry<String, Integer[]> e : mBlocks.entrySet()) {
            System.out.print(e.getKey() + " :[ " + e.getValue()[0]+","+e.getValue()[1]+"] ");
        }
        System.out.println("");
        System.out.print("space=");
        for(HashMap.Entry<Integer, Integer[]> e : mSpaceSize.entrySet()) {
            System.out.print(e.getKey() + " :[ " + e.getValue()[0]+","+e.getValue()[1]+"] ");
        }
        System.out.println("");
        System.out.println("Depth="+mXDepth);
    }
    
    public Space(){
        mBlocks = new HashMap<String,Integer[]>();
        mSpaceSize = new  HashMap<Integer,Integer[]>();
        mXDepth = new HashMap<Integer,Integer>();
    }
}
