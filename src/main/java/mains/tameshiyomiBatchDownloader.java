package mains;

import java.io.File;

/**
 * tameshiyo.me一括ダウンロード.
 *
 * @author cyrus
 */
public class tameshiyomiBatchDownloader extends tameshiyoMeDownloader {

	/**
	 * 使用するベースURL.
	 */
	private static final String BASE_URL = "https://impress.tameshiyo.me/";

	/**
	 * ダウンロード対象の本ID一覧.
	 */
	private static final String[] ID_ARRAY = { "9784295002560",
			"9784295003200", "9784295003190", "9784295005620", "9784295003860", "9784295003850", "9784295005570",
			"9784295007670", "9784295005900", "9784295007830", "9784295005370", "9784295003680", "9784295003689",
			"9784295003320", "9784295007330", "9784295005470", "9784844334160", "9784295005500" };

	/**
	 * 保存先ベースディレクトリ.
	 */
	private static final File SAVE_BASE_DIRECTORY = new File("download/");

	/**
	 * main.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// 全てのダウンロード対象の本IDに対して実行
		for (String id : ID_ARRAY) {
			String url = BASE_URL + id;
			File saveDirectory = new File(SAVE_BASE_DIRECTORY, id);

			// メイン処理
			crawlMain(url, saveDirectory);
		}
	}
}