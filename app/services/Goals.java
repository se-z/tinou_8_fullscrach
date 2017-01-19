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
 */
public class Goals {
    private String[] mIDs;
    private String mCurrentID;
    private int mNumberInLine;
    private int mSeriesNumber;
    private ArrayList<ArrayList<Integer>> mSeriesInteger; //これ初期化しなくていい気がする
    private HashMap<Integer, String> mIDMap = new HashMap<>();
    private final int k_NUMBER = 10000;

    public Goals(String[] aIDs) {
        mIDs = aIDs;
        Permutation tPerm = new Permutation();
        tPerm.permutation(mIDs.length);
        mSeriesInteger = tPerm.getNumberList();

        for (int i = 1; i <= mIDs.length; i++) {
            mIDMap.put(i, mIDs[i - 1]);
        }

        setSeriesNumber();
        mNumberInLine = 0;
    }


    /**
     * 探索を行う系列をランダムに決定
     */
    public void randomSet() {
        int tRandom = (int) (Math.random() * k_NUMBER) % mSeriesInteger.size();
        mSeriesNumber = tRandom;
        mNumberInLine = 0;
    }

    /**
     * どの系列からアプローチするかの決定則が実装されている
     * <p>
     * この決定則に自由度を持たせるなら、permutationでの実装は微妙
     */
    private void setSeriesNumber() {
        mSeriesNumber = 0;
    }

    public String getCurrentTarget() {
        return mCurrentID;
    }

    public void setNextTarget() {
        mNumberInLine++;
        int tNumber = mSeriesInteger.get(mSeriesNumber).get(mNumberInLine);
        mCurrentID = mIDMap.get(tNumber);
    }

    /**
     * 別の系列を呼び出す
     * <p>
     * 現在の系列で推論がうまくいかなった場合に読み出される
     */
    //系列の決め方ももう少し自由度をつける方が良い
    public void setNewSeiries() {
        mSeriesNumber++;
        mNumberInLine = 0;
        int tNumber = mSeriesInteger.get(mSeriesNumber).get(mNumberInLine);
        mCurrentID = mIDMap.get(tNumber);
    }

    /**
     * @return いるかどうかわからんけど一応。
     */
    public String getNextTarget() {
        setNextTarget();
        return getCurrentTarget();
    }


    /**
     * 順列の総数を求めるクラス(util)
     * <p>
     * <p>
     * utilとして外だしすべきかな
     * 内部実装が汚い
     * mPermutationListのスコープが広すぎる
     */
    private class Permutation {
        private int mNumber;
        private ArrayList<ArrayList<Integer>> mNumberList = new ArrayList<>();
        private ArrayList<Integer> mPermutationList = new ArrayList<>();

        Permutation() {
        }

        public void permutation(int aNumber) {
            ArrayList<Integer> tSource = createList(aNumber);
            arrangeRecursive(1, tSource);
        }


        /**
         * @param aCounter    再帰した回数を記録する mNumberと等しくなったら終了. 呼び出しは必ず1で初期化
         * @param aSourceList 再帰での実装はオーバーヘッドが多分大きいので、再帰を使わない実装にしたい
         */
        private void arrangeRecursive(int aCounter, ArrayList<Integer> aSourceList) {

            if (aCounter == mNumber) {
                mNumberList.add(mPermutationList);
                mPermutationList = new ArrayList<Integer>(); //これ、コンパイルしたらうまくいくんじゃない？
                return;
            }

            for (Integer tNum : aSourceList) {
                ArrayList<Integer> tSourceBuf = new ArrayList<>(aSourceList);
                mPermutationList.add(tNum);
                tSourceBuf.remove(tNum);
                arrangeRecursive(aCounter + 1, tSourceBuf);
            }

        }

        private ArrayList<Integer> createList(int aNumber) {
            ArrayList<Integer> tList = new ArrayList<>();
            for (int i = 1; i <= aNumber; i++) {
                tList.add(i);
            }

            return tList;
        }

        public ArrayList<ArrayList<Integer>> getNumberList() {
            return mNumberList;
        }

    }

}