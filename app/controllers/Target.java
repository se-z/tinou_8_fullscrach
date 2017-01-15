/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package natural.language;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Constraint{
    String mID;
    int xThan=0;
    int yThan =0;
    boolean xEq=false;
    boolean yEq=false;
    Integer[] origin;
    void show(){
        System.out.println("mID="+mID+" xThan="+xThan+" yThan="+yThan+" xEq="+xEq+" yEq="+yEq+" origin="+origin[0]+","+origin[1]);
    }
    
    public Constraint(String a_id,Integer[] a_origin,Order a_order){
        mID = a_id;
        origin = a_origin;
        if(a_order.mXGreaterThan.contains(a_id)){//a_orderのブロックのX座標がa_idより大きい
            xThan = -1;
        }
        else if(a_order.mXLessThan.contains(a_id)){
            xThan = 1;
        }
        else if(a_order.mXEqual.contains(a_id)){
            xEq = true;
        }
        
        if(a_order.mYGreaterThan.contains(a_id)){
            yThan = -1;
        }
        else if(a_order.mYLessThan.contains(a_id)){
            yThan = 1;
        }
        else if(a_order.mYEqual.contains(a_id)){
            yEq = true;
        }
    }
}

class HeatMap{
    String mID;
    ArrayList<Integer[]> positionS;
    ArrayList<Integer> valueS;
    int length;
    //2点間の移動マス距離を求める
    int countDistance(Integer[] a_origin,Integer[] a_target){
        int xdis = (a_origin[0] - a_target[0]);
        if(xdis < 0){
            xdis*=-1;
        }
        int ydis = (a_origin[1] - a_target[1]);
        if(ydis < 0){
            ydis*=-1;
        }
        int dist = xdis+ydis;
        return(dist);
    }
    
    //valueが最も小さい座標を返す
    Integer[] getMinPosition(){
        int i=0,index = 0,min = 100000;
        for(Integer value:valueS){
            if(value <= min){
              index = i;
              min = value;
            }
            ++i;
        }
        return(positionS.get(index));
    }
    
    void show(){
        System.out.println("mID="+mID);
        for(int i = 0; i < length; ++i){
            System.out.println("pos="+positionS.get(i)[0]+","+positionS.get(i)[1] +" value="+ valueS.get(i));
        }
    }
    
    private boolean checkX(Integer a_x,Integer a_origin,Constraint a_con){
        if(a_x < 0){
            return(false);
        }
        if(a_con.xEq && a_x != a_origin){//XEqual の条件下でX座標が異なる
            return(false);
        }
        if(a_con.xThan == 1 && a_x <= a_origin){//xGreaterThan の条件下でX座標が基準点より小さい
            return(false);
        }
         if(a_con.xThan == -1 && a_x >= a_origin){//xLessThan の条件下でX座標が基準点より大きい
            return(false);
        }
         return(true);
    }
    
    private boolean checkY(Integer a_y,Integer a_origin,Constraint a_con){
        if(a_con.yEq && a_y != a_origin){//YEqual の条件下でX座標が異なる
            return(false);
        }
        if(a_con.yThan == 1 && a_y <= a_origin){//yGreaterThan の条件下でY座標が基準点より小さい
            return(false);
        }
         if(a_con.yThan == -1 && a_y >= a_origin){//yLessThan の条件下でY座標が基準点より大きい
            return(false);
        }
         return(true);
    }
    
    private boolean containPos(Integer[] a_new,ArrayList<Integer[]> a_usedS){
        for(Integer[] a_used:a_usedS){
            if(a_used[0] == a_new[0] && a_used[1] == a_new[1]){
                return(true);
            }
        }
        return(false);
    }
    
