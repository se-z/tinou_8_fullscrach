/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package natural.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author YOSHINO KAI
 */
class Interm{
    String alpha;
    String beta;
    String relation;
}

class Block{
    String id;
    String color;
    String shape;
}

public class Converter {//自然言語の入力に対して、順序構造を表すクラスに変換する。
    static String[] test  = {"AはBの上","BはCの上"};//A,B,C
    static String[] test2 = {"AはBの上","BはCの上","DはEの上"};//A,B,C,D,E
    static String[] test3 = {"AはBの上","赤は緑の上"};
    static String[] test4 = {"AはBの上","BはCの右"};
    static String[] test5 = {"AはBとCの右","Dの上はB","EとFとGはDの右"};
    static String[] test6 = {"AはBの上","Aの右はD","CはAの左","Eの下はA"};
    static String[] test7 = {"AはBの上","CはDの上"};
    static String[] test8 = {"A,B,Cは同じ高さ","DはEより右","FはGの真上","HはIの真隣","JはKの真右","LはMの右上"};
    static String[] test9 = {"赤は青の上","三角は四角の上","DはEの下"};//A赤台形,B青台形,C黄三角,D緑四角
    static String[] test10 ={ "AはBの上","BがCの上"};
    static List<String> colorS = Arrays.asList("赤","青","緑","黄");
    static List<String> shapeS = Arrays.asList("四角","三角","台形");
    static List<String> relateS = Arrays.asList("上","下","左","右","同じ高さ","隣");
    static List<String> spChar = Arrays.asList(",",".","真","さ");//特殊文字一覧 これに含まれるものは弾く
    HashMap<String,String> reverse = new HashMap<String,String>(){{
        put("上","下");put("下", "上");put("右","左");put("左", "右");
    }};
    static String justRegex = "ちょうど|真";
    static String path = "src\\natural\\language\\Blocks.json";
    
ArrayList<String> getObjList(ArrayList<String> arg_tgtStateS){
    ArrayList<String> list = new ArrayList<String>();
    for(String state:arg_tgtStateS){
        ArrayList<Morpheme> morphs = Morpheme.analyzeMorpheme(state);
        for(Morpheme morph: morphs){
            //System.out.println(morph.toString());
            boolean contain=false;//既に含まれていないか
            for(String relate:relateS){
                if(morph.getSurface().contains(relate)){
                    contain = true;
                }
            }
            
            boolean concrete = true;//具体的でない色や形ではないか
            for(String color:colorS){
                if(morph.getSurface().contains(color)){
                    concrete = false;
                }
            }
            
            for(String shape:shapeS){
                if(morph.getSurface().contains(shape)){
                    concrete = false;
                }
            }
            
            if(morph.contain("名詞") && !relateS.contains(morph.getSurface()) && !contain &&
                    !spChar.contains(morph.getSurface()) && concrete ){//名詞であり、関係を表すものでない場合
                if(!list.contains(morph.surface)){
                    list.add(morph.surface);
                }
            }
        }
    }
        
    return(list);
}
   
ArrayList<Interm> getIntermList(ArrayList<String> arg_tgtStateS){
    
    ArrayList<Interm> list = new ArrayList<Interm>();
    for(String state:arg_tgtStateS){
        //ArrayList<String> objS = new ArrayList<String>();//ブロックのリスト
        String[] splited = state.split("は|が");//"は","が"で分ける 想定では２つに分かれる
        if(splited.length != 2){//2つに分けられなかった場合
            System.out.println("Unexpected splited!");
            return(list);
        }
        
        Pattern p = Pattern.compile("の|より");
        Matcher m = p.matcher(splited[0]);
        String subject,target;//主語塊と目標塊
        if(m.find()){
            subject = splited[1];
            target = splited[0];
        }
        else{
            subject = splited[0];
            target = splited[1];
        }
                 
        String[] objS = subject.split("と|、|,");//"と"で分けてブロック名にする
        String[] tgtS = target.split("の|より");//"の"で分けてブロックと場所に分ける
        String pos_relate;
        String[] tgtObjS;
        if(tgtS.length == 2){//2つに分けられた場合
            tgtObjS = tgtS[0].split("と|、|,");//"と"で分けてブロック名にする
            pos_relate = tgtS[1];
        }
        else{//2つに分けられなかった場合
            tgtObjS = objS;//
            pos_relate = tgtS[0];
        }
        
        for(String alphaObj:objS){
            for(String betaObj:tgtObjS){
                //System.out.println("alpha="+alphaObj+" beta="+betaObj+" rel="+pos_relate);
                Interm pos = setInterm(alphaObj,betaObj,pos_relate);
                if(pos != null){
                    list.add(pos);
                 }
            }
         }
            
    }//for(String state:arg_tgtStateS)
         
    return(list);
}
    
Interm setInterm(String arg_left,String arg_right,String arg_rel){
    Interm pos = new Interm();
    boolean contain=false;
    for(String relate:relateS){
        if(arg_rel.contains(relate)){//この方式にすることでrelateが "右" だけで"真右"等に対応できる
            contain = true;
        }
    }
    
    if(contain && !arg_left.equals(arg_right) ){//定義に入っている関係であり、それぞれ別のオブジェクトである
        pos.alpha = arg_left;
        pos.beta = arg_right;
        pos.relation = arg_rel;
    }
    else{//どの位置関係にも当てはまらなかった場合 または同じオブジェクトであった場合
        System.out.println("Unexpected Relation!");
        return(null);
    }
        
    return(pos);
}

//既に作られたIntermリストをブロック情報を基に拡張する
ArrayList<Interm> getExIntermList(ArrayList<Interm> a_originList,ArrayList<Block>a_blockList){
    ArrayList<Interm> exList = new ArrayList<Interm>();
    for(Interm origin:a_originList){
        ArrayList<String> alphaS = new ArrayList<String>();
        ArrayList<String> betaS = new ArrayList<String>();
        boolean concrete = true;//具体的でない色や形ではないか
        for(String color:colorS){
            if(origin.alpha.contains(color)){//左が色であった場合
                setMatchList(alphaS,a_blockList,color);//同じ色を持つものを全て取り出す
                concrete = false;
            }
            else if(origin.beta.contains(color)){//右が色であった場合 左右が同じ色であることはないと想定する
                setMatchList(betaS,a_blockList,color);
                concrete = false;
            }
        }
        
         for(String shape:shapeS){
            if(origin.alpha.contains(shape)){//左が形であった場合
                setMatchList(alphaS,a_blockList,shape);//形が一致するものを全て取り出す
                concrete = false;
            }
            else if(origin.beta.contains(shape)){//右が形であった場合
                setMatchList(betaS,a_blockList,shape);
                concrete = false;
            }
        }
         
        if(concrete){
            alphaS.add(origin.alpha);
            betaS.add(origin.beta);
        }
        //System.out.println("alpha="+alphaS);
        //System.out.println("beta="+betaS);
        for(String alpha:alphaS){
            for(String beta:betaS){
                Interm new_interm = new Interm();
                new_interm.alpha = alpha;new_interm.beta = beta;new_interm.relation = origin.relation;
                if(!containIntermList(exList,new_interm)){
                    exList.add(new_interm);
                }
            }
        }
    }
    return(exList);
}

void setMatchList(ArrayList<String>a_List ,ArrayList<Block>a_blockList,String a_match){
    for(Block block:a_blockList){
        if(block.color.equals(a_match) || block.shape.equals(a_match)){
            a_List.add(block.id);
        }
    }
}

//リスト内に a_match が含まれているか
boolean containIntermList(ArrayList<Interm> a_list,Interm a_match){
    for(Interm ele:a_list){
        if(ele.alpha.equals(a_match.alpha) && ele.beta.equals(a_match.beta) && 
                ele.relation.equals(a_match.relation)){
        return(true);
        }
    }
    return(false);
}

void setOrder(Order arg_order,String arg_new,String arg_relation, boolean arg_just){
       
    switch(arg_relation){
        case "上":{
            if(!arg_order.mYGreaterThan.contains(arg_new)){//まだ含まれていないものであった場合
                arg_order.mYGreaterThan.add(arg_new);
                if(arg_just){//真上、ちょうど上であった場合
                    arg_order.mXEqual.add(arg_new);
                }
            }
            break;
        }
        case "下":{
            if(!arg_order.mYLessThan.contains(arg_new)){
                arg_order.mYLessThan.add(arg_new);
                 if(arg_just){//真下、ちょうど下であった場合
                    arg_order.mXEqual.add(arg_new);
                }
            }
            break;
        }
        case "右":{
            if(!arg_order.mXGreaterThan.contains(arg_new)){
                arg_order.mXGreaterThan.add(arg_new);
                 if(arg_just){//
                    arg_order.mYEqual.add(arg_new);
                }
            }
            break;
        }
        case "左":{
            if(!arg_order.mXLessThan.contains(arg_new)){
                arg_order.mXLessThan.add(arg_new);
                 if(arg_just){
                    arg_order.mYEqual.add(arg_new);
                }
            }
            break;
         }
        case "隣":{
           if(!arg_order.mYEqual.contains(arg_new)){
               arg_order.mYEqual.add(arg_new);
           }
           break;
        }
        case "同じ高さ":{
            if(!arg_order.mYEqual.contains(arg_new)){
                arg_order.mYEqual.add(arg_new);
            }
            break;
        }
    }
               
}
 
ArrayList<Order> getOrderList(ArrayList<String> arg_objectS,ArrayList<Interm> arg_intermS){
    
    ArrayList<Order> OrderS  = new ArrayList<Order>();
    for(String object:arg_objectS){
        Order order = new Order();
        order.mID = object;
        for(Interm interm:arg_intermS){
            Pattern p = Pattern.compile(justRegex);
            Matcher m = p.matcher(interm.relation);
            boolean just = m.find();
            if(interm.alpha.equals(object)){//位置関係の左である場合
                for(String relate:relateS){
                    if(interm.relation.contains(relate)){
                        setOrder(order,interm.beta,relate,just);
                    }
                }
                //setTarget2(tgt2,pos.beta,pos.relation);
            }
            else if(interm.beta.equals(object)){//位置関係の右である場合
                //String new_relation = reverse.get(pos.relation);
                for(String relate:relateS){
                    if(interm.relation.contains(relate)){
                        String new_relation = relate;
                        if(reverse.containsKey(relate)){
                            new_relation = reverse.get(relate);
                        }
                        setOrder(order,interm.alpha,new_relation,just);
                    }
                }
                //setTarget2(tgt2,pos.alpha,new_relation);
            }
        }
        OrderS.add(order);
    }//for(for(String object:objectS))
    return(OrderS);
}

ArrayList<Integer> getSpace(JsonNode a_root){
    ArrayList<Integer> space = new ArrayList<Integer>();
    int i;
     for(i = 0; i < a_root.get("space").size(); ++i){
         space.add(a_root.get("space").get(i).asInt());
     }
    //System.out.println("space="+space);
     return(space);
}

ArrayList<Block> getBlockList(JsonNode a_root){
    ArrayList<Block> blockList = new ArrayList<Block>();
    int i;
    JsonNode blockArray = a_root.get("blocks");
    for(i = 0; i < blockArray.size(); ++i){
        Block newBlock = new Block();
        newBlock.id = blockArray.get(i).get("id").asText();
        newBlock.shape = blockArray.get(i).get("shape").asText();
        newBlock.color = blockArray.get(i).get("color").asText();
        blockList.add(newBlock);
    }
    return(blockList);
}

ArrayList<String> getOrderInput(JsonNode a_root){
    ArrayList<String> orderInputS = new ArrayList<String>();
    int i;
     for(i = 0; i < a_root.get("order").size(); ++i){
         orderInputS.add(a_root.get("order").get(i).asText());
     }
    return(orderInputS);
}

void readJSON(ArrayList<Integer> a_space,ArrayList<Block> a_bList,ArrayList<String> a_oInputS){
    try {
        ObjectMapper mapper = new ObjectMapper();
        //System.out.println(System.getProperty("user.dir"));
        File file = new File("src\\natural\\language\\Blocks.json");
        JsonNode root = mapper.readTree(file);
        a_space = new ArrayList(getSpace(root));
        a_bList = getBlockList(root);
        a_oInputS = getOrderInput(root);
        System.out.println(a_space);
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
     System.out.println(a_space);
    return;
}

//ブロックの情報からブロックリストを作る
ArrayList<String> makeExObjList(ArrayList<Block> a_blockList){
    ArrayList<String> objList = new ArrayList<String>();
    for(Block a_block:a_blockList){
        if(!objList.contains(a_block.id)){
            objList.add(a_block.id);
        }
    }
    return(objList);
}

/*
*自然言語からOrderに変換する 初代
*/
ArrayList<Order> convToOrder1(ArrayList<String> a_inputS,ArrayList<Block> a_blockList){
    ArrayList<String> objList = getObjList(a_inputS);
    ArrayList<Interm> intermList = getIntermList(a_inputS);
    ArrayList<Order> orderList = getOrderList(objList,intermList);
    return(orderList);
}

ArrayList<Order> convToOrder(ArrayList<String> a_inputS,ArrayList<Block> a_blockList){
    ArrayList<String> objList = makeExObjList(a_blockList);
    return(null);
}

ArrayList<Integer> getSpace2(){
    ArrayList<Integer> space = new ArrayList<Integer>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(path);
            JsonNode root = mapper.readTree(file);
            space =  getSpace(root);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return(space);
}

ArrayList<String> getExObjList(){
     ArrayList<Block> blockList = new ArrayList<Block>();
    ArrayList<String> objList = new ArrayList<String>();
    try {        
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(path);
        JsonNode root = mapper.readTree(file);
        blockList = getBlockList(root);
        objList = makeExObjList(blockList);
    }
    catch (IOException ioe) {
        ioe.printStackTrace();
    }
    return(objList);
}

ArrayList<Order> getExOrdeListr(ArrayList<String> a_objList){
    ArrayList<Order> orderList = new ArrayList<Order>();
    try {
            ObjectMapper mapper = new ObjectMapper();
            //System.out.println(System.getProperty("user.dir"));
            File file = new File(path);
            JsonNode root = mapper.readTree(file);
            ArrayList<Block> blockList = getBlockList(root);
            ArrayList<String> orderInputS = getOrderInput(root);
            ArrayList<Interm> intermList = getIntermList(orderInputS);
            ArrayList<Interm> exList = getExIntermList(intermList, blockList);
            orderList = getOrderList(a_objList,exList);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    
    return(orderList);
}

public static void main(String args[]){
       ArrayList<Integer> space = new ArrayList<Integer>();
       ArrayList<Block> blockList = new ArrayList<Block>();
       ArrayList<String> orderInputS = new ArrayList<String>();
       Converter conv = new Converter();
       //conv.readJSON(space,blockList,orderInputS);
       try {
            ObjectMapper mapper = new ObjectMapper();
            //System.out.println(System.getProperty("user.dir"));
            File file = new File(path);
            JsonNode root = mapper.readTree(file);
            space =  conv.getSpace(root);
            blockList = conv.getBlockList(root);
            orderInputS = conv.getOrderInput(root);
            ArrayList<String> objList = conv.makeExObjList(blockList);
            int i = 0;
            for(String obj:objList){
                System.out.println("obj"+ i +" = " + obj);
                ++i;
            }
            ArrayList<Interm> intermList = conv.getIntermList(orderInputS);
            i = 0;
            for(Interm interm:intermList){
                System.out.println("interm"+i+" = "+interm.alpha+" "+interm.relation+" "+interm.beta);
                ++i;
            }
            ArrayList<Interm> exList = conv.getExIntermList(intermList, blockList);
            i = 0;
            for(Interm interm:exList){
                System.out.println("intermEx"+i+" = "+interm.alpha+" "+interm.relation+" "+interm.beta);
                ++i;
            }
            ArrayList<Order> orderList = conv.getOrderList(objList,exList);
             i = 0;
            for(Order tgt2:orderList){
                System.out.println("tgt2_"+i+" = "+tgt2.mID+
                   ":mXGreaterThan="+tgt2.mXGreaterThan+" mXLessThan="+tgt2.mXLessThan + 
                   " mYGreaterThan="+tgt2.mYGreaterThan+" mYLessThan="+tgt2.mYLessThan +
                   " mXEqual="+tgt2.mXEqual+" mYEqual="+tgt2.mYEqual);
                ++i;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
       
    }
}
