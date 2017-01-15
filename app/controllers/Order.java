/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package natural.language;
import java.util.ArrayList;

/**
 *
 * @author YOSHINO KAI
 */
public class Order {//ブロック間の順序を表すデータ構造
    public String mID;//このオブジェクトの名前
    public ArrayList<String> mXLessThan;//このオブジェクトのX座標の方が小さいオブジェクトの名前
    public ArrayList<String> mXGreaterThan;////このオブジェクトのX座標の方が大きいオブジェクトの名前
    public ArrayList<String> mYLessThan;//このオブジェクトのY座標の方が小さいオブジェクトの名前
    public ArrayList<String> mYGreaterThan;//このオブジェクトのY座標の方が大きいオブジェクトの名前
    public ArrayList<String> mXEqual;//このオブジェクトのY座標が同じオブジェクトの名前
    public  ArrayList<String> mYEqual;//このオブジェクトのY座標が同じオブジェクトの名前
    public Order(){//コンストラクタ
        mXLessThan = new ArrayList<String>();
        mXGreaterThan = new ArrayList<String>();
        mYLessThan = new ArrayList<String>();
        mYGreaterThan = new ArrayList<String>();
        mXEqual = new ArrayList<String>();
        mYEqual = new ArrayList<String>();
    }
    
    public Order(Order original){
        mXLessThan = new ArrayList<String>(original.mXLessThan);
        mXGreaterThan = new ArrayList<String>(original.mXGreaterThan);
        mYLessThan = new ArrayList<String>(original.mYLessThan);
        mYGreaterThan = new ArrayList<String>(original.mYGreaterThan);
        mXEqual = new ArrayList<String>(original.mXEqual);
        mYEqual = new ArrayList<String>(original.mYEqual);
    }
    
    //基準点へのふさわしさ
    int countCtrl(){
         int count = mXGreaterThan.size()*(-1)+ mXLessThan.size()*2+
                    mYGreaterThan.size()*(-1)+mYLessThan.size()*1+
                    mXEqual.size()*1+mYEqual.size()*1;
        return(count);
    }
    
     //制約数を数える
    int countConstraint(){
        int count = mXGreaterThan.size()+ mXLessThan.size()+
                    mYGreaterThan.size()+mYLessThan.size()+
                    mXEqual.size()+mYEqual.size();
        return(count);
    }
    
    //targetをどこかに含まれているか
    boolean contains(String target){
        if(mXGreaterThan.contains(target) || mXLessThan.contains(target) || mXEqual.contains(target)){
            return(true);
        }
        else if(mYGreaterThan.contains(target) || mYLessThan.contains(target) || mYEqual.contains(target)){
            return(true);
        }
        else{
            return(false);
        }
    }
    
    //制約しているブロックの数を返す
    int countBlock(){
        ArrayList<String> blockS = new ArrayList<String>();
        for(String xgBlock:mXGreaterThan){
            if(!blockS.contains(xgBlock)){
                blockS.add(xgBlock);
            }
        }
        for(String xlBlock:mXLessThan){
            if(!blockS.contains(xlBlock)){
                blockS.add(xlBlock);
            }
        }
        for(String ygBlock:mYGreaterThan){
            if(!blockS.contains(ygBlock)){
                blockS.add(ygBlock);
            }
        }
        for(String ylBlock:mYLessThan){
            if(!blockS.contains(ylBlock)){
                blockS.add(ylBlock);
            }
        }
        for(String xeBlock:mXEqual){
            if(!blockS.contains(xeBlock)){
                blockS.add(xeBlock);
            }
        }
         for(String yeBlock:mYEqual){
            if(!blockS.contains(yeBlock)){
                blockS.add(yeBlock);
            }
        }
         return(blockS.size());
    }
    
    ArrayList<String> getBlock(){
        ArrayList<String> blockS = new ArrayList<String>();
        for(String xgBlock:mXGreaterThan){
            if(!blockS.contains(xgBlock)){
                blockS.add(xgBlock);
            }
        }
        for(String xlBlock:mXLessThan){
            if(!blockS.contains(xlBlock)){
                blockS.add(xlBlock);
            }
        }
        for(String ygBlock:mYGreaterThan){
            if(!blockS.contains(ygBlock)){
                blockS.add(ygBlock);
            }
        }
        for(String ylBlock:mYLessThan){
            if(!blockS.contains(ylBlock)){
                blockS.add(ylBlock);
            }
        }
        for(String xeBlock:mXEqual){
            if(!blockS.contains(xeBlock)){
                blockS.add(xeBlock);
            }
        }
         for(String yeBlock:mYEqual){
            if(!blockS.contains(yeBlock)){
                blockS.add(yeBlock);
            }
        }
         return(blockS);
    }
}
