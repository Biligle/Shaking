package com.bs.shocklibrary;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * 摇一摇（翻转手机）——>手机振动
 */
public class ShockUtil implements SensorEventListener {

    private static final String TAG = ShockUtil.class.getSimpleName();
    public static final int SHAKE_SHOCK = 1;//摇一摇
    public static final int VERSE_SHOCK = 2;//上下（左右）翻转
    private static volatile ShockUtil instance;
    private WeakReference<Context> weakReference;
    private Context mContext;
    private long EFFECT_TIME = 800;//默认时间间隔
    private int shock_style = 1;//默认风格：摇一摇
    private boolean isOpen = true;//true:开启振动
    private ShockLisener lisener;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long lastTime = 0;
    private int lastX, lastY, lastZ;//上一次坐标值

    private ShockUtil () {}

    public static ShockUtil getInstance () {
        if (instance == null) {
            synchronized (ShockUtil.class) {
                if (null == instance) {
                    instance = new ShockUtil();
                }
            }
        }
        return instance;
    }

    public void setLisener (ShockLisener lisener) {
        this.lisener = lisener;
    }

    /**
     * 设置时间间隔
     * @param time
     */
    public ShockUtil setEffectTime (long time) {
        EFFECT_TIME = time;
        return instance;
    }

    /**
     * 设置振动风格
     * @param shock_style
     */
    public ShockUtil setShockStyle(int shock_style) {
        this.shock_style = shock_style;
        return instance;
    }

    /**
     * 关闭摇一摇（翻转）振动功能
     * @param isOpen
     */
    public void openShock (boolean isOpen) {
        this.isOpen = isOpen;
    }

    /**
     * 主方法（监测xyz坐标，达到振动效果）
     * @param context
     */
    public void shock(Context context) {
        weakReference = new WeakReference<Context>(context);
        mContext = weakReference.get();
        if (null != mContext) {
            mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
            if (null == mSensorManager) {
                Log.e(TAG, "deveice not support SensorManager");
            }
            // 参数三，检测的精准度
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME
        }
    }

    private int verseCount = 0;//翻转计数器，>=1时，触发振动
    @Override
    public void onSensorChanged (SensorEvent sensorEvent) {
        if (sensorEvent.sensor == null) {
            return;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //----------------横向手机------------------------
            //屏幕朝上（0,   0,  10）
            //屏幕朝下（0,   0, -10）
            //屏幕朝左（10,  0,   0）
            //屏幕朝右（-10, 0,   0）
            //----------------竖立手机------------------------
            //上下左右都是（0,10,0）
            int x = (int) sensorEvent.values[0];
            int y = (int) sensorEvent.values[1];
            int z = (int) sensorEvent.values[2];
            //在时间间隔内翻转了，verseCount++，计数大于1，振动
            shake(z,x);
            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int i) {

    }

    /**
     * 摇一摇
     * @param z
     * @param x
     */
    private void shake(int z, int x) {
        if (isOpen) {//开启振动
            switch (shock_style) {
                case SHAKE_SHOCK://摇一摇
                    if (z * lastZ < 0 || x * lastX < 0)
                    {
                        realShock();
                    }
                    else verseCount = 0;
                    break;
                case VERSE_SHOCK://翻一翻
                    if (z * lastZ < 0 && Math.abs(lastZ*z) > 50)
                    {
                        realShock();
                    }
                    else verseCount = 0;
                    break;
            }
        }
    }

    /**
     * 执行振动
     */
    private void realShock() {
        if ((System.currentTimeMillis() - lastTime) < EFFECT_TIME)
        {
            if (verseCount >= 1)
            {
                Vibrator vibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
                verseCount = 0;
                if (null == lisener) return;
                lisener.onShockResult();
                isOpen = false;//关闭振动
                return;
            }
            verseCount++;
        }
        else {
            lastTime = System.currentTimeMillis();
        }
    }

}
