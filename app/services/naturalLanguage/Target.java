package services.naturalLanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import services.Space;


class MapMaker {
    //a_id と同じmIDを持つ Order をリストから取り除く(最初の一個だけ) 使われず
    Order removeOrderById(ArrayList<Order> a_orderList, String a_id) {
        int count = 0;
        for (Order a_order : a_orderList) {
            if (a_order.mID.equals(a_id)) {
                //System.out.println("Match!");
                return (a_orderList.remove(count));
            }
        }
        //System.out.println("NoMatch");
        return (null);
    }

    //次の基準となるブロックを選ぶ
    static Order getNextCtrl(ArrayList<Order> a_orderRest, ArrayList<Order> a_orderUsed) {
        int max = -1;
        Order next = a_orderRest.get(0);
        for (Order rest : a_orderRest) {
            int count = 0;
            for (Order used : a_orderUsed) {
                //System.out.println("mID="+used.mID+a_orderUsed.size());
                if (rest.contains(used.mID)) {//既に使われたブロックと関連があった場合
                    count += 2;
                }
            }
            count += rest.countConstraint();
            if (count > max) {
                max = count;
                next = rest;
            }
        }
        return (next);
    }

    //最も左下、基準となるブロックを選ぶ
    static Order getControlPoint(ArrayList<Order> a_orderList) {
        int max = 0;
        Order point = new Order();
        for (Order order : a_orderList) {
            int t_num = order.countCtrl();
            if (t_num > max) {
                point = order;
                max = t_num;
            }
        }

        return (point);
    }
	/*
    public static ArrayList<HeatMap> makeMap1(ArrayList<Order> a_orderList, int a_blockNum, ArrayList<Integer> aSpace) {
        ArrayList<HeatMap> heatMapS = new ArrayList<HeatMap>();
        Order ctrl = getControlPoint(a_orderList);//基準点を求める  
        Order lastCtrl = new Order();
        ArrayList<Order> usedOrderS = new ArrayList<Order>();//ヒートマップが作られたOrderリスト
        ArrayList<Order> restOrderS = new ArrayList<Order>(a_orderList);//残りのOrderリスト          
        HeatMap map = new HeatMap(ctrl.mID, aSpace);//始まりのヒートマップを作製
        while (heatMapS.size() < a_blockNum) {
            //map.show();//マップを表示する
            heatMapS.add(map);//マップを追加する
            lastCtrl = new Order(ctrl);
            usedOrderS.add(ctrl);
            restOrderS.remove(ctrl);
            if (restOrderS.size() > 0) {//残っているブロックがあれば更新
                ctrl = getNextCtrl(restOrderS, usedOrderS);//基準点を更新
                Constraint new_con = new Constraint(ctrl.mID, map.getMinPosition(), lastCtrl);//制約を作る
                map = new HeatMap(ctrl.mID, aSpace, map.getMinPosition(), new_con);
            }
        }
        return (heatMapS);
    }
	//*/
	/*
    public static ArrayList<HeatMap> makeMap2(ArrayList<Order> a_orderList, int a_blockNum, int width, int height) {
        ArrayList<HeatMap> heatMapS = new ArrayList<HeatMap>();
        Target tgt = new Target();
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
        return (heatMapS);
    }
	//*/
}

class Place {//オブジェクトの目標状態
    String name;//名前
    Integer[] coordinate;//座標
    ArrayList<String> Upper;//Y座標がこれより大きいもの
    ArrayList<String> Lower;//Y座標がこれより小さいもの
    ArrayList<String> Equal;//Y座標がこれと等しいもの

    void show() {
        System.out.println("Name=" + name + "[" + coordinate[0] + "," + coordinate[1] + "]" + Upper + " " + Lower + " " + Equal);
    }

    public Place(String a_name) {//コンストラクタ
        name = a_name;
        coordinate = new Integer[2];
        Upper = new ArrayList<String>();
        Lower = new ArrayList<String>();
        Equal = new ArrayList<String>();
    }