    private void addYMap(Integer[] a_pos,Integer[] a_origin,Constraint a_con,Integer height,ArrayList<Integer[]> a_usedS){
        int j;
        if(checkX(a_pos[0],a_origin[0],a_con)){//X座標が条件を満たしていた場合
            for(j = a_origin[1]; j < height && a_origin[1]+j <= height &&a_origin[1]-j  >= -height+1; ++j){//縦にずらす
                Integer[] new_pos = new Integer[] {a_pos[0],a_origin[1]+j};
                if(checkY(new_pos[1],a_origin[1],a_con) && !containPos(a_pos,a_usedS)){
                    Integer value = countDistance(a_origin,new_pos);
                    positionS.add(new_pos);
                    valueS.add(value);
                    //System.out.println("add pos="+new_pos[0]+","+new_pos[1]+" value="+value);
                }
                    
                if(j > 0){//j が1以上なら反転も考慮する
                    Integer[] pos_yr = new Integer[2];
                    pos_yr[0] = a_pos[0];//X座標はコピー
                    pos_yr[1] = a_origin[1]-j;//Y座標は反転
                    if(checkY(pos_yr[1],a_origin[1],a_con)  && !containPos(pos_yr,a_usedS)){
                        Integer value = countDistance(a_origin,pos_yr);
                        positionS.add(pos_yr);
                        valueS.add(value);
                        //System.out.println("addR pos="+pos_yr[0]+","+pos_yr[1]+" value="+value);
                    }
                }//if(j > 0)
            }//for(j = origin[1]; j < height - (height/2); ++j)
    }//if(checkX(a_pos[0],a_origin[0],a_con))
}
    
    private void addMap2(Integer[] a_pos,Integer[] a_origin,Constraint a_con,Integer height){
        int j;
        if(checkX(a_pos[0],a_origin[0],a_con)){//X座標が条件を満たしていた場合
            for(j = a_origin[1]; j < height && a_origin[1]+j <= height &&a_origin[1]-j  >= -height+1; ++j){//縦にずらす
                Integer[] new_pos = new Integer[] {a_pos[0],a_origin[1]+j};
                if(checkY(new_pos[1],a_origin[1],a_con)){
                    Integer value = countDistance(a_origin,new_pos);
                    positionS.add(new_pos);
                    valueS.add(value);
                    //System.out.println("add pos="+new_pos[0]+","+new_pos[1]+" value="+value);
                }
                    
                if(j > 0){//j が1以上なら反転も考慮する
                    Integer[] pos_yr = new Integer[2];
                    pos_yr[0] = a_pos[0];//X座標はコピー
                    pos_yr[1] = a_origin[1]-j;//Y座標は反転
                    if(checkY(pos_yr[1],a_origin[1],a_con)){
                        Integer value = countDistance(a_origin,pos_yr);
                        positionS.add(pos_yr);
                        valueS.add(value);
                        //System.out.println("addR pos="+pos_yr[0]+","+pos_yr[1]+" value="+value);
                    }
                }//if(j > 0)
            }//for(j = origin[1]; j < height - (height/2); ++j)
    }//if(checkX(a_pos[0],a_origin[0],a_con))
    }
    
    public HeatMap(String id, int height){//コンストラクタ 最初のヒートマップ用
        mID = id;
        length = height;
        positionS = new ArrayList<Integer[]>();
        valueS = new ArrayList<Integer>();
        int i;
        for(i = 0; i < height - (height/2);++i){
            Integer[] pos = new Integer[2];
            pos[0] = 0;pos[1] = i;
            Integer value = countDistance(new Integer[] {0,0},pos);
            positionS.add(pos);
            valueS.add(value);
            if(i > 0){
                Integer[] pos2 = new Integer[2];
                pos2[0] = 0;pos2[1] = -i;
                Integer value2 = countDistance(new Integer[] {0,0},pos2);
                positionS.add(pos2);
                valueS.add(value2);
            }
        }
    }

    public HeatMap(String id,int width,int height,Integer[] origin,Constraint a_con){
         mID = id;
        positionS = new ArrayList<Integer[]>();
        valueS = new ArrayList<Integer>();
        //Integer[] origin = {used.get(used.size() - 1)[0],used.get(used.size() - 1)[1]};//使用済座標リストの最後が基準点
        int i,j;
        for(i = 0; i < width ;++i){//横にずらす 横の範囲内に収まるようにする
            Integer[] pos = new Integer[2];
            pos[0] = origin[0]+i;
            addMap2(pos,origin,a_con,width);
            if(i > 0){
                Integer[] pos_xr = new Integer[2];
                pos_xr[0] = origin[0]-i;//X座標を反転
                addMap2(pos_xr,origin,a_con,width);
             } 
        }//for(i = 0; origin[0]+i < width/2 ;++i)
        length = positionS.size();
    }
    
