package object;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 「搖一搖」功能
 * @author WaterMelon add 2014/07/16 將「搖一搖」功能寫成 Class
 */
public class ShakeWorker
{
	public interface OnShakeWorkerListener
	{
		/**搖一搖*/
		void onMShakeWork();
	}

	private Activity				m_activity;
	private OnShakeWorkerListener	m_onListener;

    private SensorManager	m_sensorManager;	//定義sensor管理器
    private boolean			bSensor2Workflag = true;

	private SensorEventListener m_sensorEventListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent event)
		{
			if (null == m_onListener) {
				return;
			}

//			if (false == TheAppInfo.getInstance(m_activity).uiGetShakeWork()) {
//				return;
//			}

			int sensorType = event.sensor.getType();
			//values[0]:X軸，values[1]：Y軸，values[2]：Z軸
			float[] values = event.values;
			if(sensorType == Sensor.TYPE_ACCELEROMETER) {
			  /*因为一般正常情況下，任意軸數值最大就在9.8~10之間，只有在你突然搖動手機
			  *的時候，瞬時加速度才會突然增大或減少。
			  *所以，經過實際測試，只需監聽任一軸的加速度大於14的時候，改變你需要的設置
			  *就OK了~~~
			  */
				if((Math.abs(values[0])>14 || Math.abs(values[1])>14)) {
					if (bSensor2Workflag)
					{
						bSensor2Workflag = false;
						
						m_onListener.onMShakeWork();
					}
				}
				else if (Math.abs(values[0])<3 || Math.abs(values[1])<3)
				{
					if (!bSensor2Workflag)
					{//搖一搖停止
						bSensor2Workflag = true;
					}
				}
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	};

	public ShakeWorker(Activity activity, OnShakeWorkerListener onShakeWorkerListener)
	{
		m_activity = activity;
		m_onListener = onShakeWorkerListener;

		m_sensorManager = (SensorManager) m_activity.getSystemService(Context.SENSOR_SERVICE);
	}

	public void uiOnResume()
	{
		m_sensorManager.registerListener(m_sensorEventListener, 
				m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				  //還有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
				  //根據不同應用，需要的反應速率不同，具體根據實際情況設定
				  SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void uiOnPause()
	{
		m_sensorManager.unregisterListener(m_sensorEventListener);
	}

}