    public Place(Place a_origin) {
        name = new String(a_origin.name);
        coordinate = new Integer[]{a_origin.coordinate[0], a_origin.coordinate[1]};
        Upper = new ArrayList<String>(a_origin.Upper);
        Lower = new ArrayList<String>(a_origin.Lower);
        Equal = new ArrayList<String>(a_origin.Equal);
    }
}

/**
 * @author YOSHINO KAI
 */
public class Target {//目標状態のリストを生成する

    boolean containPositon(HashMap<String, Integer[]> blockMap, Integer[] pos) {
        for (HashMap.Entry<String, Integer[]> block : blockMap.entrySet()) {
            if (block.getValue()[0] == pos[0] && block.getValue()[1] == pos[1]) {
                return (true);
            }
        }
        return (false);
    }

    boolean containPoint(ArrayList<Integer[]> a_List, Integer[] a_pos) {
        for (Integer[] ele : a_List) {
            if (ele[0] == a_pos[0] && ele[1] == a_pos[1]) {
                return (true);
            }
        }
        return (false);
    }

    boolean containPoint2(ArrayList<Space> aUsedSpaceS, Space aNewSpace) {
        for (Space tSpace : aUsedSpaceS) {
            if (!tSpace.isSame(aNewSpace)) {//ブロックの配置が全く同じものがあった場合
                return (true);
            }
        }
        return (false);
    }

    HashMap<String, Integer[]> sortBlockS(HashMap<String, Integer[]> a_BlockS) {
        int maxY;
        HashMap<String, Integer[]> sortedS = new HashMap<String, Integer[]>();
        while (sortedS.size() < a_BlockS.size()) {
            maxY = -100;
            for (HashMap.Entry<String, Integer[]> block : a_BlockS.entrySet()) {
                if (maxY <= block.getValue()[1] && !sortedS.containsKey(block.getKey())) {
                    maxY = block.getValue()[1];
                    sortedS.put(block.getKey(), block.getValue());
                }
            }
        }
        return (sortedS);
    }
	
	/*
    ArrayList<Space> getSpace(ArrayList<HeatMap> a_hMapS, ArrayList<Integer> aSpace) {
        ArrayList<Space> spaceS = new ArrayList<Space>();
        ArrayList<ArrayList<Integer>> usedIndex = new ArrayList<ArrayList<Integer>>();
        int level, maxLevel = a_hMapS.size(), maxValue = 2, maxSpace = 100;
        int minX = aSpace.get(0), maxX = aSpace.get(1), minY = aSpace.get(2), maxY = aSpace.get(3);
        boolean end = false;
        int[] indexS = new int[maxLevel];
        for (int index : indexS) {
            index = 0;
        }

        while (spaceS.size() < maxSpace) {//maxSpace個まで解を求める
            Space ans = new Space(minX, maxX, minY, maxY);
            HashMap<String, Integer[]> mBlockS = new HashMap<String, Integer[]>();
            for (level = 0; level < maxLevel; ++level) {//ヒートマップを先頭から調べる
                HeatMap map = a_hMapS.get(level);//現在のヒートマップを取り出す
                while ((map.valueS.get(indexS[level]) > maxValue) || //最大値より大きいものは飛ばす
                        //ans.mBlocks.containsValue(map.positionS.get(indexS[level]))
                        containPositon(mBlockS, map.positionS.get(indexS[level]))//既にある座標なら飛ばす
                        ) {
                    ++indexS[level];
                    if (indexS[level] >= map.positionS.size()) {
                        break;
                    }
                    for (int j = level + 1; j < maxLevel; ++j) {
                        indexS[j] = 0;//ここより下のヒートマップのインデックスはリセット
                    }
                }

                if (indexS[level] >= map.positionS.size()) {
                    System.out.println("New!");
                    if (level == 0) {//一番上のヒートマップの限界に達した　すなわち条件を満たす解を全て求めた場合
                        end = true;
                    } else {
                        indexS[level] = 0;
                        ++indexS[level - 1];
                    }
                    break;
                }
                
            }//for(HeatMap map:a_hMapS)

            HashMap<String, Integer[]> sortedS = sortBlockS(mBlockS);
            for (HashMap.Entry<String, Integer[]> block : sortedS.entrySet()) {
                ans.addBlock(block.getKey(), block.getValue()[0], block.getValue()[1]);
            }

            if (end) {
                break;
            }
            ++indexS[level - 1];
            if (ans.check() && level == maxLevel) {
                spaceS.add(ans);
                ans.show();
                System.out.print("indexS=");
                for (int index : indexS) {
                    System.out.print(index + ",");
                }
                System.out.println("");
            }
        }//while(count < 100)
        System.out.println("Finish!" + spaceS.size());
        return (spaceS);
    }
	//*/
	
