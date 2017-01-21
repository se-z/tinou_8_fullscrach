package services;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Position {
    String alpha;
    String beta;
    String relation;
}

class Target1 {//オブジェクトの目標状態
    String name;//名前
    int[] coordinate;//座標

    public Target1() {//コンストラクタ
        coordinate = new int[2];
    }
}

class Target2 {
    String mID;//このオブジェクトの名前
    ArrayList<String> mXLessThan;//このオブジェクトのX座標の方が小さいオブジェクトの名前
    ArrayList<String> mXGreaterThan;////このオブジェクトのX座標の方が大きいオブジェクトの名前
    ArrayList<String> mYLessThan;//このオブジェクトのY座標の方が小さいオブジェクトの名前
    ArrayList<String> mYGreaterThan;//このオブジェクトのY座標の方が大きいオブジェクトの名前
    ArrayList<String> mXEqual;//このオブジェクトのY座標が同じオブジェクトの名前
    ArrayList<String> mYEqual;//このオブジェクトのY座標が同じオブジェクトの名前

    public Target2() {//コンストラクタ
        mXLessThan = new ArrayList<String>();
        mXGreaterThan = new ArrayList<String>();
        mYLessThan = new ArrayList<String>();
        mYGreaterThan = new ArrayList<String>();
        mXEqual = new ArrayList<String>();
        mYEqual = new ArrayList<String>();
    }
}

/**
 * @author YOSHINO KAI
 */
public class NaturalLanguage {

    static String[] test = {"AはBの上", "BはCの上"};//A,B,C
    static String[] test2 = {"AはBの上", "BはCの上", "DはEの上"};//A,B,C,D,E
    static String[] test3 = {"AはBの上", "赤は緑の上"};
    static String[] test4 = {"AはBの上", "BはCの右"};
    static String[] test5 = {"AはBとCの右", "Dの上はB", "EとFとGはDの右"};
    static String[] test6 = {"AはBの上", "Aの右はD", "AはCの左", "Eの下はA"};
    static String[] test7 = {"AはBの上", "CはDの上", "AはAの上"};
    static String[] test8 = {"A,B,Cは同じ高さ", "DはEより右", "FはGの真上", "HはIの真隣", "JはKの真右", "LはMの右上"};
    static String[] test9 = {"赤は青の上", "三角は四角の上"};
    static String test10 = "AはBの上、BがCの上";
    static String[] color = {"赤", "緑", "青", "黄"};
    static String[] shape = {"四角", "三角", "台形"};
    static List<String> relateS = Arrays.asList("上", "下", "左", "右", "同じ高さ", "隣");
    static List<String> spChar = Arrays.asList(",", ".", "真", "さ");//特殊文字一覧 これに含まれるものは弾く
    HashMap<String, String> reverse = new HashMap<String, String>() {{
        put("上", "下");
        put("下", "上");
        put("右", "左");
        put("左", "右");
    }};
    static String justRegex = "ちょうど|真";

    ArrayList<String> getObjList(String[] arg_tgtStateS) {
        ArrayList<String> list = new ArrayList<String>();
        for (String state : arg_tgtStateS) {
            ArrayList<Morpheme> morphs = Morpheme.analyzeMorpheme(state);
            for (Morpheme morph : morphs) {
                //System.out.println(morph.toString());
                boolean contain = false;//既に含まれているか
                for (String relate : relateS) {
                    if (morph.getSurface().contains(relate)) {
                        contain = true;
                    }
                }

                if (morph.contain("名詞") && !relateS.contains(morph.getSurface()) && !contain &&
                        !spChar.contains(morph.getSurface())) {//名詞であり、関係を表すものでない場合
                    if (!list.contains(morph.surface)) {
                        list.add(morph.surface);
                    }
                }
            }
        }

        return (list);
    }

