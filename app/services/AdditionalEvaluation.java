package services;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AdditionalEvaluation {
	
	//評価関数で使用するパラメータ
	//コメントの最後に示す式の値を加算する(kは変数名の略称)
	//地中にあるなら　+(穴の深さ×k)減点
	static final int kPointUnderground;
	
	//移動するブロックが副目標のブロック
	//x座標が目標地点と同じ、かつ、y座標が目標地点より高い　+(k)減点
	static final int kSPointHigh;
	//xy共に目標地点と同じ　+(k)加点（多めにする）
	static final int kSPointSame;
	//x座標が目標地点と同じ、かつ、y座標が目標地点より低い　+(k)減点
	static final int kSPointLow;
	//目標地点との距離　+(目標地点とのx方向の距離×k)減点
	static final int kSPointNearX;
	
	
	//移動するブロックが副目標のブロック以外
	//x座標が目標地点と同じ、かつ、y座標が目標地点より高い　+(k)減点
	static final int kPointHigh;
	//xy共に目標地点と同じ　+(k)減点
	static final int kPointSame;
	//x座標が目標地点と同じ、かつ、y座標が目標地点より低い　+(k)加点
	static final int kPointLow;
	//移動するブロックが上にブロックを乗せられるなら　 +(副目標のブロックとのx方向の距離×k)減点
	static final int kPointCanBeOn;
	//移動するブロックが上にブロックを乗せられないなら　 +(副目標のブロックとのx方向の距離×k)加点
	static final int kPointCannotBeOn;
	//移動するブロックの座標が副目標のブロックより高いor同じ　+(k)減点
	static final int kPointHighThanTarget;
	//移動するブロックの座標が副目標のブロックより低い　+(k)加点
	static final int kPointLowThanTarget;
	
	/**
	 * @param aSpaceList 評価するSpaceのリスト
	 * @param aTargetSpace 目標状態
	 * @param aBlocks 全てのブロックの情報
	 * @param aChosenBlockID 移動したブロックのID
	 * @param aSubTargetBlockID 副目標に設定されているブロックのID
	 * @return
	 */  
	public static Space[] evaluateSpace(ArrayList<Space> aSpaceList,Space aTargetSpace,HashMap<String,Block> aBlocks,String aChosenBlockID,String aSubTargetBlockID){
		boolean tSameFlag=aChosenBlockID.equals(aSubTargetBlockID);//移動したブロックが副目標のブロックならtrue

		//評価値をHashMapに記録
		HashMap<Space,Integer> tEvaluation=new HashMap<>();
		for(Space tSpace:aSpaceList){//評価するSpaceを取り出す
			if(tSameFlag)
				tEvaluation.put(tSpace,evaluateTarget(tSpace,aTargetSpace,aChosenBlockID));
			else
				tEvaluation.put(tSpace,evaluateNotTarget(tSpace,aTargetSpace,aChosenBlockID,aSubTargetBlockID,aBlocks.get(aChosenBlockID).canBeOn()));
		}
		
		
		//HashMapのソート 参考:http://papiroidsensei.com/memo/java_map_sort.html
		// List 生成 (ソート用)  
		List<Entry<Space, Integer>> tEntries = new ArrayList<Map.Entry<Space,Integer>>(tEvaluation.entrySet());  
		Collections.sort(tEntries, new Comparator<Map.Entry<Space,Integer>>() {  

			@Override  
			public int compare(  
					Entry<Space,Integer> entry1, Entry<Space,Integer> entry2) {  
				return ((Integer)entry2.getValue()).compareTo((Integer)entry1.getValue());  
			}  
		});  

		//配列に入れる  
		int tSize=tEntries.size();  
		Space[] tReturned =new Space[tSize];  
		int tNum=0;  
		for(Entry<Space,Integer> tS : tEntries){  
			tReturned[tNum]=tS.getKey();  
			tNum++;  
		}         
		return tReturned;  

	}
	/**
	 * 移動したブロックが副目標に設定されている場合の評価
	 * @param aSpace 評価する状態  
	 * @param aTargetSpace
	 * @param aChosenBlockID 移動したブロックのID
	 * @return
	 */   
	public int evaluateTarget(Space aSpace,Space aTargetSpace,String aChosenBlockID){
		int tValue=0;
		int[] tAfterMovePosition=aSpace.getPosition(aChosenBlockID);//移動するブロック（=副目標のブロック）の移動後の座標
		int[] tTargetPosition=aTargetSpace.getPosition(aChosenBlockID);//移動するブロック（=副目標のブロック）の目標地点
		
		//x座標の評価
		if(tAfterMovePosition[0]==tTargetPosition[0]){//x座標が目標地点と等しい場合の評価
			//目標地点よりy座標が
			if(tAfterMovePosition[1]>tTargetPosition[1])//高い
				tValue+=kSPointHigh;
			else if(tAfterMovePosition[1]==tTargetPosition[1])//同じ
				tValue+=kSPointSame;
			else//低い
				tValue+=kSPointLow;
		}
		else{//x座標が目標地点と異なる
			tValue+=Math.abs(tAfterMovePosition[0]-tTargetPosition[0])*kSPointNearX;
		}
		
		//地中にあるか評価
		if(tAfterMovePosition[1]<0)
			tValue+=-tAfterMovePosition[1]*kPointUnderground;
		
		return tValue;
	}
	/**
	 * 移動したブロックが副目標に設定されていない場合の評価
	 * @param aSpace 評価する状態
	 * @param aTargetSpace
	 * @param aChosenBlockID 移動したブロックのID
	 * @param aSubTargetBlockID 副目標に設定されているブロックのID
	 * @param aCanBeOn 移動したブロックは上にブロックをのせられるか（true:乗せられる　false:乗せられない）
	 *  @return
	 */
	public int evaluateNotTarget(Space aSpace,Space aTargetSpace,String aChosenBlockID,String aSubTargetBlockID,boolean aCanBeOn){
		int tValue=0;
		int[] tAfterMovePosition=aSpace.getPosition(aChosenBlockID);//移動するブロックの移動後の座標
		int[] tTargetGoalPosition=aTargetSpace.getPosition(aSubTargetBlockID);//副目標のブロックの目標地点
		int[] tTargetCurrentPosition=aTargetSpace.getPosition(aSubTargetBlockID);//副目標のブロックの現在の座標
		
		//x座標の評価
		if(tAfterMovePosition[0]==tTargetGoalPosition[0]){//x座標が副目標のブロックの目標地点と等しい場合の評価
			//目標地点よりy座標が
			if(tAfterMovePosition[1]>tTargetGoalPosition[1])//高い
				tValue+=kPointHigh;
			else if(tAfterMovePosition[1]==tTargetGoalPosition[1])//同じ
				tValue+=kPointSame;
			else//低い
				tValue+=kPointLow;
		}
		else{//x座標が目標地点と異なる
			int tDistance=Math.abs(tAfterMovePosition[0]-tTargetGoalPosition[0]);
			if(aCanBeOn)//移動するブロックが上にブロックを乗せられるか
				tValue+=tDistance*kPointCanBeOn;
			else
				tValue+=tDistance*kPointCannotBeOn;
			
			if(tAfterMovePosition[1]>=tTargetGoalPosition[1])//副目標のブロックより高い
				tValue+=kPointHighThanTarget;
			else//副目標のブロックより低い
				tValue+=kPointLowThanTarget;
		}
		
		//地中にあるか評価
		if(tAfterMovePosition[1]<0)
			tValue+=-tAfterMovePosition[1]*kPointUnderground;
		
		return tValue;
	}
}