	//Place の リストをY座標の高い順に並べ替える
    ArrayList<Place> sortPlaceS(ArrayList<Place> a_placeS) {
        int maxY;
        Place minPlace = new Place("");
        ArrayList<Place> sortedS = new ArrayList<Place>();
        while (sortedS.size() < a_placeS.size()) {
            maxY = -100;
            for (Place a_place : a_placeS) {
                if (a_place.coordinate[1] >= maxY && !sortedS.contains(a_place)) {
                    maxY = a_place.coordinate[1];
                    minPlace = a_place;
                }
            }
            sortedS.add(minPlace);
        }
        return (sortedS);
    }

    Integer[] setPoint(Integer[] a_ctrlPoint, String a_currentName, Order a_ctrl, ArrayList<Integer[]> a_usedPointS, ArrayList<Integer> aSpace) {
        Integer[] newPoint = new Integer[]{a_ctrlPoint[0], a_ctrlPoint[1]};//座標をコピー
        int minX = aSpace.get(0), maxX = aSpace.get(1), minY = aSpace.get(2), maxY = aSpace.get(3);
        if (a_currentName.equals(a_ctrl.mID)) {//基準点と今調べている点が同一のものであった場合
            return (newPoint);//そのまま返す
        }

        if (!a_ctrl.mXEqual.contains(a_currentName)) {//X座標が同じでなくても問題ない場合
            if (a_ctrl.mXGreaterThan.contains(a_currentName)) {//基準点より左の点であった場合
                --newPoint[0];
                while (containPoint(a_usedPointS, newPoint)) {
                    --newPoint[0];
                    if (newPoint[0] < minX) {//左に行き過ぎた場合
                        ++newPoint[1];//上げる
                        newPoint[0] = a_ctrlPoint[0];//基準を戻す
                    }
                }
            } else if (a_ctrl.mXLessThan.contains(a_currentName)) {
                ++newPoint[0];
                while (containPoint(a_usedPointS, newPoint)) {
                    ++newPoint[0];
                    if (newPoint[0] > maxX) {//右に行き過ぎた場合
                        ++newPoint[1];//上げる
                        newPoint[0] = a_ctrlPoint[0];//基準を戻す
                    }
                }
            }
        }

        if (!a_ctrl.mYEqual.contains(a_currentName)) {//Y座標が同じでなくても問題ない場合
            if (a_ctrl.mYGreaterThan.contains(a_currentName)) {//基準点より下の点であった場合
                --newPoint[1];
                while (containPoint(a_usedPointS, newPoint)) {
                    --newPoint[1];
                    if (newPoint[1] < minY) {//下に行き過ぎた場合
                        ++newPoint[0];//右にずらす
                        newPoint[1] = a_ctrlPoint[1];//基準を戻す
                    }
                }
            } else if (a_ctrl.mYLessThan.contains(a_currentName)) {
                ++newPoint[1];
                while (containPoint(a_usedPointS, newPoint)) {
                    ++newPoint[1];
                    if (newPoint[1] > maxY) {//上に行き過ぎた場合
                        ++newPoint[0];//右にずらす
                        newPoint[1] = a_ctrlPoint[1];//基準を戻す
                    }
                }
            }
        }


        while (containPoint(a_usedPointS, newPoint)) {//まだ既存の点と被っている場合
            ++newPoint[0];//右にずらす
        }

        return (newPoint);
    }