    public HeatMap(String id,int width, int height,ArrayList<Integer[]> a_used,Constraint a_con){//２個目以降のヒートマップ用
        mID = id;
        positionS = new ArrayList<Integer[]>();
        valueS = new ArrayList<Integer>();
        //Integer[] origin = {used.get(used.size() - 1)[0],used.get(used.size() - 1)[1]};//使用済座標リストの最後が基準点
        Integer[] origin = a_con.origin;
        int i,j;
        for(i = 0; i < width ;++i){//横にずらす 横の範囲内に収まるようにする
            Integer[] pos = new Integer[2];
            pos[0] = origin[0]+i;
            addYMap(pos,origin,a_con,width,a_used);
            if(i > 0){
                Integer[] pos_xr = new Integer[2];
                pos_xr[0] = origin[0]-i;//X座標を反転
                addYMap(pos_xr,origin,a_con,width,a_used);
             } 
        }//for(i = 0; origin[0]+i < width/2 ;++i)
        length = positionS.size();
    }//HeatMapコンストラクタ
}

/**
 *
 * @author YOSHINO KAI
 */
public class Target {//目標状態のリストを生成する
    
    //a_id と同じmIDを持つ Order をリストから取り除く(最初の一個だけ)
    Order removeOrderById(ArrayList<Order> a_orderList,String a_id){
        int count = 0;
        for(Order a_order:a_orderList){
            if(a_order.mID.equals(a_id)){
                //System.out.println("Match!");
                return(a_orderList.remove(count));
            }
        }
        //System.out.println("NoMatch");
        return(null);
    }
    
    //次の基準となるブロックを選ぶ
    Order getNextCtrl(ArrayList<Order> a_orderRest,ArrayList<Order> a_orderUsed){
        int max=-1;
        Order next = a_orderRest.get(0);
        for(Order rest:a_orderRest){
            int count = 0;
            for(Order used:a_orderUsed){
                //System.out.println("mID="+used.mID+a_orderUsed.size());
                if(rest.contains(used.mID)){//既に使われたブロックと関連があった場合
                  count+=2;  
                }
            }
            count+=rest.countConstraint();
            if(count > max){
                max = count;
                next = rest;
            }
        }
        return(next);
    }
    
    //最も左下、基準となるブロックを選ぶ
    Order getControlPoint(ArrayList<Order> a_orderList){
        int max = 0;
        Order point = new Order();
        for(Order order:a_orderList){
            int t_num = order.countCtrl();
            if(t_num > max){
                point = order;
                max = t_num;
            }
        }
        
        return(point);
    }
    
    Space makeSpace(ArrayList<Order> a_orderList,Order a_control,ArrayList<Integer> a_space){
        Order ctrlPoint = a_control;
        Space space = new Space();
        space.mBlocks.put(ctrlPoint.mID, new Integer[] {0,0});//基準点を設定
        int i,init_width=a_space.get(0),init_height = a_space.get(1);
        for(i = 0; i < init_width ; ++i){
            space.mSpaceSize.put(i,new Integer[] {0,init_height});//空間を初期化
        }
        for(i = 0; i < init_width; ++i){
            space.mXDepth.put(i,0);//深さを初期化
        }
        
        ArrayList<Order> cloneOrderS = new ArrayList<Order>(a_orderList);//残りのブロックリスト
        for(i = 0; cloneOrderS.size() > 0; ++i){
            int max = -1;
            Order newCtrl = cloneOrderS.get(0);
            for(Order cOrder : cloneOrderS){//まだ残っている全てのブロックを調べる
               if(ctrlPoint.contains(cOrder.mID)){//現在の基準ブロックと関連のあるものであった場合
                   int t_num = cOrder.countConstraint();
                   if(t_num > max){//制約の多いものを優先する
                       newCtrl = cOrder;
                       max = t_num;
                   }
               }
            }
            
        }
        return(space);
    }
    
    boolean checkSpace(Space a_space){
        for(HashMap.Entry<String,Integer[]> block:a_space.mBlocks.entrySet()){
            if(block.getValue()[1] > 0 && //ブロックが宙に浮いていて
                   (
                    ! a_space.mBlocks.containsValue(new Integer[] {block.getValue()[0] ,block.getValue()[1]-1 })
                    )//その下にブロックがない場合
                    ){
                return(false);
            }
        }
        return(true);
    }
    
