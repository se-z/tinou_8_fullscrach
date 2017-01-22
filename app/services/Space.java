package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by seijihagawa on 2017/01/12.
 */

/**
 * fix関数使った記憶がない
 */
public class Space implements Cloneable {
    private HashMap<String, Position> mBlocks;//ブロックの座標
    private HashMap<String, int[]> mSpaceSize;//座標空間の広さ
    private HashMap<Integer, Integer> mXDepth;//穴の位置と深さ
    private ArrayList<String> mFixedBlocks;
    private HashMap<String, Boolean> mUpwardFlag; //上にブロックが乗っているかのフラグ

    private String mMovingID;

    public Space(int aMinX, int aMaxX, int aMinY, int aMaxY) {
        mSpaceSize = new HashMap<String, int[]>();
        int tXRange[] = {aMinX, aMaxX};
        int tYRange[] = {aMinY, aMaxY};
        mSpaceSize.put("x", tXRange);
        mSpaceSize.put("y", tYRange);

        mBlocks = new HashMap<String, Position>();
        mXDepth = new HashMap<Integer, Integer>();
        mFixedBlocks = new ArrayList<String>();
        mUpwardFlag = new HashMap<String, Boolean>();

        for (int i = aMinX; i <= aMaxX; i++) {
            mXDepth.put(i, 0);
        }
    }

	public Space(Space origin){
		mBlocks = new HashMap<String, Position>(origin.mBlocks);
		mSpaceSize = new HashMap<String, int[]>(origin.mSpaceSize);
		mXDepth = new HashMap<Integer, Integer>(origin.mXDepth);
		mFixedBlocks = new ArrayList<String>();
        mUpwardFlag = new HashMap<String, Boolean>();
	}
	
    private class Position {//座標の情報を持つクラス
        private final int mX;
        private final int mY;

        Position(int aX, int aY) {
            mX = aX;
            mY = aY;
        }

        public int getX() {
            return mX;
        }

        public int getY() {
            return mY;
        }
    }


    /**
     * 新規登録のためのadd関数
     * 新規登録のためにsetBlockCloneSpace()は使わないこと
     * setBlockCloneSpace()はblock移動後に差分更新のみに使う
     *
     * @param aID
     * @param aX
     * @param aY  blockをaddできることを前提としているので、ありえない配置でもaddできるという問題点はある
     *            kaiくんのコードとすり合わせができているか確認する
     */

    public void addBlock(String aID, int aX, int aY) throws IllegalArgumentException {

        if (aX < mSpaceSize.get("x")[0] || aX > mSpaceSize.get("x")[1]
                || aY < mSpaceSize.get("y")[0] || aY > mSpaceSize.get("y")[1]) {
            throw new IllegalArgumentException();
        }

        Position tPosition = new Position(aX, aY);
        mBlocks.put(aID, tPosition);
        mUpwardFlag.put(aID, false);

        String tDownward = getBlockID(tPosition.getX(), tPosition.getY() - 1);

        if (!Objects.equals(tDownward, null)) {
            mUpwardFlag.put(tDownward, true);
        }

        if (aY - 1 < 0 && !beHole(aX, aY)) {
            dig(aX, aY);
        }
    }

    /**
     * 系列を取得するためにIDを登録する
     * <p>
     * 新たに、TargetIDをもたせたのでバグってないかcheck出来てない
     */
    public void setMovingID(String aID) {
        mMovingID = aID;
    }

    public String getMovingID() {
        return mMovingID;
    }

