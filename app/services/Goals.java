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
    private ArrayList<ArrayList<Integer>> mSeriesListInteger; //これ初期化しなくていい気がする
    private HashMap<Integer, String> mIDMap = new HashMap<>();
    private final int k_NUMBER = 10000;

    public Goals(String[] aIDs) {
        mIDs = aIDs;
        //Permutation tPerm = new Permutation();
        //tPerm.doPermutation(mIDs.length);
        //mSeriesListInteger = tPerm.getNumberList();

        mSeriesListInteger = permutation(mIDs.length);
        //これ何やってんだ？
        for (int i = 1; i <= mIDs.length; i++) {
            mIDMap.put(i, mIDs[i - 1]);
        }

        setSeriesNumber();
        mNumberInLine = 0;

        //test
        System.out.println(mSeriesListInteger.size());
        for (int i = 0; i < mSeriesListInteger.size(); i++) {
            System.out.println(mSeriesListInteger.get(i));
        }
    }


    /**
     * 探索を行う系列をランダムに決定
     */
    public void randomSet() {
        int tRandom = (int) (Math.random() * k_NUMBER) % mSeriesListInteger.size();
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
        int tNumber = mSeriesListInteger.get(mSeriesNumber).get(mNumberInLine);
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
        int tNumber = mSeriesListInteger.get(mSeriesNumber).get(mNumberInLine);
        mCurrentID = mIDMap.get(tNumber);
    }

    /**
     * @return いるかどうかわからんけど一応。
     */
    public String getNextTarget() {
        setNextTarget();
        return getCurrentTarget();
    }


    private static ArrayList<ArrayList<Integer>> permutation(Integer aNumber) {
        int tN = aNumber;

        if (tN < 0)
            return null;

        ArrayList<ArrayList<Integer>> results = new ArrayList<>();
        if (tN == 0) {
            ArrayList<Integer> tList = new ArrayList<>();
            results.add(tList);
            return results;
        }

        ArrayList<ArrayList<Integer>> tPrevResults = permutation(tN - 1);
        for (ArrayList<Integer> tPerm : tPrevResults) {
            for (int i = 0; i <= tN; ++i) {    // n を加える位置についてのループ
                tPerm.add(i, tN);
                results.add(new ArrayList<>(tPerm));
                tPerm.remove(tN);
            }
            tPerm.clear();     // 全要素を削除しておく これいる？
        }
        tPrevResults.clear();     // 全要素を削除しておく これいる？

        return results;
    }
}