    boolean containPositon(HashMap<String,Integer[]>blockMap,Integer[] pos){
         for(HashMap.Entry<String,Integer[]> block:blockMap.entrySet()){
            if(block.getValue()[0] == pos[0] && block.getValue()[1] == pos[1]){
                return(true);
            }
        }
        return(false);
    }
    
    ArrayList<Space> getSpace(ArrayList<HeatMap> a_hMapS,ArrayList<Integer> a_spaceSize){
        ArrayList<Space> spaceS = new ArrayList<Space>();
        ArrayList<ArrayList<Integer>> usedIndex = new ArrayList<ArrayList<Integer>>();
        int level,maxLevel = a_hMapS.size(),maxValue = 3,count=0;
        int[] indexS = new int [maxLevel];
        for(int index:indexS){
            index = 0;
        }
        
        while(spaceS.size() < 100){//  
            Space ans = new Space();
            for(int i = 0; i < a_spaceSize.get(0);++i){//空間、深さを初期代入
                ans.mSpaceSize.put(i,new Integer[] {0,a_spaceSize.get(1)} );
                ans.mXDepth.put(i, 0);
            }
            
            for(level = 0; level < maxLevel;++level){//ヒートマップを先頭から調べる
                HeatMap map = a_hMapS.get(level);
                while((map.valueS.get(indexS[level]) > maxValue) || //最大値より大きいものは飛ばす
                        //ans.mBlocks.containsValue(map.positionS.get(indexS[level]))
                        containPositon(ans.mBlocks,map.positionS.get(indexS[level]))//既にある座標なら飛ばす
                        ){
                    ++indexS[level];
                    if(indexS[level] >= map.positionS.size()){
                        break;
                    }
                    for(int j=level+1; j < maxLevel;++j){
                        indexS[j] = 0;//ここより下のヒートマップのインデックスはリセット
                    }
                }
                
                if(indexS[level] >= map.positionS.size()){
                    System.out.println("New!");
                    ++indexS[level-1];
                    break;
                }
                else if(level == maxLevel-1){
                    ++indexS[level];
                }
                ans.mBlocks.put(map.mID, map.positionS.get(indexS[level]));
                Integer height = map.positionS.get(indexS[level])[1];
                if(height >= 0){
                     height = 0;
                 }
                    
                int x = map.positionS.get(indexS[level])[0];
                if(!ans.mXDepth.containsKey(x) ||  -height > ans.mXDepth.get(x)){
                    ans.mSpaceSize.put(x,new Integer[] {0+height,a_spaceSize.get(1)} );
                    ans.mXDepth.put(x, -height);
                }
                //};
                 for(int index:indexS){
                    System.out.print(index+",");
                }
                System.out.println(" level="+level);
                
        }//for(HeatMap map:a_hMapS)
            ans.show();
        if(checkSpace(ans)){
            spaceS.add(ans);
            System.out.print("indexS=");
            for(int index:indexS){
                System.out.print(index+",");
            }
            System.out.println("");         
        }
    }//while(count < 100)
    System.out.println("Finish!"+spaceS.size());
    return(spaceS);
}
    