    //次に扱う点を調べる 基準点ではない
    Order getNextOrder(Order a_ctrl, ArrayList<String> a_usedBlockS, ArrayList<Order> a_orderS) {
        int max = -1;
        Order next = null;
        for (Order a_order : a_orderS) {
            if (!a_usedBlockS.contains(a_order.mID) && a_ctrl.contains(a_order.mID)) {//使用済みでなく、基準点と関連のあるものを調べる
                int count = a_order.countConstraint();//制約数を調べる
                //System.out.println("next;name="+a_order.mID+" count="+count);
                if (max < count) {
                    next = a_order;
                    max = count;
                }
            }
        }
        return (next);//最も制約数の多いものを返す
    }

    //次の基準点を求める
    Order getNextCtrl2(ArrayList<String> a_usedBlockS, ArrayList<Order> a_orderS) {
        int max = -1;
        Order next = null;
        for (Order a_order : a_orderS) {
            if (!a_usedBlockS.contains(a_order.mID)) {//使用済みでないものを調べる
                int count = a_order.countConstraint();//制約数を調べる
                if (max < count) {
                    next = a_order;
                    max = count;
                }
            }
        }
        return (next);
    }

    boolean checkSlideS(Integer[] a_slideS, Integer[] a_maxYS, Integer[] a_minYS, int a_current, int a_width) {
        boolean end = false;
        if (a_current == a_width - 1) {//右端のスライドであった場合
            --a_slideS[a_current];//minYS[i]に近づける
            for (int j = 0; j < a_width; ++j) {//全てのスライドを調べる
                if (a_slideS[j] < a_minYS[j]) {//限界を超えた場合
                    if (j == 0) {//左端が限界に達したら終了
                        end = true;
                    } else {//そうでない場合
                        --a_slideS[j - 1];
                        for (int k = j; k < a_width; ++k) {
                            a_slideS[k] = a_maxYS[k];
                        }
                    }
                }//if(slideS[j] < minYS[j])
            }//for(int j = 0; j < width; ++j)
        }//if(i == width-1)
        return (end);
    }