    ArrayList<Position> getPosList(String[] arg_tgtStateS) {

        ArrayList<Position> list = new ArrayList<Position>();
        for (String state : arg_tgtStateS) {
            //ArrayList<String> objS = new ArrayList<String>();//ブロックのリスト
            String[] splited = state.split("は|が");//"は","が"で分ける 想定では２つに分かれる
            if (splited.length != 2) {//2つに分けられなかった場合
                System.out.println("Unexpected splited!");
                return (list);
            }

            Pattern p = Pattern.compile("の|より");
            Matcher m = p.matcher(splited[0]);
            String subject, target;//主語塊と目標塊
            if (m.find()) {
                subject = splited[1];
                target = splited[0];
            } else {
                subject = splited[0];
                target = splited[1];
            }

            String[] objS = subject.split("と|、|,");//"と"で分けてブロック名にする
            String[] tgtS = target.split("の|より");//"の"で分けてブロックと場所に分ける
            String pos_relate;
            String[] tgtObjS;
            if (tgtS.length == 2) {//2つに分けられた場合
                tgtObjS = tgtS[0].split("と|、|,");//"と"で分けてブロック名にする
                pos_relate = tgtS[1];
            } else {//2つに分けられなかった場合
                tgtObjS = objS;//
                pos_relate = tgtS[0];
            }

            for (String alphaObj : objS) {
                for (String betaObj : tgtObjS) {
                    //System.out.println("alpha="+alphaObj+" beta="+betaObj+" rel="+pos_relate);
                    Position pos = setPosition(alphaObj, betaObj, pos_relate);
                    if (pos != null) {
                        list.add(pos);
                    }
                }
            }

        }//for(String state:arg_tgtStateS)

        return (list);
    }

    Position setPosition(String arg_left, String arg_right, String arg_rel) {
        Position pos = new Position();
        boolean contain = false;
        for (String relate : relateS) {
            if (arg_rel.contains(relate)) {//この方式にすることでrelateが "右" だけで"真右"等に対応できる
                contain = true;
            }
        }

        if (contain && !arg_left.equals(arg_right)) {//定義に入っている関係であり、それぞれ別のオブジェクトである
            pos.alpha = arg_left;
            pos.beta = arg_right;
            pos.relation = arg_rel;
        } else {//どの位置関係にも当てはまらなかった場合 または同じオブジェクトであった場合
            System.out.println("Unexpected Relation!");
            return (null);
        }

        return (pos);
    }

    boolean existCoordinate(int[] arg_coor, ArrayList<Target1> arg_targetS) {
        for (Target1 arg_target : arg_targetS) {
            if (arg_target.coordinate[0] == arg_coor[0] && arg_target.coordinate[1] == arg_coor[1]) {//既存の座標と一致した場合
                return (true);
            }
        }

        return (false);
    }

    Target1 getTarget(String arg_new, String arg_known, String arg_relation, ArrayList<Target1> arg_targetS) {
        Target1 target = new Target1();
        int[] known_coordinate = new int[2], new_coordinate = new int[2];
        for (Target1 arg_target : arg_targetS) {
            if (arg_target.name.equals(arg_known)) {
                known_coordinate = arg_target.coordinate;
                break;
            }
        }

        switch (arg_relation) {
            case "上": {
                new_coordinate[0] = known_coordinate[0];
                new_coordinate[1] = known_coordinate[1] + 1;
                while (existCoordinate(new_coordinate, arg_targetS)) {//座標が被る限りずらす
                    new_coordinate[1] += 1;
                }
                break;
            }
            case "下": {
                new_coordinate[0] = known_coordinate[0];
                new_coordinate[1] = known_coordinate[1] - 1;
                while (existCoordinate(new_coordinate, arg_targetS)) {//座標が被る限りずらす
                    new_coordinate[1] -= 1;
                }
                break;
            }
            case "右": {
                new_coordinate[0] = known_coordinate[0] + 1;
                new_coordinate[1] = known_coordinate[1];
                while (existCoordinate(new_coordinate, arg_targetS)) {//座標が被る限りずらす
                    new_coordinate[0] += 1;
                }
                break;
            }
            case "左": {
                new_coordinate[0] = known_coordinate[0] - 1;
                new_coordinate[1] = known_coordinate[1];
                while (existCoordinate(new_coordinate, arg_targetS)) {//座標が被る限りずらす
                    new_coordinate[0] -= 1;
                }
                break;
            }
        }

        target.name = arg_new;
        target.coordinate = new_coordinate;

        return (target);
    }

