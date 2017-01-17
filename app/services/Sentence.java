/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文節のリストから成る1文．文節間の係り受け構造を保持するクラス．
 */
public class Sentence extends ArrayList<Chunk> {

	Chunk head;

	static Process caboChaPrc;
	static PrintWriter caboChaOut;
	static BufferedReader caboChaIn;
	//static String cabochaCmd = "C:\\Program Files (x86)\\CaboCha\\bin\\cabocha -f1";
        static String cabochaCmd = "C:\\play\\hello\\public\\CaboCha-bin\\cabocha -f1";
	//static String encoding = "EUC-JP";
        static String encoding = "Shift_JIS";
        
	static String knowledge = "私はかもめです。タマは猫です。あの物体は魚です。私とタマは敵同士です。";

	// このへんにmainメソッドを追加して下さい
	/**
	 * CaboChaの係り受け解析を試す例
	 */
	public static void main(String[] args) {
		//String text = "知ってた？隣のお客さんはたくさん柿を食べるって。"; // 解析対象のテキスト
		String text = "猫は誰ですか？";
		System.out.println("解析対象のテキスト: " + text);

		// 対象テキストを文に分割した上でCaboChaに渡し，解析結果を受け取る．
		List<Sentence> sentences = Sentence.parseTweet(text);
		System.out.println("係り受け解析結果:\n" + sentences);

		for (Sentence sentence : sentences) { // 文を1つずつ処理するループ
			// 主辞（最後の文節）を取得
			Chunk headChunk = sentence.getHeadChunk();
			// 主辞に係る文節のリストを返す
			List<Chunk> dependents = headChunk.getDependents();

			for (Chunk dependent : dependents) {
				// 文節dependentの主辞（最後の形態素）を取得
				Morpheme headMorph = dependent.getHeadMorpheme();

				// 助詞の「は」かどうか判定するif文
				if (headMorph.getPos().equals("助詞")
						&& headMorph.getSurface().equals("は")) {
					// 文節dependent内の助詞以外の形態素をつなげる
					String topic = "";
					for (int i = 0; i < dependent.size() - 1; i++) {
						topic += dependent.get(i).getSurface();
					}
					System.out.println("主辞に係るハ格: " + topic);
				}

				// 助詞の「を」かどうか判定するif文
				if (headMorph.getPos().equals("助詞")
						&& headMorph.getSurface().equals("を")) {
					// 文節dependent内の助詞以外の形態素をつなげる
					String woframe = "";
					for (int i = 0; i < dependent.size() - 1; i++) {
						woframe += dependent.get(i).getSurface();
					}
					System.out.println("動作対象に係るヲ格: " + woframe);
				}

			}//for (Chunk dependent : dependents)
		}//for (Sentence sentence : sentences)

		for (Sentence sentence : sentences) { // 文を1つずつ処理するループ
			// 主辞（最後の文節）を取得
			Chunk headChunk = sentence.getHeadChunk();
			// 助詞の「の」を探す
			// 再帰メソッドfindNoFramesを呼び出す
			List<String> noFrames = findNOFrames(headChunk);

			// 見つかった「の」がつく語句をすべて表示
			for (String noFrame : noFrames) {
				System.out.println("助詞の「の」がついた語句:" + noFrame);
			}
		}
		
		Answer(text);
	}

	public static List<String> findNOFrames(Chunk chunk) {
		// 「の」がつく語句を記録するためのリスト
		List<String> noFrames = new ArrayList<String>();
		// 文節dependentの主辞（最後の形態素）を取得
		Morpheme headMorph = chunk.getHeadMorpheme();

		// 助詞の「の」かどうか判定するif文
		if (headMorph.getPos().equals("助詞")
				&& headMorph.getSurface().equals("の")) {
			// 文節dependent内の助詞以外の形態素をつなげる
			String noframe = "";
			for (int i = 0; i < chunk.size() - 1; i++) {
				noframe += chunk.get(i).getSurface();
			}
			// リストnoFramesに文字列noFrameを追加
			noFrames.add(noframe);
		}

		// 再帰処理
		List<Chunk> dependents = chunk.getDependents();
		for (Chunk dependent : dependents) {
			List<String> depNOframe = findNOFrames(dependent);// chunkに係る文節中の「の」がつく語句を探す
			noFrames.addAll(depNOframe);// 語句を全て追加
		}
		return (noFrames);
	}
	
	 
	