    public ArrayList<Space> getTargetList2(FieldData aFData) {
        ArrayList<Integer> space = new ArrayList<Integer>();
        Converter conv = new Converter();
        //space = conv.getSpace2();
		space = aFData.getSpace();
        //ArrayList<String> objList = conv.getExObjList();
		ArrayList<String> objList = aFData.getExObjList();
        //ArrayList<Order> orderList = conv.getExOrdeListr(objList);
		ArrayList<Order> orderList = conv.getExOrderList2(objList,aFData);
        //Orderから Place 概念解(重力、重さを考えない)を求める
        ArrayList<Place> ansOrigin = new ArrayList<Place>();//概念解
        ArrayList<String> usedBlockS = new ArrayList<String>();
        ArrayList<Integer[]> usedPointS = new ArrayList<Integer[]>();//概念解を求める用
        Order ctrl = MapMaker.getControlPoint(orderList);//基準点を求める
        Order current = ctrl;//現在扱う点
        int minX = space.get(0), maxX = space.get(1), minY = space.get(2), maxY = space.get(3);
        Integer[] ctrlPoint = new Integer[]{0, 0};//基準座標
        //usedPointS.add(ctrlPoint);
        while (ansOrigin.size() < objList.size()) {//概念解に全てのブロックが含まれるまで続ける
            Place new_place = new Place(current.mID);
            Integer[] new_pos = setPoint(ctrlPoint, current.mID, ctrl, usedPointS, space);
            new_place.coordinate = new_pos;
            usedPointS.add(new_pos);
            usedBlockS.add(current.mID);
            new_place.Upper = new ArrayList<String>(current.mYLessThan);
            new_place.Lower = new ArrayList<String>(current.mYGreaterThan);
            new_place.Equal = new ArrayList<String>(current.mYEqual);
            ansOrigin.add(new_place);
            new_place.show();
            Order next = getNextOrder(ctrl, usedBlockS, orderList);//次に扱う点を基準点との関りから求める
            if (next == null) {//現在の基準点からは求まらなかった場合 次の基準点を求める
                if (!ctrl.equals(current)) {//現在扱っている点と基準点が異なる場合
                    ctrl = current;//現在の点を新たな基準点にする
                    current = getNextOrder(ctrl, usedBlockS, orderList);
                    if (current == null) {
                        current = getNextCtrl2(usedBlockS, orderList);
                    }
                } else {//現在扱っている点と基準点が同じ場合
                    ctrl = getNextCtrl2(usedBlockS, orderList);//新たな基準点を求める
                    //System.out.println("new ctrl ="+ctrl.mID);
                    current = ctrl;//基準点を調べる
                }
                ctrlPoint[0] = new_pos[0];
                ctrlPoint[1] = new_pos[1];
            } else {
                current = next;
                //current.show();
            }
        }//while(ansOrigin.size() < objList.size())

        //概念解の各楯列の最高・最低点を求め異なるなら２点間でずらす int[] current int[][] range
        Integer[] minYS = new Integer[space.get(1)], maxYS = new Integer[space.get(1)];//各縦列の高さ、低さ
        Integer[] slideS = new Integer[space.get(1)];
        Arrays.fill(minYS, 0);
        Arrays.fill(maxYS, 0);
        Arrays.fill(slideS, 0);//0で初期化する
        for (Place place : ansOrigin) {
            if (minYS[place.coordinate[0]] > place.coordinate[1]) {//現在より低い地点があった場合
                minYS[place.coordinate[0]] = place.coordinate[1];
            }
            if (maxYS[place.coordinate[0]] < place.coordinate[1]) {//現在より高い地点があった場合
                maxYS[place.coordinate[0]] = place.coordinate[1];
            }
        }

        for (int i = 0; i < slideS.length; ++i) {
            System.out.println("max" + i + "=" + maxYS[i] + " min" + i + "=" + minYS[i]);
            slideS[i] = maxYS[i];//最高点をコピー
        }

        //概念解をずらすときに関連するブロックもずらす
        //ずらしたものを変換したものを解とする
        ArrayList<Space> answerList = new ArrayList<Space>();
        boolean end = false, except = false;
        while (answerList.size() < 100) {//100個解を生成するか、全ての解を見つけるまで続ける
            ArrayList<Place> newPlaceS = new ArrayList<Place>();
            except = false;
            for (Place place : ansOrigin) {
                newPlaceS.add(new Place(place));
            }

            for (int i = 0; i < maxX; ++i) {//左からずらしていく
                for (Place place : newPlaceS) {//現在あるブロックを全て調べる
                    if (place.coordinate[0] == i && minYS[i] <= slideS[i]) {//X座標が一致し、スライドが許容範囲内の場合
                        place.coordinate[1] -= slideS[i];//Y座標をずらす
                        //System.out.println("slide "+place.name+":"+place.coordinate[1]+" "+slideS[i]);

                        for (String upper : place.Upper) {//ずらしたブロックより高い位置にあるブロックを調べる
                            for (Place place2 : newPlaceS) {
                                if (place2.name.equals(upper) && slideS[i] < 0 &&
                                        place2.coordinate[0] != i) {
                                    place2.coordinate[1] -= slideS[i];
                                    System.out.println("upper slide " + place2.name + ":" + place2.coordinate[1] + " " + slideS[i]);
                                }
                            }
                        }
                        for (String lower : place.Lower) {//ずらしたブロックより低い位置にあるブロックを調べる
                            for (Place place2 : newPlaceS) {
                                if (place2.name.equals(lower) && slideS[i] > 0 &&
                                        place2.coordinate[0] != i) {
                                    place2.coordinate[1] -= slideS[i];
                                    System.out.println("lower " + place2.name + ":" + place2.coordinate[1] + " " + slideS[i]);
                                }
                            }
                        }
                        //*/
                        for (String equal : place.Equal) {//ずらしたブロックと同じ高さにあるブロックを調べる
                            for (Place place2 : newPlaceS) {
                                if (place2.name.equals(equal) && slideS[i] != 0 &&
                                        place2.coordinate[0] != i) {
                                    place2.coordinate[1] -= slideS[i];
                                }
                            }
                        }
                    }//if(place.coordinate[0] == i){//X座標が一致した場合
                }//for(Place place:newPlaceS){//現在ある点を全て調べる
                end = checkSlideS(slideS, maxYS, minYS, i, maxX);

                if (end) {
                    break;
                }
            }//for(int i = 0; i < width; ++i)
            System.out.println("slideS=" + slideS[0] + "," + slideS[1] + "," + slideS[2] + "," + slideS[3] + "," + slideS[4]);
            if (end) {
                break;
            }

            Space ans = new Space(minX, maxX, minY, maxY);
            ArrayList<Place> sortPlaceS = sortPlaceS(newPlaceS);//高い順に並び替える
            for (Place sorted : sortPlaceS) {
                try {
                    ans.addBlock(sorted.name, sorted.coordinate[0], sorted.coordinate[1]);
                } catch (IllegalArgumentException e) {
                    except = true;
                }
            }

            if (!except && ans.check() && !containPoint2(answerList, ans)) {
                answerList.add(ans);
                ans.show();
            }

        }//
        System.out.println("answer=" + answerList.size());
        return (answerList);
    }
	/*
    ArrayList<Space> getTargetList() {
        ArrayList<Integer> space = new ArrayList<Integer>();
        Converter conv = new Converter();
        space = conv.getSpace2();
        ArrayList<String> objList = conv.getExObjList();
        ArrayList<Order> orderList = conv.getExOrdeListr(objList);
        ArrayList<HeatMap> heatMapS = new ArrayList<HeatMap>();//ヒートマップリスト
        heatMapS = MapMaker.makeMap1(orderList, objList.size(), space);//ヒートマップのリストを作る
        ArrayList<Space> answerList = getSpace(heatMapS, space);//ヒートマップから解候補のリストを作る

        return (answerList);
    }
	//*/
}

