package com.umeng.plugin;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.busll.quzhouxing.R;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.os.Looper.getMainLooper;

/**
 * This class echoes a string called from JavaScript.
 */
public class UPush extends CordovaPlugin {
    private Context mContext = null;
  private static final String TAG = "quzhouxing.upush";
  public static final String UPDATE_STATUS_ACTION = "com.busll.quzhouxing.action.UPDATE_STATUS";
  private Handler handler;
  public static String deviceToken;
    @Override
   public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getToken")) {
            this.getToken(callbackContext);
            return true;
        }
        return false;
    }

    public void getToken(CallbackContext callbackContext) {
        if (deviceToken!=null) {
            callbackContext.success(deviceToken);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

	public static void setToken(String token){
      deviceToken = token;
    }
}
