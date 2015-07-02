package com.unarin.cordova.beacon;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
//import android.support.v4.app.NotificationCompat;

import org.altbeacon.beacon.*;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.service.BeaconService;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Tom on 01/06/2015.
 */
public class BackgroundBeaconService extends Service implements BootstrapNotifier, RangeNotifier {

	public BackgroundBeaconService() {
		super();
	}

	private BackgroundPowerSaver backgroundPowerSaver;
	private BeaconManager iBeaconManager;
	private ArrayList<RegionBootstrap> regionBootstraps;

	public void onCreate() {
		Log.d("com.unarin.cordova.beacon", "BACKGROUND: Creating BackgroundBeaconService.");
		super.onCreate();
		iBeaconManager = BeaconManager.getInstanceForApplication(this);
		iBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
		//iBeaconManager.setBackgroundMode(false);
		iBeaconManager.setBackgroundBetweenScanPeriod(20000);
		//iBeaconManager.setBackgroundScanPeriod(1000);
		// Simply constructing this class and holding a reference to it
		// enables auto battery saving of about 60%
		backgroundPowerSaver = new BackgroundPowerSaver(this.getApplicationContext());
		iBeaconManager.setDebug(true);
		iBeaconManager.setRangeNotifier(this);

		regionBootstraps = new ArrayList<RegionBootstrap>();
		//masterBeaconRegion.
		Region region = new Region("backgroundRegionBlispa", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), null, null);
		regionBootstraps.add(new RegionBootstrap(this, region));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion9", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("9"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion16", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("16"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion21", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("21"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion22", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("22"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion23", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("23"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion24", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("24"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion25", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("25"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion26", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("26"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion27", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("27"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion28", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("28"), null)));
		regionBootstraps.add(new RegionBootstrap(this, new Region("backgroundRegion29", Identifier.parse("02424C49-5350-4F00-9DBF-3F5307B1159A"), Identifier.parse("29"), null)));

		Log.d("com.unarin.cordova.beacon", "BACKGROUND: Created RegionBootstrap in BackgroundBeaconService.");
	}

	public void onDestroy(){
		Log.d("com.unarin.cordova.beacon", "Destroying BackgroundBeaconService");
	}

	@Override
	public void didEnterRegion(Region region) {
		//sendNotification();
		Log.d("com.unarin.cordova.beacon", "BackgroundBeaconService.didEnterRegion called!");
	}

	@Override
	public void didExitRegion(Region region) {
		Log.d("com.unarin.cordova.beacon", "BackgroundBeaconService.didExitRegion called!");
	}

	//didRangeBeaconsInRegion used for when we initate a range after entering/exiting a region to get the exact beacons we can see (rather than just a region).
	@Override
	public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region){
		Log.d("com.unarin.cordova.beacon", "BackgroundBeaconService.didRangeBeaconsInRegion called!");
	}

	@Override
	public void didDetermineStateForRegion(int i, Region region) {
		Log.d("com.unarin.cordova.beacon", "BackgroundBeaconService.didDetermineStateForRegion called!");
	}

	@Override
	public Context getApplicationContext() {
		return this.getApplication().getApplicationContext();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*private void sendNotification() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle("Beacon Reference Application")
				.setContentText("An beacon is nearby.");

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addNextIntent(new Intent(this, com.ionicframework.TimeHunter525983.CordovaApp.class));
		PendingIntent resultPendingIntent =
			stackBuilder.getPendingIntent(
				0,
				PendingIntent.FLAG_UPDATE_CURRENT
			);
		builder.setContentIntent(resultPendingIntent);
		NotificationManager notificationManager =
			(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, builder.build());
	}*/
}