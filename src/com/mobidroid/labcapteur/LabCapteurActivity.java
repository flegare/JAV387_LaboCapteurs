package com.mobidroid.labcapteur;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LabCapteurActivity extends Activity {
	
	private SensorManager mSensorManager;	
	private HashMap<SensorEventListener, Sensor> sensorsListnerMap;
	private static final String TAG = LabCapteurActivity.class.getName();
		
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
			
		
		
		//On obtient une instance du gestionnaire de capteur
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);       
        
        //On va tracker tout les sensor listener que l'on va cree (en même temps que la vue)
        sensorsListnerMap = new HashMap<SensorEventListener, Sensor>();
        
        //Ici on deposera toutes les vues
        LinearLayout sensorListLayout = (LinearLayout)findViewById(R.id.layoutSensorList);

        //On obtient la liste des senseurs disponible sur ce telephone
        List<Sensor> listOfSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        
        Log.d(TAG,"*******************************************");
        Log.d(TAG,"Found " + listOfSensors.size() + " sensors");
        Log.d(TAG,"*******************************************");
        
        for (Iterator<Sensor> iterator = listOfSensors.iterator(); iterator.hasNext();) {        	
        	Sensor sensor = (Sensor) iterator.next();
        	Log.d(TAG, "New sensor found: " + sensor);
			//Pour chaque capteur on va ajouter une layout avec ces info + valeurs + creer un listner
			sensorListLayout.addView(createSensorView(sensor));
		}                            
	}
	
	/**
	 * On cree une vue pour ce capteur
	 * @param sensor
	 * @return
	 */
	private View createSensorView(Sensor sensor) {
				
		String[] sInfo = {
				 "Sensor name: " + sensor.getName(),
				 "Type: " + readableType(sensor.getType()),		
				 "Vendor: " + sensor.getVendor(),				
				 "Version: " + sensor.getVersion(),		
				 "Minimum update delay: " + sensor.getMinDelay() + " ms",
				 "Maximum range: " + sensor.getMaximumRange(),
				 "Resolution : " + sensor.getResolution(),	
				 "Power usage cost: "+sensor.getPower() + " mA"
				 };
							
		LinearLayout sensorInfo = new LinearLayout(this);
		sensorInfo.setOrientation(LinearLayout.VERTICAL);
		sensorInfo.setBackgroundDrawable(getResources().getDrawable(R.drawable.appwidget_bg));
		
		for (int i = 0; i < sInfo.length; i++) {
			TextView tv = new TextView(this);
			tv.setText(sInfo[i]);
			sensorInfo.addView(tv);				
		}
		
		switch(sensor.getType()){
			case Sensor.TYPE_ACCELEROMETER:
				sensorInfo.addView(generic3valueSensorView(sensor, "m/s^2"));
				break;
			case Sensor.TYPE_LIGHT:
				sensorInfo.addView(generic1valueSensorView(sensor, "Light", "lux"));
				break;
			case Sensor.TYPE_PROXIMITY:
				sensorInfo.addView(generic1valueSensorView(sensor, "Distance", "cm"));
				break;
			case Sensor.TYPE_PRESSURE:
				sensorInfo.addView(generic1valueSensorView(sensor, "Pressure", "hpa"));
				break;
			case Sensor.TYPE_TEMPERATURE:
				sensorInfo.addView(generic1valueSensorView(sensor, "Temperature", "celcius"));
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				sensorInfo.addView(generic3valueSensorView(sensor, " uT (micro tesla)"));
				break;			
			case Sensor.TYPE_GYROSCOPE:
				sensorInfo.addView(generic3valueSensorView(sensor, " rad/sec (radian per second)"));
				break;						
			case Sensor.TYPE_GRAVITY:
				sensorInfo.addView(generic3valueSensorView(sensor, "m/s^2"));
				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:
				sensorInfo.addView(generic3valueSensorView(sensor, "m/s^2"));
				break;
		
			//FIXME: Complete with other sensors... inflate view from xml...
		}
		
		return sensorInfo;
	}
	
	private View generic1valueSensorView(Sensor s, final String label, final String unit){

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		final TextView accuracity = new TextView(this);
		final TextView value = new TextView(this);
				
		ll.addView(accuracity);
		ll.addView(value);
								
		//On cree un nouveau listener pour ce type de capteur
		SensorEventListener sel = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				accuracity.setText("Precision: " + event.accuracy);				
				value.setText(label + ": "+ event.values[0] + " " + unit);
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				accuracity.setText("Precision: " + accuracy);
			}
		};
		
		//On ajoute à la liste
		sensorsListnerMap.put(sel, s);				
		return ll;
	}
	
	private View generic3valueSensorView(Sensor s, final String unit){
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		final TextView accuracity = new TextView(this);
		final TextView x = new TextView(this);
		final TextView y = new TextView(this);
		final TextView z = new TextView(this);
		
		ll.addView(accuracity);
		ll.addView(x);
		ll.addView(y);
		ll.addView(z);
								
		//On cree un nouveau listener pour ce type de capteur
		SensorEventListener sel = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				accuracity.setText("Precision: " + event.accuracy);				
				x.setText("X: "+ event.values[0] + " " + unit);
				y.setText("Y: "+ event.values[1] + " " + unit);
				z.setText("Z: "+ event.values[2] + " " + unit);
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				accuracity.setText("Precision: " + accuracy);	
			}
		};
		
		//On ajoute à la liste
		sensorsListnerMap.put(sel, s);
				
		return ll;	
	}


	private String readableType(int type) {		
		switch(type){	
			case Sensor.TYPE_ACCELEROMETER: return "Accelerometer";
			case Sensor.TYPE_GRAVITY: return "Gravity sensor";
			case Sensor.TYPE_GYROSCOPE: return "Gyroscope";
			case Sensor.TYPE_LIGHT: return "Light sensor";
			case Sensor.TYPE_LINEAR_ACCELERATION: return "Linear accelerometer";
			case Sensor.TYPE_MAGNETIC_FIELD: return "Magnetic field sensor";
			case Sensor.TYPE_ORIENTATION: return "Orientation sensor";
			case Sensor.TYPE_PRESSURE: return "Pressure sensor";
			case Sensor.TYPE_PROXIMITY: return "Proxymity sensor";
			case Sensor.TYPE_ROTATION_VECTOR: return "Rotation vector sensor";
			case Sensor.TYPE_TEMPERATURE: return "Thermometer";			
			default: return "Unknown type";
		}
	}

	@Override
	protected void onResume() {			
		
		/*
		 * On enregistre tout les listners au demarrage de l'activite
		 */
		
		final int delay = SensorManager.SENSOR_DELAY_UI;	
		
		Set<SensorEventListener> sensorListners = sensorsListnerMap.keySet();
		for (Iterator<SensorEventListener> iterator = sensorListners.iterator(); iterator.hasNext();) {			
			SensorEventListener sensorEventListener = (SensorEventListener) iterator.next();
			Sensor sensor = sensorsListnerMap.get(sensorEventListener);			
			mSensorManager.registerListener(sensorEventListener, sensor, delay);					
		}		
		super.onResume();		
	}
	
	@Override
	protected void onPause() {
		
		/*
		 * Afin d'éviter de bouffer toute la batterie, onPause on se désoucrit au event 
		 */
		Set<SensorEventListener> sensorListners = sensorsListnerMap.keySet();
		for (Iterator<SensorEventListener> iterator = sensorListners.iterator(); iterator.hasNext();) {
			SensorEventListener sensorEventListener = (SensorEventListener) iterator.next();
			mSensorManager.unregisterListener(sensorEventListener);
		}
			
		super.onPause();
	}
}