    ArrayList<Target1> getTgtList(ArrayList<String> objectS, ArrayList<Position> positionS) {
        int count = 0;
        boolean exist = false;
        ArrayList<String> register = new ArrayList<String>();//登録済みオブジェクトリスト
        ArrayList<Target1> targetS = new ArrayList<Target1>();
        for (String object : objectS) {
            Target1 tgt = new Target1();
            if (count == 0) {//初回の時
                tgt.name = object;
                tgt.coordinate[0] = 0;
                tgt.coordinate[1] = 0;//原点に設定
                targetS.add(tgt);
                register.add(object);
                ++count;
            }//if(count == 0)
            else {
                exist = false;
                for (Position position : positionS) {//全ての関係を調べる
                    if (position.alpha.equals(object) && register.contains(position.beta)) {//位置関係の左であり、右の座標が決まっている場合
                        tgt = getTarget(object, position.beta, position.relation, targetS);
                        targetS.add(tgt);
                        register.add(object);
                        exist = true;
                        break;
                    } else if (position.beta.equals(object) && register.contains(position.alpha)) {//位置関係の右であり、左の座標が決まっている場合
                        String new_relation = reverse.get(position.relation);
                        tgt = getTarget(object, position.alpha, new_relation, targetS);
                        targetS.add(tgt);
                        register.add(object);
                        exist = true;
                        break;
                    }
                }//forPosition position:positionS)

                if (!exist) {//まだ登録されていないものであった場合
                    tgt.name = object;
                    tgt.coordinate[0] = 1;
                    tgt.coordinate[1] = 0;
                    while (existCoordinate(tgt.coordinate, targetS)) {
                        tgt.coordinate[0] += 1;//横にずらすことにする
                    }
                    targetS.add(tgt);
                    register.add(object);
                }

            }//else
        }//for(for(String object:objectS))
        return (targetS);
    }

    ArrayList<Target1> getNormalizeTgtList(ArrayList<Target1> arg_targetS) {
        ArrayList<Target1> normal = new ArrayList<Target1>();
        int[] min = {0, 0};
        for (Target1 arg_target : arg_targetS) {
            if (arg_target.coordinate[0] < min[0]) {
                min[0] = arg_target.coordinate[0];
            }

            if (arg_target.coordinate[1] < min[1]) {
                min[1] = arg_target.coordinate[1];
            }
        }

        for (Target1 arg_target : arg_targetS) {
            Target1 normal_tgt = new Target1();
            normal_tgt.name = arg_target.name;
            normal_tgt.coordinate[0] = arg_target.coordinate[0] - min[0];
            normal_tgt.coordinate[1] = arg_target.coordinate[1] - min[1];
            normal.add(normal_tgt);
        }


        return (normal);
    }

    void fixHeightTgtList(ArrayList<Target1> arg_targetS) {
        boolean all_right = false;
        while (!all_right) {
            all_right = true;
            for (Target1 arg_target : arg_targetS) {
                int[] downed = {arg_target.coordinate[0], arg_target.coordinate[1] - 1};
                if (arg_target.coordinate[1] > 0 && !existCoordinate(downed, arg_targetS)) {//arg_targetが宙に浮いていた場合
                    arg_target.coordinate[1] -= 1;
                    all_right = false;
                }
            }
        }
    }

    void setTarget2(Target2 arg_target2, String arg_new, String arg_relation, boolean just) {

        switch (arg_relation) {
            case "上": {
                if (!arg_target2.mYGreaterThan.contains(arg_new)) {//まだ含まれていないものであった場合
                    arg_target2.mYGreaterThan.add(arg_new);
                    if (just) {//真上、ちょうど上であった場合
                        arg_target2.mXEqual.add(arg_new);
                    }
                }
                break;
            }
            case "下": {
                if (!arg_target2.mYLessThan.contains(arg_new)) {
                    arg_target2.mYLessThan.add(arg_new);
                    if (just) {//真下、ちょうど下であった場合
                        arg_target2.mXEqual.add(arg_new);
                    }
                }
                break;
            }
            case "右": {
                if (!arg_target2.mXGreaterThan.contains(arg_new)) {
                    arg_target2.mXGreaterThan.add(arg_new);
                    if (just) {//
                        arg_target2.mYEqual.add(arg_new);
                    }
                }
                break;
            }
            case "左": {
                if (!arg_target2.mXLessThan.contains(arg_new)) {
                    arg_target2.mXLessThan.add(arg_new);
                    if (just) {
                        arg_target2.mYEqual.add(arg_new);
                    }
                }
                break;
            }
            case "隣": {
                if (!arg_target2.mYEqual.contains(arg_new)) {
                    arg_target2.mYEqual.add(arg_new);
                }
                break;
            }
            case "同じ高さ": {
                if (!arg_target2.mYEqual.contains(arg_new)) {
                    arg_target2.mYEqual.add(arg_new);
                }
                break;
            }
        }

    }