    /**
     * 空間が成立しているかどうかをcheckする関数
     * <p>
     * spaceの範囲外にblockがあるかどうかのcheckをここで行うかどうかは保留
     * 上にblockが乗らないblockの上に、何かblockが乗っていてもtrueが返却される
     * <p>
     * 要test
     */
    public boolean check() {
        for (HashMap.Entry<String, Position> tBlock : mBlocks.entrySet()) {
            if (tBlock.getValue().mY > 0) {
                String tID = this.getBlockID(tBlock.getValue().getX(), tBlock.getValue().getY() - 1);
                if (Objects.equals(tID, null)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 指定した座標にあるブロックのIDを返す.
     * ブロックがなければnullを返す
     *
     * @param aX
     * @param aY
     * @return nullであるかどうかの比較はObjects.equals(o1, o2):boolean で比較できる
     */
    public String getBlockID(int aX, int aY) {
        for (Iterator<String> i = mBlocks.keySet().iterator(); i.hasNext(); ) {
            String tKey = i.next();
            Position tPosition = mBlocks.get(tKey);
            if (tPosition.getX() == aX && tPosition.getY() == aY)
                return tKey;
        }
        return null;
    }


    /**
     * 指定したブロックの座標を返す.
     *
     * @param aID
     * @return
     */
    public int[] getPosition(String aID) {
        System.out.println(aID);
        System.out.println();
        Position tPosition = mBlocks.get(aID);
        int[] tXY = {tPosition.getX(), tPosition.getY()};
        return tXY;
    }

    /**
     * 指定したブロックの上に何も載っていなければtrue.
     *
     * @param aID
     * @return
     */
    public boolean isClear(String aID) {
        return mUpwardFlag.get(aID);
    }

    /**
     * ブロックを固定する
     *
     * @param aID 固定するブロックのID
     */
    public void fix(String aID) {
        mFixedBlocks.add(aID);
    }

//    /**
//     * ブロックの固定を解放する
//     *
//     * @param aID
//     */
//    public void unfix(String aID) {
//        mFixedBlocks.remove(aID);
//    }

    /**
     * 指定した座標とそれよりも上の空間が穴であることを保証する
     * そこにbolockがあるかどうかは問わない
     *
     * @param aX
     * @param aY
     * @return
     */
    private void dig(int aX, int aY) {
        if (aY >= -1 * mXDepth.get(aX)) {
            return;
        }

        mXDepth.remove(aX);
        mXDepth.put(aX, -1 * aY);
    }


    /**
     * 指定した座標が穴として掘られているかどうかを返す
     *
     * @param aX
     * @param aY
     * @return blockはないことが前提でこの関数は呼ばれる
     */
    private boolean beHole(int aX, int aY) {
        int tDepth = mXDepth.get(aX);
        if (tDepth == 0 || -1 * tDepth > aY) {
            return false;
        }
        return true;
    }


    /**
     * 指定した座標の一番上にあるブロックの上の座標を返す,
     * ブロックがなければ地面の高さを返す.
     *
     * @param aX
     * @return 面の凹凸を捉えているイメージ
     */
    public int getTopY(int aX) {
        for (Iterator<String> i = mBlocks.keySet().iterator(); i.hasNext(); ) {
            String tID = i.next();
            Position tPosition = mBlocks.get(tID);
            if (tPosition.getX() != aX) {
                continue;
            }
            if (mUpwardFlag.get(tID)) {
                continue;
            }

            return tPosition.getY() + 1;
        }
        return -1 * mXDepth.get(aX);
    }

    /**
     * 指定したx座標の一番上のblockのIDを返却する
     *
     * @param aX
     * @return Blockが存在しない場合はnullを返す
     * ヌルポに注意すること
     * nullであるかどうかの比較はObjects.equals(o1, o2):boolean で比較できる
     */
    public String getTopBlockID(int aX) {
        for (Iterator<String> i = mBlocks.keySet().iterator(); i.hasNext(); ) {
            String tID = i.next();
            Position tPosition = mBlocks.get(tID);
            if (tPosition.getX() != aX) {
                continue;
            }

            if (mUpwardFlag.get(tID)) {
                continue;
            }

            return tID;
        }

        return null;
    }


    /**
     * 注:::Blockをクローンした時のみ使って良い関数
     * blockを移動させるためには使わない
     * 新しいspaceを登録するのに使用する
     * クローンされた後に、space関数を更新する
     *
     * @param aID
     * @param aX
     * @param aY  コピー元のスペースを謝るとおそらくバグる
     *            digがバグりそうで怖い
     *            <p>
     *            未テスト
     */
    public void setBlockCloneSpace(String aID, int aX, int aY) {

        Position tOldPosition = mBlocks.get(aID);
        String tDownward = getBlockID(tOldPosition.getX(), tOldPosition.getY() - 1);

        if (!Objects.equals(tDownward, null)) {
            mUpwardFlag.put(tDownward, false);
        }

        mBlocks.remove(aID);
        mBlocks.put(aID, new Position(aX, aY));
        mUpwardFlag.remove(aID);
        mUpwardFlag.put(aID, false);

        String tNewDownward = getBlockID(aX, aY - 1);
        if (!Objects.equals(tNewDownward, null)) {
            mUpwardFlag.put(tNewDownward, true);
            return;
        }

        if (aY < 0 && !beHole(aX, aY)) {
            dig(aX, aY);
        }

    }


//    /**
//     * 現在の状態を複製して返す.
//     *
//     * @return 別のオブジェクトが生成されていることは確認済み
//     * 未テスト
//     */
//    public Space cloneSpace() {
//
//        int[] tXRange = mSpaceSize.get("x");
//        int[] tYRange = mSpaceSize.get("y");
//        Space tClone = new Space(tXRange[0], tXRange[1], tYRange[0], tYRange[1]);
//        for (String tID : mBlocks.keySet()) {
//            Position tPosition = mBlocks.get(tID);
//            int tX = tPosition.getX();
//            int tY = tPosition.getY();
//            tClone.addBlock(tID, tPosition.getX(), tPosition.getY());
//            //Position tNew = new Position(tPosition.getX(), tPosition.getY());
//            //tClone.mBlocks.put(tID, tNew);
//        }
//        try {
//            for (Integer tX : mXDepth.keySet()) {
//                Integer tY = mXDepth.get(tX);
//                tClone.mXDepth.put(tX.clone(), tY.clone());
//            }
//            for (String tID : mFixedBlocks) {
//                String tBlock = tID;
//                tClone.mFixedBlocks.add(tBlock);
//            }
//            for (String tID : mUpwardFlag.keySet()) {
//                tClone.mUpwardFlag.put(tID, mUpwardFlag.get(tID));
//            }
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//
//        return tClone;
//    }
//

    @Override
    public Space clone() {
        System.out.println("clone");
        try {
            Space tClone = (Space) super.clone();
            tClone.mBlocks=(HashMap<String, Position>) this.mBlocks.clone();//ブロックの座標
            tClone.mSpaceSize=(HashMap<String, int[]>) this.mSpaceSize.clone();//座標空間の広さ
            tClone.mXDepth=(HashMap<Integer, Integer>) this.mXDepth.clone();//穴の位置と深さ
            tClone.mFixedBlocks=(ArrayList<String>) this.mFixedBlocks.clone();
            tClone.mUpwardFlag=(HashMap<String, Boolean>) this.mUpwardFlag.clone(); //上にブロックが乗っているかのフラグ
            return tClone;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }


    /**
     * 表示を行う
     * <p>
     * 前にyoshinoが実装した部分。一応残しておく
     **/
    public void show() {
        System.out.print("Blocks=");
        for (HashMap.Entry<String, Position> e : mBlocks.entrySet()) {
            System.out.print(e.getKey() + " :[ " + e.getValue().mX + "," + e.getValue().mY + "] ");
        }
        System.out.println("");
        System.out.print("space=");
        for (HashMap.Entry<String, int[]> e : mSpaceSize.entrySet()) {
            System.out.print(e.getKey() + " :[ " + e.getValue()[0] + "," + e.getValue()[1] + "] ");
        }
        System.out.println("");
        System.out.println("Depth=" + mXDepth);
    }


    public boolean isSame(Space aComp) {
        for (HashMap.Entry<String, Position> tCompBlock : aComp.mBlocks.entrySet()) {

            if (mBlocks.get(tCompBlock.getKey()).getX() != tCompBlock.getValue().getX()) {
                return false;
            }

            if (mBlocks.get(tCompBlock.getKey()).getY() != tCompBlock.getValue().getY()) {
                return false;
            }
        }
        return true;
    }
}
