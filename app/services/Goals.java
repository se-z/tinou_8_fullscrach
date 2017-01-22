package services;

/**
 * Created by seijihagawa on 2017/01/12.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;

/**
 * どの副目標から達成していくのかを管理します。
 * targetにするblockの処理の順序を管理する
 * <p>
 * mIDMap 系列はすべてint型の番号で管理しているのでIDとmappingさせるためのmap
 * mIDMap : {[1,A], [2,B], [3,C],,,}
 * <p>
 * 動作確認済み
 */
public class Goals {
    private String[] mIDs;
    private String mCurrentID;
    private int mNumberInLine;
    private int mSeriesNumber;
    private ArrayList<ArrayList<Integer>> mSeriesListInteger; //これ初期化しなくていい気がする
    private HashMap<Integer, String> mIDMap;
    private final int k_NUMBER = 10000;
    private ArrayList<Integer> mFalseSeries;

    public Goals(String[] aIDs) {
        mIDs = aIDs;
        mIDMap = new HashMap<>();
        mSeriesListInteger = permutation(mIDs.length);

        //String型のIDとInt型の系列リストを紐付けている
        for (int i = 1; i <= mIDs.length; i++) {
            mIDMap.put(i, mIDs[i - 1]);
        }

        mSeriesNumber = 0;
        mNumberInLine = 0;
        mFalseSeries = new ArrayList<>();
    }


    /**
     * 失敗した系列を登録し、探索を行う系列をランダムに決定、最初のノードを指定する
     */
    public void randomSetSeries() {

        if (!mFalseSeries.isEmpty()) {
            int tPrevNumber = mSeriesNumber;
            mFalseSeries.add(tPrevNumber);
        }

        int tRandom;
        do {
            tRandom = (int) (Math.random() * k_NUMBER) % mSeriesListInteger.size();
        } while (mSeriesNumber == tRandom);

        //値の更新
        this.setSeriesNumber(tRandom);
        this.setNumberInLine(0);
        this.setCurrentID(mSeriesListInteger.get(tRandom).get(0));
    }

    private void setCurrentID(int aNum) {
        mCurrentID = mIDMap.get(aNum);
    }

    private void setNumberInLine(int aNum) {
        mNumberInLine = aNum;
    }

    private void setSeriesNumber(int aNum) {
        mSeriesNumber = aNum;
    }

    public String getCurrentTarget() {
        return mCurrentID;
    }

    public boolean isLast() {
        if (mNumberInLine == mSeriesListInteger.get(0).size()) {
            return true;
        }

        return false;
    }

    public void setNextTarget() {
        mNumberInLine++;
        int tListSize = mSeriesListInteger.get(mSeriesNumber).size();

        if (mNumberInLine > tListSize) {
            throw new IndexOutOfBoundsException();
        }
		System.out.println("mSn="+mSeriesNumber+" mNil="+mNumberInLine+
		" mg1="+mSeriesListInteger.size()+" mg2="+mSeriesListInteger.get(mSeriesNumber).size());
        int tNumber = mSeriesListInteger.get(mSeriesNumber).get(mNumberInLine);
        mCurrentID = mIDMap.get(tNumber);
    }


    /**
     * 現在着目してるGoalsのリストを返す
     *
     * @return これを返してしまうと結構Goalsの存在意義が怪しくなる
     */
    public String[] getCurrentList() {
        ArrayList<Integer> tSeries = mSeriesListInteger.get(mSeriesNumber);
        String[] tString = new String[tSeries.size()];

        for (int i = 0; i < tSeries.size(); i++) {
            tString[i] = mIDMap.get(tSeries.get(i));
        }
        return tString;
    }

    /**
     * 指定された数字の順列の総組み合わせを返す
     *
     * @param aNumber
     * @return 再帰を用いた実装で、オーバーヘッド大きいので変更を加えたいところ
     */
    private static ArrayList<ArrayList<Integer>> permutation(Integer aNumber) {
        int tNumber = aNumber;

        if (tNumber == 0) {
            ArrayList<Integer> tList = new ArrayList<>();
            ArrayList<ArrayList<Integer>> tNullList = new ArrayList<>();
            tNullList.add(tList);
            return tNullList;
        }

        ArrayList<ArrayList<Integer>> results = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tPrevResults = permutation(tNumber - 1);

        for (ArrayList<Integer> tPrev : tPrevResults) {
            for (int i = 0; i < aNumber; ++i) {
                ArrayList<Integer> tBuf = new ArrayList<>(tPrev);
                tBuf.add(i, tNumber);
                results.add(new ArrayList<>(tBuf));
            }
        }

        return results;
    }

}