    ArrayList<Space> getTagetList(){
        ArrayList<Integer> space = new ArrayList<Integer>();
        ArrayList<Block> blockList = new ArrayList<Block>();
        ArrayList<String> orderInputS = new ArrayList<String>();
        Converter conv = new Converter();
        space =  conv.getSpace2();
        System.out.println("space="+space);
        ArrayList<String> objList = conv.getExObjList();
        int i;
        /*
        i = 0;
        for(String obj:objList){
            System.out.println("obj"+ i +" = " + obj);
            ++i;
        }
        //*/
        ArrayList<Order> orderList = conv.getExOrdeListr(objList);
        //System.out.println(getControlPoint(orderList).mID);
        Order ctrl = getControlPoint(orderList);
        ArrayList<String>register = new ArrayList<String>();
        ArrayList<Space> answerList = new ArrayList<Space>();
        
        int blockNum = objList.size();   
        Order lastCtrl = new Order();
        ArrayList<Order> usedOrderS = new ArrayList<Order>();//ヒートマップが作られたOrderリスト
        ArrayList<Order> restOrderS = new ArrayList<Order>(orderList);//残りのOrderリスト          
        ArrayList<HeatMap> heatMapS  = new ArrayList<HeatMap>();//ヒートマップリスト
        HeatMap map = new HeatMap(ctrl.mID,space.get(1));
        while(heatMapS.size() < blockNum){
           map.show();
           heatMapS.add(map);
           lastCtrl = new Order(ctrl);
           usedOrderS.add(ctrl);
           restOrderS.remove(ctrl);
           if(restOrderS.size() > 0){
               ctrl = getNextCtrl(restOrderS,usedOrderS);//基準点を更新
               Constraint new_con = new Constraint(ctrl.mID,map.getMinPosition(),lastCtrl);//制約を作る
               map = new HeatMap(ctrl.mID,space.get(0),space.get(1),map.getMinPosition(),new_con);
               //System.out.println("Newed!");
            }
        }
        
        ArrayList<Space> ansList = getSpace(heatMapS,space);
        
        /*
        for(i = 0; i < start.length; ++i){
            boolean first = true;
            System.out.println("---------------------------------------------------------");
            ArrayList<Order> usedOrderS = new ArrayList<Order>();//ヒートマップが作られたOrderリスト
            ArrayList<Order> restOrderS = new ArrayList<Order>(orderList);//残りのOrderリスト          
            ArrayList<HeatMap> heatMapS  = new ArrayList<HeatMap>();//ヒートマップリスト
            heatMapS.add(start);//基準点のヒートマップを加える
            register.add(start.mID);
            ArrayList<Integer[]> usedPoint = new ArrayList<Integer[]>();//使われた座標リスト
            usedPoint.add(start.positionS.get(i));
            //System.out.println(cblockS);
            while(heatMapS.size() < blockNum){//基準点からの制約で全てのブロックのヒートマップを作れなかった場合 
                if(restOrderS.size() > 0){
                    lastCtrl = ctrl;
                    ctrl = getNextCtrl(restOrderS,usedOrderS);//基準点を更新
                    //System.out.println("Newed!");
                }
                    
                //if(!register.contains(ctrl.mID)){
                Constraint new_con = new Constraint(ctrl.mID,usedPoint.get(usedPoint.size()-1),lastCtrl);//制約を作る
                HeatMap newMap = new HeatMap(ctrl.mID,space.get(0),space.get(1),usedPoint,new_con);
                heatMapS.add(newMap);
                usedOrderS.add(ctrl);
                restOrderS.remove(ctrl);//基準点を使用済みとして取り除く
                    register.add(ctrl.mID);
                //}              
                ArrayList<String> cblockS = ctrl.getBlock();//基準点が制約しているブロックを得る
                //System.out.println("cblocks="+cblockS+" ctrl="+ctrl.mID);
                for(int j = 0; j < cblockS.size(); ++j){//基準点からヒートマップを作れるだけ作る
                    if(!register.contains(cblockS.get(j))){
                    Constraint con = new Constraint(cblockS.get(j),usedPoint.get(usedPoint.size()-1),ctrl);//制約を作る
                    //con.show();
                    HeatMap conMap = new HeatMap(cblockS.get(j),space.get(0),space.get(1),usedPoint,con);
                    heatMapS.add(conMap);
                    register.add(conMap.mID);
                    //System.out.println(conMap.mID);
                    Integer[] newUsed = conMap.getMinPosition();
                    usedPoint.add(newUsed);
                    Order usedOrder = removeOrderById(restOrderS,cblockS.get(j));
                    if(usedOrder != null){
                        usedOrderS.add(usedOrder);//使われたブロックを使用済みに移す
                    }
                    //conMap.show();  
                    }
                }//for(int j = 0; j < cblockS.size(); ++j)                
            }//while(heatMapS.size() < blockNum)
            
            for(HeatMap map:heatMapS){
                System.out.println(map.mID);
            }
            //System.out.println("Mapped!"+heatMapS.size()+blockNum);
            
        }//for(i = 0; i < start.length; ++i)
        //*/
        
        return(answerList);
    }
    
    public static void main(String[] args){
        Target tgt = new Target();
        tgt.getTagetList();
    }
}
