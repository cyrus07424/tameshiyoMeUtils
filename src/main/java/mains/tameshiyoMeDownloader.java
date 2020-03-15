package mains;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import constants.Configurations;
import utils.SeleniumHelper;

/**
 * tameshiyo.meダウンロード.
 *
 * @author cyrus
 */
public class tameshiyoMeDownloader {

	/**
	 * 使用するURL.
	 */
	private static final String URL = "https://impress.tameshiyo.me/9784295002560";

	/**
	 * 保存先ディレクトリ.
	 */
	private static final File SAVE_DIRECTORY = new File("download/9784295002560");

	/**
	 * 初期ページ.
	 */
	private static final int INITIAL_PAGE = 375;

	/**
	 * main.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		crawlMain(URL, SAVE_DIRECTORY, INITIAL_PAGE);
	}

	/**
	 * メイン処理.
	 *
	 * @param url
	 * @param saveDirectory
	 */
	protected static void crawlMain(String url, File saveDirectory) {
		crawlMain(url, saveDirectory, 1);
	}

	/**
	 * メイン処理.
	 *
	 * @param url
	 * @param saveDirectory
	 * @param initialPage
	 */
	protected static void crawlMain(String url, File saveDirectory, int initialPage) {
		// 保存先ディレクトリを作成
		if (!saveDirectory.exists()) {
			saveDirectory.mkdirs();
		}

		// WebDriver
		WebDriver webDriver = null;
		try {
			// WebDriverを取得
			webDriver = SeleniumHelper.getWebDriver();

			// ページ番号(クエリパラメータ部分)
			int page = initialPage;
			if (page % 2 == 0) {
				page--;
				if (page < 0) {
					page = 1;
				}
			}

			// 画面を表示
			webDriver.get(getPagedUrl(url, page));
			SeleniumHelper.waitForBrowserToLoadCompletely(webDriver);

			while (true) {
				System.out.println("page : " + page);
				int loopCount = 0;
				while (loopCount < Configurations.MAX_LOOP_COUNT) {
					loopCount++;
					boolean processed = false;

					// 画像一覧を取得
					List<WebElement> pageImgList = webDriver
							.findElements(By.cssSelector("div.page_frame.front_side_pnl div.page_pnl img.page_img"));

					// 全ての画像に対して実行
					for (int i = 0; i < pageImgList.size(); i++) {
						String src = pageImgList.get(i).getAttribute("src");
						processed |= save(saveDirectory, src, page - 1 + i);
					}

					if (processed) {
						break;
					} else {
						// リロード
						webDriver.navigate().refresh();
						SeleniumHelper.waitForBrowserToLoadCompletely(webDriver);
					}
				}

				// ページ番号を進める
				page += 2;

				// 次のページを開く
				try {
					new WebDriverWait(webDriver, Duration.ofSeconds(10))
							.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#overlay_filter2")));
				} catch (Exception e) {
					// NOP
				}
				WebElement bookFrame = webDriver.findElement(By.cssSelector("#book_frame"));
				new WebDriverWait(webDriver, Duration.ofSeconds(10))
						.until(ExpectedConditions.elementToBeClickable(bookFrame));
				bookFrame.click();
				SeleniumHelper.waitForBrowserToLoadCompletely(webDriver);

				try {
					// URLを確認
					new WebDriverWait(webDriver, Duration.ofSeconds(10))
							.until(ExpectedConditions.urlToBe(getPagedUrl(url, page)));
				} catch (Exception e) {
					// 次のページを開く
					webDriver.get(getPagedUrl(url, page));
					SeleniumHelper.waitForBrowserToLoadCompletely(webDriver);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// WebDriverを終了
			if (webDriver != null) {
				webDriver.quit();
			}
		}
	}

	/**
	 * 画像を保存.
	 *
	 * @param saveDirectory
	 * @param src
	 * @param page
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return
	 */
	private static boolean save(File saveDirectory, String src, int page) throws FileNotFoundException, IOException {
		System.out.println("src : " + src);
		if (src.startsWith("data:") && src.split(",").length == 2) {
			String data = src.split(",")[1];
			byte[] byteData = Base64.getDecoder().decode(data);
			File file = new File(saveDirectory, String.format("%03d.jpg", page));
			IOUtils.write(byteData, new FileOutputStream(file));
			return true;
		}
		return false;
	}

	/**
	 * ページ番号付きURLを取得.
	 *
	 * @param url
	 * @param page
	 */
	private static String getPagedUrl(String url, int page) {
		return String.format("%s?page=%d", url, page);
	}
}