package com.unarin.cordova.beacon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;

import com.blispa.womad.R;

/**
 * Tom's Notes as of 3rd July 2015.
 * Problems as stands:
 * - The main activity can't be started in the background without showing it on the screen. This is why we need a background activity with Theme="NoDisplay" in the first place.
 * - The backgroundBeaconCordovaActivity can take control of the ranging notifications meaning the main activity doesn't recieve them (I think).
 * - When backgroundBeaconCordovaActivity starts it's not visible, but still blocks the screen and makes icons unclickable until the home button is pressed.
 * - The two activities would have to work together to prevent duplicate notifications.
 * - When destroyed, the background activity still exists (you can see it in chrome://inspect/#devices from the laptop) and javascript still fires etc.
 * - onNewIntent is not fired when the activity is already running (eg. the second time we enter a region).
 * - onPause() gets called multiple times (for multiple regions?) making the BackgroundPowerSaver think there are -ve numbers of processes in the foreground, so it probably would need help to switch to ForegroundMode again (and do bluetooth pings more regularly) if the app was opened by the user.
 *
 * Rather than fixing all of these I'm going to try a different (native) approach.
 */
public class BackgroundBeaconCordovaActivity extends Activity implements CordovaInterface {

	private CordovaWebView cordova_webview;
	private WindowManager windowManager;
	private String TAG = "com.unarin.BackgroundBeaconCordovaActivity";
	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	// Android Activity Life-cycle events
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 0;
		params.width = 0;
		params.height = 0;

		//LinearLayout view = new LinearLayout(this);
		//view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

		cordova_webview = new CordovaWebView(this);
		cordova_webview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		//view.addView(wv);
		cordova_webview.loadUrl("file:///android_asset/www/index.html?backgroundBeacon=true");

		windowManager.addView(cordova_webview, params);

		// 30 seconds coundowntimer
		/*new CountDownTimer(10000, 1000) {

			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				Log.d(TAG, "CountDownTimer finished");
				onPause();

				//windowManager.removeView(cordova_webview);
				//windowManager = null;
				//onDestroy();
				//finish(); // finish Activity.
			}
		}.start();*/


		//try{
			//Thread.sleep(10000);
			//cordova_webview.destroy();
			//onDestroy();
		/*}
		catch( InterruptedException e ){
			e.printStackTrace();
		}*/

		/*
		setContentView(R.layout.cordova_layout);
		cordova_webview = (CordovaWebView) findViewById(R.id.cordova_web_view);
		// Config.init(this);
		String url = "file:///android_asset/www/index.html?backgroundBeacon=true";
		cordova_webview.loadUrl(url);

		cordova_webview.destroy();
		onDestroy();*/
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		//finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");

		/*new CountDownTimer(10000, 1000) {
			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				Log.d(TAG, "Resumed CountDownTimer finished");
				onPause();
			}
		}.start();*/
	}

	@Override
	protected void onNewIntent(Intent intent){
		Log.d(TAG, "onNewIntent");
		onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (cordova_webview != null) {
			cordova_webview.setWebViewClient(null);
			cordova_webview.setWebChromeClient(null);
			cordova_webview.pauseTimers();
			cordova_webview.clearHistory();
			cordova_webview.loadUrl("javascript:try{cordova.require('cordova/channel').onDestroy.fire();}catch(e){console.log('exception firing destroy event from native');};");
			cordova_webview.loadUrl("file:///android_asset/www/exitNow.html");
			cordova_webview.loadUrl("about:blank");
			try{
				cordova_webview.handleDestroy();
			}
			catch( IllegalArgumentException e ){

			}
			cordova_webview = null;
		}
	}

	// Cordova Interface Events: see
	// http://www.infil00p.org/advanced-tutorial-using-cordovawebview-on-android/
	// for more details
	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	@Override
	public Object onMessage(String message, Object obj) {
		Log.d(TAG, message);
		if (message.equalsIgnoreCase("exit")) {
			super.finish();
		}
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {
		Log.d(TAG, "setActivityResultCallback is unimplemented");
	}

	@Override
	public void startActivityForResult(CordovaPlugin cordovaPlugin,
									   Intent intent, int resultCode) {
		Log.d(TAG, "startActivityForResult is unimplemented");
	}
}