    ArrayList<Target2> getTgt2List(ArrayList<String> objectS, ArrayList<Position> positionS) {

        ArrayList<String> register = new ArrayList<String>();//登録済みオブジェクトリスト
        ArrayList<Target2> target2S = new ArrayList<Target2>();
        for (String object : objectS) {
            Target2 tgt2 = new Target2();
            tgt2.mID = object;
            for (Position pos : positionS) {
                Pattern p = Pattern.compile(justRegex);
                Matcher m = p.matcher(pos.relation);
                //boolean just = pos.relation.contains(justRegex);//真 や ちょうど などを含んでいるか
                boolean just = m.find();
                if (pos.alpha.equals(object)) {//位置関係の左である場合
                    for (String relate : relateS) {
                        if (pos.relation.contains(relate)) {
                            setTarget2(tgt2, pos.beta, relate, just);
                        }
                    }
                    //setTarget2(tgt2,pos.beta,pos.relation);
                } else if (pos.beta.equals(object)) {//位置関係の右である場合
                    //String new_relation = reverse.get(pos.relation);
                    for (String relate : relateS) {
                        if (pos.relation.contains(relate)) {
                            String new_relation = relate;
                            if (reverse.containsKey(relate)) {
                                new_relation = reverse.get(relate);
                            }
                            setTarget2(tgt2, pos.alpha, new_relation, just);
                        }
                    }
                    //setTarget2(tgt2,pos.alpha,new_relation);
                }
            }
            target2S.add(tgt2);
        }//for(for(String object:objectS))
        return (target2S);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String[] inputS = test8;
        NaturalLanguage nl = new NaturalLanguage();
        int i = 0;
        for (String input : inputS) {
            System.out.println("input" + i + " = " + input);
            ++i;
        }

        ArrayList<String> objList = nl.getObjList(inputS);
        i = 0;
        for (String obj : objList) {
            System.out.println("obj" + i + " = " + obj);
            ++i;
        }

        ArrayList<Position> posList = nl.getPosList(inputS);
        i = 0;
        for (Position pos : posList) {
            System.out.println("pos" + i + " = " + pos.alpha + " " + pos.relation + " " + pos.beta);
            ++i;
        }

        ArrayList<Target2> tgt2List = nl.getTgt2List(objList, posList);
        i = 0;
        for (Target2 tgt2 : tgt2List) {
            System.out.println("tgt2_" + i + " = " + tgt2.mID +
                    ":mXGreaterThan=" + tgt2.mXGreaterThan + " mXLessThan=" + tgt2.mXLessThan +
                    " mYGreaterThan=" + tgt2.mYGreaterThan + " mYLessThan=" + tgt2.mYLessThan +
                    " mXEqual=" + tgt2.mXEqual + " mYEqual=" + tgt2.mYEqual);
            ++i;
        }
       /*
       ArrayList<Target> tgtList = nl.getTgtList(objList, posList);
        i = 0;
       for(Target tgt:tgtList){
           System.out.println("tgt"+i+" = "+tgt.name+" "+tgt.coordinate[0]+","+tgt.coordinate[1] );
           ++i;
       }
       //*/
       /*
       ArrayList<Target> normalized = nl.getNormalizeTgtList(tgtList);
        i = 0;
       for(Target normal:normalized){
           System.out.println("normal"+i+" = "+normal.name+" "+normal.coordinate[0]+","+normal.coordinate[1] );
           ++i;
       }
       //*/
       /*
       nl.fixHeightTgtList(normalized);
        i = 0;
       for(Target normal:normalized){
           System.out.println("fixed"+i+" = "+normal.name+" "+normal.coordinate[0]+","+normal.coordinate[1] );
           ++i;
       }
       //*/
    }


}