/*
class Constraint {
    String mID;
    int xThan = 0;
    int yThan = 0;
    boolean xEq = false;
    boolean yEq = false;
    Integer[] origin;

    void show() {
        System.out.println("mID=" + mID + " xThan=" + xThan + " yThan=" + yThan + " xEq=" + xEq + " yEq=" + yEq + " origin=" + origin[0] + "," + origin[1]);
    }

    public Constraint(String a_id, Integer[] a_origin, Order a_order) {
        mID = a_id;
        origin = a_origin;
        if (a_order.mXGreaterThan.contains(a_id)) {//a_orderのブロックのX座標がa_idより大きい
            xThan = -1;
        } else if (a_order.mXLessThan.contains(a_id)) {
            xThan = 1;
        } else if (a_order.mXEqual.contains(a_id)) {
            xEq = true;
        }

        if (a_order.mYGreaterThan.contains(a_id)) {
            yThan = -1;
        } else if (a_order.mYLessThan.contains(a_id)) {
            yThan = 1;
        } else if (a_order.mYEqual.contains(a_id)) {
            yEq = true;
        }
    }
}

class HeatMap {
    String mID;
    ArrayList<Integer[]> positionS;
    ArrayList<Integer> valueS;
    int length;

    //2点間の移動マス距離を求める
    int countDistance(Integer[] a_origin, Integer[] a_target) {
        int xdis = (a_origin[0] - a_target[0]);
        if (xdis < 0) {
            xdis *= -1;
        }
        int ydis = (a_origin[1] - a_target[1]);
        if (ydis < 0) {
            ydis *= -1;
        }
        int dist = xdis + ydis;
        return (dist);
    }

    //valueが最も小さい座標を返す
    Integer[] getMinPosition() {
        int i = 0, index = 0, min = 100000;
        for (Integer value : valueS) {
            if (value <= min) {
                index = i;
                min = value;
            }
            ++i;
        }
        return (positionS.get(index));
    }

    void show() {
        System.out.println("mID=" + mID);
        for (int i = 0; i < length; ++i) {
            System.out.println("pos=" + positionS.get(i)[0] + "," + positionS.get(i)[1] + " value=" + valueS.get(i));
        }
    }

    private boolean checkX(Integer a_x, Integer a_origin, Constraint a_con) {
        if (a_x < 0) {
            return (false);
        }
        if (a_con.xEq && a_x != a_origin) {//XEqual の条件下でX座標が異なる
            return (false);
        }
        if (a_con.xThan == 1 && a_x <= a_origin) {//xGreaterThan の条件下でX座標が基準点より小さい
            return (false);
        }
        if (a_con.xThan == -1 && a_x >= a_origin) {//xLessThan の条件下でX座標が基準点より大きい
            return (false);
        }
        return (true);
    }

    private boolean checkY(Integer a_y, Integer a_origin, Constraint a_con) {
        if (a_con.yEq && a_y != a_origin) {//YEqual の条件下でX座標が異なる
            return (false);
        }
        if (a_con.yThan == 1 && a_y <= a_origin) {//yGreaterThan の条件下でY座標が基準点より小さい
            return (false);
        }
        if (a_con.yThan == -1 && a_y >= a_origin) {//yLessThan の条件下でY座標が基準点より大きい
            return (false);
        }
        return (true);
    }

    private boolean containPos(Integer[] a_new, ArrayList<Integer[]> a_usedS) {
        for (Integer[] a_used : a_usedS) {
            if (a_used[0] == a_new[0] && a_used[1] == a_new[1]) {
                return (true);
            }
        }
        return (false);
    }

    private void addYMap(Integer[] a_pos, Integer[] a_origin, Constraint a_con, Integer height, ArrayList<Integer[]> a_usedS) {
        int j;
        if (checkX(a_pos[0], a_origin[0], a_con)) {//X座標が条件を満たしていた場合
            for (j = a_origin[1]; j < height && a_origin[1] + j <= height && a_origin[1] - j >= -height + 1; ++j) {//縦にずらす
                Integer[] new_pos = new Integer[]{a_pos[0], a_origin[1] + j};
                if (checkY(new_pos[1], a_origin[1], a_con) && !containPos(a_pos, a_usedS)) {
                    Integer value = countDistance(a_origin, new_pos);
                    positionS.add(new_pos);
                    valueS.add(value);
                    //System.out.println("add pos="+new_pos[0]+","+new_pos[1]+" value="+value);
                }

                if (j > 0) {//j が1以上なら反転も考慮する
                    Integer[] pos_yr = new Integer[2];
                    pos_yr[0] = a_pos[0];//X座標はコピー
                    pos_yr[1] = a_origin[1] - j;//Y座標は反転
                    if (checkY(pos_yr[1], a_origin[1], a_con) && !containPos(pos_yr, a_usedS)) {
                        Integer value = countDistance(a_origin, pos_yr);
                        positionS.add(pos_yr);
                        valueS.add(value);
                        //System.out.println("addR pos="+pos_yr[0]+","+pos_yr[1]+" value="+value);
                    }
                }//if(j > 0)
            }//for(j = origin[1]; j < height - (height/2); ++j)
        }//if(checkX(a_pos[0],a_origin[0],a_con))
    }

    private void addMap2(Integer[] a_pos, Integer[] a_origin, Constraint a_con, Integer height) {
        int j;
        if (checkX(a_pos[0], a_origin[0], a_con)) {//X座標が条件を満たしていた場合
            for (j = a_origin[1]; j < height && a_origin[1] + j <= height && a_origin[1] - j >= -height + 1; ++j) {//縦にずらす
                Integer[] new_pos = new Integer[]{a_pos[0], a_origin[1] + j};
                if (checkY(new_pos[1], a_origin[1], a_con) && !containPos(new_pos, positionS)) {
                    Integer value = countDistance(a_origin, new_pos);
                    positionS.add(new_pos);
                    valueS.add(value);
                    //System.out.println("add pos="+new_pos[0]+","+new_pos[1]+" value="+value);
                }

                if (j > 0) {//j が1以上なら反転も考慮する
                    Integer[] pos_yr = new Integer[2];//反転座標
                    pos_yr[0] = a_pos[0];//X座標はコピー
                    pos_yr[1] = a_origin[1] - j;//Y座標は反転
                    if (checkY(pos_yr[1], a_origin[1], a_con) && !containPos(pos_yr, positionS)) {
                        Integer value = countDistance(a_origin, pos_yr);
                        positionS.add(pos_yr);
                        valueS.add(value);
                        //System.out.println("addR pos="+pos_yr[0]+","+pos_yr[1]+" value="+value);
                    }
                }//if(j > 0)
            }//for(j = origin[1]; j < height - (height/2); ++j)
        }//if(checkX(a_pos[0],a_origin[0],a_con))
    }

    public HeatMap(String id, ArrayList<Integer> aSpace) {//コンストラクタ 最初のヒートマップ用
        int minX = aSpace.get(0), maxX = aSpace.get(1), minY = aSpace.get(2), maxY = aSpace.get(3);
        mID = id;
        length = maxY;
        positionS = new ArrayList<Integer[]>();
        valueS = new ArrayList<Integer>();
        int i;
        for (i = 0; i < maxY - (maxY / 2); ++i) {
            Integer[] pos = new Integer[2];
            pos[0] = minX;
            pos[1] = i;
            Integer value = countDistance(new Integer[]{0, 0}, pos);
            positionS.add(pos);
            valueS.add(value);
            if (i > 0) {
                Integer[] pos2 = new Integer[2];
                pos2[0] = minX;
                pos2[1] = -i;
                Integer value2 = countDistance(new Integer[]{0, 0}, pos2);
                positionS.add(pos2);
                valueS.add(value2);
            }
        }
    }

    public HeatMap(String id, ArrayList<Integer> aSpace, Integer[] origin, Constraint a_con) {
        mID = id;
        positionS = new ArrayList<Integer[]>();
        valueS = new ArrayList<Integer>();
        int minX = aSpace.get(0), maxX = aSpace.get(1), minY = aSpace.get(2), maxY = aSpace.get(3);
        int i;
        for (i = 0; i < maxX; ++i) {//横にずらす 横の範囲内に収まるようにする
            Integer[] pos = new Integer[2];
            pos[0] = origin[0] + i;
            addMap2(pos, origin, a_con, maxY);
            if (i > 0) {
                Integer[] pos_xr = new Integer[2];
                pos_xr[0] = origin[0] - i;//X座標を反転
                addMap2(pos_xr, origin, a_con, maxY);
            }
        }//for(i = 0; origin[0]+i < width/2 ;++i)
        length = positionS.size();
    }

    public HeatMap(String id, ArrayList<Integer> aSpace, ArrayList<Integer[]> a_used, Constraint a_con) {//２個目以降のヒートマップ用
        mID = id;
        positionS = new ArrayList<Integer[]>();
        valueS = new ArrayList<Integer>();
        //Integer[] origin = {used.get(used.size() - 1)[0],used.get(used.size() - 1)[1]};//使用済座標リストの最後が基準点
        Integer[] origin = a_con.origin;
        int minX = aSpace.get(0), maxX = aSpace.get(1), minY = aSpace.get(2), maxY = aSpace.get(3);
        int i, j;
        for (i = 0; i < maxX; ++i) {//横にずらす 横の範囲内に収まるようにする
            Integer[] pos = new Integer[2];
            pos[0] = origin[0] + i;
            addYMap(pos, origin, a_con, maxY, a_used);
            if (i > 0) {
                Integer[] pos_xr = new Integer[2];
                pos_xr[0] = origin[0] - i;//X座標を反転
                addYMap(pos_xr, origin, a_con, maxY, a_used);
            }
        }//for(i = 0; origin[0]+i < width/2 ;++i)
        length = positionS.size();
    }//HeatMapコンストラクタ
}

*/