	public static void Answer(String question) {
		Pattern pat1 = Pattern.compile("(.+)は誰です(.+)?");//正規表現
		Matcher mat = pat1.matcher(question);
        if (mat.find()) {  // もしパターンにマッチすれば
            String asked = mat.group(1); // 正規表現の1番目の()内にマッチする部分を取得
            Pattern newPat = Pattern.compile("(.+)は" + asked + "です。");
            Matcher newmat = newPat.matcher(knowledge);
            if(newmat.find()){ // もしパターンにマッチすれば
            	String answer = newmat.group(1);
            	System.out.println(asked+"は"+answer+"です。");
            }
            String resp = asked+"について聞いています";  // 応答文生成
            System.out.println(resp);    // ターミナルに表示
        }
		// 対象テキストを文に分割した上でCaboChaに渡し，解析結果を受け取る．
		List<Sentence> sentences = Sentence.parseTweet(question);
		//System.out.println("係り受け解析結果:\n" + sentences);
		for (Sentence sentence : sentences) { // 文を1つずつ処理するループ
			// 主辞（最後の文節）を取得
			Chunk headChunk = sentence.getHeadChunk();
			//System.out.println("主辞;"+headChunk);
			// 主辞に係る文節のリストを返す
			List<Chunk> dependents = headChunk.getDependents();
			Chunk preDependent = null;
			for (Chunk dependent : dependents) {
				// 文節dependentの最初を入手
				Morpheme headMorph = dependent.get(0);
				//System.out.println(dependent);
				// 名詞の「誰」かどうか判定するif文
				if (headMorph.getPos().equals("名詞")
						&& headMorph.getSurface().equals("誰")) {
					System.out.println("「誰」を見つけました");
					if(preDependent != null){
						Morpheme preheadMorph = preDependent.getHeadMorpheme();
						// 助詞の「は」かどうか判定するif文
						if (preheadMorph.getPos().equals("助詞")
								&& preheadMorph.getSurface().equals("は")) {
							// 文節preDependent内の助詞以外の形態素をつなげる
							String topic = "";
							for (int i = 0; i < preDependent.size() - 1; i++) {
								topic += preDependent.get(i).getSurface();
							}
							System.out.println(topic+"について聞いています");
						}
					}
				}
				
				
				preDependent = dependent;
			}
			
		}
	}

	public Sentence() {
		super();
	}

	public Sentence(List<Chunk> chunks) {
		super(chunks);
		initDependency();
	}

	public void initDependency() {
		Iterator<Chunk> i = this.iterator();
		while (i.hasNext()) {
			Chunk chunk = i.next();
			int dependency = chunk.getDependency();
			if (dependency == -1) {
				head = chunk;
			} else {
				Chunk depChunk = this.get(dependency);
				depChunk.addDependentChunk(chunk);
				chunk.setDependencyChunk(depChunk);
			}
		}
	}

	/**
	 * 主辞の文節を返す
	 * 
	 * @return 主辞の文節
	 */
	public Chunk getHeadChunk() {
		return head;
	}

	/**
	 * 指定した品詞・原型の形態素を主辞に持つ文節を探して返す
	 * 
	 * @param pos
	 *            探しててる文節の主辞形態素の品詞
	 * @param baseform
	 *            探している文節の主辞形態素の原型
	 * @return 見つかった文節のリスト
	 */
	public List<Chunk> findChunkByHeads(String pos, String baseform) {
		List<Chunk> matches = new ArrayList<Chunk>();
		for (Iterator<Chunk> i = this.iterator(); i.hasNext();) {
			Chunk chunk = i.next();
			Morpheme head = chunk.getHeadMorpheme();
			if (pos.equals(head.getPos())
					&& baseform.equals(head.getBaseform())) {
				matches.add(chunk);
			}
		}
		return matches;
	}
	
	
	/**
	 * 動作主格の文節を返す
	 * 
	 * @return 動作主格の文節
	 */
	public Chunk getAgentCaseChunk() {
		// 主辞に係る文節の中からガ格の文節を探す
		Chunk cand = findChunkByHead(head.getDependents(), "助詞", "が");
		if (cand != null)
			return cand;
		// 主辞に係る文節の中からハ格の文節を探す
		cand = findChunkByHead(head.getDependents(), "助詞", "は");
		if (cand != null)
			return cand;
		// 全ての文節の中からガ格の文節を探す
		cand = findChunkByHead(this, "助詞", "が");
		if (cand != null)
			return cand;
		// 全ての文節の中からハ格の文節を探す
		cand = findChunkByHead(this, "助詞", "は");
		return cand;
	}

	/**
	 * 指定した品詞・原型の形態素を主辞に持つ文節を文節リストchunksから探して返す
	 * 
	 * @param chunks
	 *            文節リスト
	 * @param pos
	 *            探しててる文節の主辞形態素の品詞
	 * @param baseform
	 *            探している文節の主辞形態素の原型
	 * @return 見つかった文節
	 */
	public Chunk findChunkByHead(List<Chunk> chunks, String pos, String baseform) {
		for (Iterator<Chunk> i = chunks.iterator(); i.hasNext();) {
			Chunk chunk = i.next();
			Morpheme head = chunk.getHeadMorpheme();
			if (pos.equals(head.getPos())
					&& baseform.equals(head.getBaseform())) {
				return chunk;
			}
		}
		return null;
	}

