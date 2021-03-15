
package capacitor.plugin.docline.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import docline.doclinevideosdk.core.listeners.ArchiveListener;
import docline.doclinevideosdk.core.listeners.ChatListener;
import docline.doclinevideosdk.core.listeners.ConnectionListener;
import docline.doclinevideosdk.core.listeners.DoclineListener;
import docline.doclinevideosdk.core.listeners.enums.CameraSource;
import docline.doclinevideosdk.core.listeners.enums.ScreenView;
import docline.doclinevideosdk.views.DoclineActivity;

@NativePlugin
public class DoclineSDK extends Plugin {

  final String EVENT_ID = "eventId";
  final String TYPE_ID = "type";
  final String SCREEN_ID = "screenId";

  @PluginMethod
  public void join(final PluginCall call) {
    Intent intent = new Intent(getActivity(), DoclineActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    JSObject data = call.getData();
    String code = data.getString("code", "");
    if (code.isEmpty()) {
      try {
        JSONObject dictionary = new JSONObject();
        dictionary.put(EVENT_ID,"error");
        dictionary.put(TYPE_ID,"emptyCodeError");
        notify(call, dictionary);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return;
    }
    String path = data.getString("path");
    if (path.isEmpty()) {
      try {
        JSONObject dictionary = new JSONObject();
        dictionary.put(EVENT_ID,"error");
        dictionary.put(TYPE_ID,"connectionError");
        notify(call, dictionary);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return;
    }
    intent.putExtra(DoclineActivity.CODE, code);
    intent.putExtra(DoclineActivity.URL, path);
    getContext().startActivity(intent);
    IntentFilter filter = new IntentFilter();
    filter.addAction(DoclineActivity.GENERAL_LISTENER);
    filter.addAction(DoclineActivity.CONNECTION_LISTENER);
    filter.addAction(DoclineActivity.ARCHIVE_LISTENER);
    filter.addAction(DoclineActivity.CHAT_LISTENER);
    BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
          case DoclineActivity.GENERAL_LISTENER:
            manageGeneralListener(call, intent);
            break;
          case DoclineActivity.CONNECTION_LISTENER:
            manageConnectionListener(call, intent);
            break;
          case DoclineActivity.ARCHIVE_LISTENER:
            manageArchiveListener(call, intent);
            break;
          case DoclineActivity.CHAT_LISTENER:
            manageChatListener(call, intent);
            break;
          default:
            break;
        }
      }
    };
    getContext().registerReceiver(receiver, filter);
    call.success();
  }


  private void manageGeneralListener(PluginCall call, Intent intent) {
    Bundle extras = intent.getExtras();
    DoclineListener.MethodName method = (DoclineListener.MethodName) extras.getSerializable(DoclineActivity.METHOD);
    JSONObject dictionary = new JSONObject();
    Bundle bundle = intent.getExtras();
    ScreenView screenName;
    try {
      dictionary.put(EVENT_ID, method.toString());
      switch (method) {
        case consultationJoinSuccess:
          notify(call, dictionary);
          break;
        case consultationTerminated:
          screenName = (ScreenView) bundle.getSerializable(DoclineActivity.SCREEN);
          dictionary.put(SCREEN_ID, screenName);
          notify(call, dictionary);
          break;
        case consultationJoinError:
          dictionary.put(TYPE_ID, "unauthorizedError");
          notify(call, dictionary);
          break;
        case showScreenView:
        case updatedMicrophone:
          screenName = (ScreenView) bundle.getSerializable(DoclineActivity.SCREEN);
          dictionary.put(SCREEN_ID, screenName.toString());
          notify(call, dictionary);
          break;
        case updatedCameraSource:
          CameraSource cameraSource = (CameraSource) bundle.getSerializable(DoclineActivity.CAMERA_SOURCE);
          screenName = (ScreenView) bundle.getSerializable(DoclineActivity.SCREEN);
          dictionary.put(SCREEN_ID, screenName.toString());
          notify(call, dictionary);
          break;
        case updatedCameraStatus:
          boolean isEnabled = bundle.getBoolean(DoclineActivity.CAMERA_SOURCE);
          screenName = (ScreenView) bundle.getSerializable(DoclineActivity.SCREEN);
          dictionary.put(SCREEN_ID, screenName.toString());
          notify(call, dictionary);
          break;
        default:
          break;
      }
    } catch (JSONException e) {
      call.resolve();
    }
  }


  private void manageConnectionListener(PluginCall call, Intent intent) {
    Bundle extras = intent.getExtras();
    ConnectionListener.MethodName method = (ConnectionListener.MethodName) extras.getSerializable(DoclineActivity.METHOD);
    Bundle bundle = intent.getExtras();
    JSONObject dictionary = new JSONObject();
    try {
      dictionary.put(EVENT_ID, method.toString());
      switch (method) {
        case consultationReconnecting:
        case consultationReconnected:
        case userSelectExit:
        case userTryReconnect:
          notify(call, dictionary);
          break;
        case disconnectedByError:
          dictionary.put(TYPE_ID, "connectionError");
          notify(call, dictionary);
          break;
        default:
          break;
      }
    } catch (JSONException e) {
      call.resolve();
    }

  }

  private void manageArchiveListener(PluginCall call, Intent intent) {
    Bundle extras = intent.getExtras();
    ArchiveListener.MethodName method = (ArchiveListener.MethodName) extras.getSerializable(DoclineActivity.METHOD);
    Bundle bundle = intent.getExtras();
    JSONObject dictionary = new JSONObject();
    try {
      dictionary.put(EVENT_ID, method.toString());
      switch (method) {
        case screenRecordingStarted:
        case screenRecordingFinished:
        case screenRecordingApproved:
        case screenRecordingDenied:
          notify(call, dictionary);
          break;
        default:
          break;
      }
    } catch (JSONException e) {
      call.resolve();
    }
  }

  private void manageChatListener(PluginCall call, Intent intent) {
    Bundle extras = intent.getExtras();
    ChatListener.MethodName method = (ChatListener.MethodName) extras.getSerializable(DoclineActivity.METHOD);
    JSONObject dictionary = new JSONObject();
    try {
      dictionary.put(EVENT_ID, method.toString());
      switch (method) {
        case messageSent:
        case messageReceived:
          notify(call, dictionary);
          break;
        default:
          break;
      }
    } catch (JSONException e) {
      call.resolve();
    }
  }


  private void notify(PluginCall call, JSONObject dictionary) {
    JSObject ret = new JSObject();
    Iterator<String> it = dictionary.keys();
    while (it.hasNext()) {
      String key = it.next();
      try {
        ret.put(key, dictionary.getString(key));
      } catch (JSONException e) {
        e.printStackTrace();
        call.resolve();
      }
    }
    try {
      notifyListeners(dictionary.getString(EVENT_ID), ret);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}