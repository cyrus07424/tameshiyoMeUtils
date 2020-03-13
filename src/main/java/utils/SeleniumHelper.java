package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import constants.Configurations;

/**
 * Seleniumヘルパー.
 *
 * @author cyrus
 */
public class SeleniumHelper {

	/**
	 * WebDriverを取得.
	 *
	 * @return
	 */
	public static WebDriver getWebDriver() {
		System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
				Configurations.CHROME_DRIVER_EXECUTABLE_PATH);

		// ChromeOptions
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setHeadless(Configurations.USE_HEADLESS_MODE);
		chromeOptions.addArguments("--disable-dev-shm-usage");
		chromeOptions.addArguments("--no-sandbox");

		// prefs
		Map<String, Object> chromePrefs = new HashMap<>();
		chromeOptions.setExperimentalOption("prefs", chromePrefs);

		// WebDriverを取得
		WebDriver driver = new ChromeDriver(chromeOptions);

		// タイムアウトを設定
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		return driver;
	}

	/**
	 * ページが完全に読み込まれるまで待機.<br>
	 * https://code-examples.net/ja/q/598b97
	 *
	 * @param webDriver
	 */
	public static void waitForBrowserToLoadCompletely(WebDriver webDriver) {
		String state;
		String oldstate;
		try {
			System.out.println("Waiting for browser loading to complete");
			int i = 0;
			while (i < 5) {
				Thread.sleep(1000);
				state = ((JavascriptExecutor) webDriver).executeScript("return document.readyState;").toString();
				System.out.println("." + Character.toUpperCase(state.charAt(0)) + ".");
				if (state.equals("interactive") || state.equals("loading")) {
					break;
				}

				// If browser in 'complete' state since last X seconds. Return.
				if (i == 1 && state.equals("complete")) {
					System.out.println("complete");
					return;
				}
				i++;
			}
			i = 0;
			oldstate = null;
			Thread.sleep(2000);

			// Now wait for state to become complete
			while (true) {
				state = ((JavascriptExecutor) webDriver).executeScript("return document.readyState;").toString();
				System.out.println("." + state.charAt(0) + ".");
				if (state.equals("complete")) {
					System.out.println("complete");
					break;
				}

				if (state.equals(oldstate)) {
					i++;
				} else {
					i = 0;
				}

				// If browser state is same (loading/interactive) since last 60
				// secs. Refresh the page.
				if (i == 15 && state.equals("loading")) {
					System.out.println("Browser in " + state + " state since last 60 secs. So refreshing browser.");
					webDriver.navigate().refresh();
					System.out.println("Waiting for browser loading to complete");
					i = 0;
				} else if (i == 6 && state.equals("interactive")) {
					System.out
							.println("Browser in " + state + " state since last 30 secs. So starting with execution.");
					return;
				}
				Thread.sleep(4000);
				oldstate = state;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}