	/**
	 * この文に含まれる文節間の係り受け構造を表すXML風の文字列を返す
	 * 
	 * @return XML風の文字列
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<文 主辞=\"" + head.getId() + "\">\n");
		for (Iterator<Chunk> i = iterator(); i.hasNext();) {
			sb.append(i.next().toString());
			sb.append("\n");
		}
		sb.append("</文>");
		return sb.toString();
	}

	static void startCaboCha() {
		if (caboChaPrc != null) {
			caboChaPrc.destroy();
		}
		try {
			caboChaPrc = Runtime.getRuntime().exec(cabochaCmd);
			caboChaOut = new PrintWriter(new OutputStreamWriter(
					caboChaPrc.getOutputStream(), encoding));
			caboChaIn = new BufferedReader(new InputStreamReader(
					caboChaPrc.getInputStream(), encoding));
		} catch (IOException ex) {
			System.err.println("係り受け解析器CaboChaを起動できませんでした");
			System.exit(-1);
		}
	}

	/**
	 * 文に区切るためのセパレータ
	 */
	static List separators = Arrays.asList(new String[] { "。", "！", "!", "？",
			"?", "．", "\n" });

	/**
	 * 文に区切る
	 * 
	 * @param text
	 *            複数の文を含む可能性のあるString
	 * @return 区切られた文（String）のリスト
	 */
	static List<String> splitSentences(String text) {
		List<String> sentences = new ArrayList<String>();
		while (text.length() > 0) {
			int i = -1;
			for (int k = 0; k < separators.size(); k++) {
				String sep = (String) separators.get(k);
				int j = text.indexOf(sep);
				if (j >= 0 && (i < 0 || j < i)) {
					i = j;
				}
			}
			if (i < 0 || i == text.length() - 1) {
				sentences.add(text);
				text = "";
			} else {
				sentences.add(text.substring(0, i + 1));
				text = text.substring(i + 1);
			}
		}
		return sentences;
	}

	/**
	 * 対象テキストを文に分割した上でCaboChaに渡し，解析結果を取得
	 * 
	 * @param tweet
	 *            解析対象のテキスト（ツイート）
	 * @return Sentenceオブジェクトのリスト
	 */
	static List<Sentence> parseTweet(String tweet) {
		List<String> sentenceStrs = splitSentences(tweet); // 文に分割
		List<Sentence> sentences = new ArrayList<Sentence>();
		for (String sentenceStr : sentenceStrs) {
			Sentence sentence = parse(sentenceStr); // 1文ずつ解析
			sentences.add(sentence);
		}
		return sentences;
	}

	/**
	 * 文を係り受け解析
	 * 
	 * @param sentenceStr
	 *            日本語文の文字列
	 * @return このバージョンではnull
	 */
	static Sentence parse(String sentenceStr) {
		if (caboChaOut == null) {
			startCaboCha(); // CaboChaが実行されていない場合は実行する
		}
		caboChaOut.println(sentenceStr); // CaboChaに文を渡す
		caboChaOut.flush();

		Sentence sentence = new Sentence(); // 新しいSentenceオブジェクト(中身は空)
		Chunk chunk = null;

		try {
			// CaboChaから解析結果を受け取るfor文．変数lineに1行ずつ代入される
			for (String line = caboChaIn.readLine(); line != null; line = caboChaIn
					.readLine()) {
				// forループの中で文節(Chunkオブジェクト)や文(Sentenceオブジェクト)を作っていく
				if (line.equals("EOS")) {
					if (sentence.head == null) {
						sentence.head = chunk;
					}
					break;
				} else if (line.startsWith("*")) {
					chunk = new Chunk();
					sentence.add(chunk);
					String[] tokens = line.split(" ");
					int id = Integer.parseInt(tokens[1]);
					chunk.setId(id);
					if (tokens[2].endsWith("D")) {
						int dep = Integer.parseInt(tokens[2].substring(0,
								tokens[2].length() - 1));
						chunk.setDependency(dep);
						if (dep == -1) {
							sentence.head = chunk;
						}
					}
				} else {
					Morpheme morph = new Morpheme(line);
					chunk.addMorpheme(morph);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("CaboChaの係り受け解析に失敗しました: 「" + sentenceStr + "」");
		}
		sentence.initDependency(); // 係り受け関係を構築
		return sentence;
	}

}

