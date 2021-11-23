package capacitor.plugin.docline.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.lifecycle.Lifecycle;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import docline.doclinevideosdk.core.listeners.ArchiveListener;
import docline.doclinevideosdk.core.listeners.ChatListener;
import docline.doclinevideosdk.core.listeners.ConnectionListener;
import docline.doclinevideosdk.core.listeners.DoclineListener;
import docline.doclinevideosdk.core.listeners.LifecycleListener;
import docline.doclinevideosdk.core.listeners.enums.CameraSource;
import docline.doclinevideosdk.core.listeners.enums.ScreenView;
import docline.doclinevideosdk.core.listeners.enums.UserType;
import docline.doclinevideosdk.views.DoclineActivity;

@CapacitorPlugin(name = "DoclineSDK")
public class DoclineSDKPlugin extends Plugin {

    final String EVENT_ID = "eventId";
    final String TYPE_ID = "type";
    final String SCREEN_ID = "screenId";
    final String USER_TYPE = "userType";

    private PluginCall pluginCall = null;
    private BroadcastReceiver broadcastReceiver = null;

    @PluginMethod
    public void join(final PluginCall call) {
        pluginCall = call;

        // get code and path
        JSObject data = pluginCall.getData();
        String code = data.getString("code");
        String path = data.getString("path");

        // check if code is valid
        if (code == null || code.isEmpty()) {
            try {
                JSONObject dictionary = new JSONObject();
                dictionary.put(EVENT_ID,"error");
                dictionary.put(TYPE_ID,"emptyCodeError");
                notifyError(dictionary);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        // check if path is valid
        if (path == null || path.isEmpty()) {
            try {
                JSONObject dictionary = new JSONObject();
                dictionary.put(EVENT_ID,"error");
                dictionary.put(TYPE_ID,"connectionError");
                notifyError(dictionary);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        // create broadcast listeners
        IntentFilter filter = new IntentFilter();
        filter.addAction(DoclineActivity.LIFECYCLE_LISTENER);
        filter.addAction(DoclineActivity.GENERAL_LISTENER);
        filter.addAction(DoclineActivity.CONNECTION_LISTENER);
        filter.addAction(DoclineActivity.ARCHIVE_LISTENER);
        filter.addAction(DoclineActivity.CHAT_LISTENER);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case DoclineActivity.LIFECYCLE_LISTENER:
                        manageLifecycleListener(intent);
                        break;
                    case DoclineActivity.GENERAL_LISTENER:
                        manageGeneralListener(intent);
                        break;
                    case DoclineActivity.CONNECTION_LISTENER:
                        manageConnectionListener(intent);
                        break;
                    case DoclineActivity.ARCHIVE_LISTENER:
                        manageArchiveListener(intent);
                        break;
                    case DoclineActivity.CHAT_LISTENER:
                        manageChatListener(intent);
                        break;
                    default:
                        break;
                }
            }
        };
        getContext().registerReceiver(broadcastReceiver, filter);

        // start DoclineActivity
        Intent intent = new Intent(getActivity(), DoclineActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DoclineActivity.CODE, code);
        intent.putExtra(DoclineActivity.URL, path);
        intent.putExtra(DoclineActivity.ENABLE_SETTINGS, true);
        getContext().startActivity(intent);

        resolve(false);
    }

    private void manageLifecycleListener(Intent intent) {
        Bundle extras = intent.getExtras();
        LifecycleListener.Method method = (LifecycleListener.Method) extras.getSerializable(DoclineActivity.METHOD);
        JSONObject dictionary = new JSONObject();
        try {
            dictionary.put(EVENT_ID, method.toString());
            switch (method) {
                case OnActivityDestroyed:
                    notify(dictionary);
                    resolve(true);
                    break;
                case OnActivityCreated:
                    notify(dictionary);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            resolve(false);
        }
    }

    private void manageGeneralListener(Intent intent) {
        Bundle extras = intent.getExtras();
        DoclineListener.MethodName method = (DoclineListener.MethodName) extras.getSerializable(DoclineActivity.METHOD);
        JSONObject dictionary = new JSONObject();
        Bundle bundle = intent.getExtras();
        ScreenView screenName;
        try {
            dictionary.put(EVENT_ID, method.toString());
            switch (method) {
                case consultationJoinSuccess:
                    notify(dictionary);
                    break;
                case consultationTerminated:
                    screenName = (ScreenView) bundle.getSerializable(DoclineActivity.SCREEN);
                    dictionary.put(SCREEN_ID, screenName);
                    notify(dictionary);
                    resolve(true);
                    break;
                case consultationJoinError:
                    dictionary.put(EVENT_ID,"error");
                    dictionary.put(TYPE_ID, "unauthorizedError");
                    notifyError(dictionary);
                    break;

                case consultationExit:
                case consultationRejoin:
                    UserType userType = (UserType) bundle.getSerializable(DoclineActivity.USER_TYPE);
                    dictionary.put(USER_TYPE, userType.toString());
                    notify(dictionary);
                    break;

                case showScreenView:
                case updatedMicrophone:
                    screenName = (ScreenView) bundle.getSerializable(DoclineActivity.SCREEN);
                    dictionary.put(SCREEN_ID, screenName.toString());
                    notify(dictionary);
                    break;
                case updatedCameraSource:
                    CameraSource cameraSource = (CameraSource) bundle.getSerializable(DoclineActivity.CAMERA_SOURCE);
                    screenName = (ScreenView) bundle.getSerializable(DoclineActivity.SCREEN);
                    dictionary.put(SCREEN_ID, screenName.toString());
                    notify(dictionary);
                    break;
                case updatedCameraStatus:
                    boolean isEnabled = bundle.getBoolean(DoclineActivity.CAMERA_SOURCE);
                    screenName = (ScreenView) bundle.getSerializable(DoclineActivity.SCREEN);
                    dictionary.put(SCREEN_ID, screenName.toString());
                    notify(dictionary);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            resolve(false);
        }
    }

    private void manageConnectionListener(Intent intent) {
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
                    notify(dictionary);
                    break;
                case disconnectedByError:
                    dictionary.put(TYPE_ID, "connectionError");
                    notify(dictionary);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            resolve(false);
        }

    }

    private void manageArchiveListener(Intent intent) {
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
                    notify(dictionary);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            resolve(false);
        }
    }

    private void manageChatListener(Intent intent) {
        Bundle extras = intent.getExtras();
        ChatListener.MethodName method = (ChatListener.MethodName) extras.getSerializable(DoclineActivity.METHOD);
        JSONObject dictionary = new JSONObject();
        try {
            dictionary.put(EVENT_ID, method.toString());
            switch (method) {
                case messageSent:
                case messageReceived:
                    notify(dictionary);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            resolve(false);
        }
    }


    private void notify(JSONObject dictionary) {
        JSObject ret = new JSObject();
        Iterator<String> it = dictionary.keys();
        while (it.hasNext()) {
            String key = it.next();
            try {
                ret.put(key, dictionary.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
                resolve(false);
            }
        }
        try {
            notifyListeners(dictionary.getString(EVENT_ID), ret);
        } catch (JSONException e) {
            e.printStackTrace();
            resolve(false);
        }
    }

    private void notifyError(JSONObject dictionary) {
        JSObject ret = new JSObject();
        Iterator<String> it = dictionary.keys();

        while (it.hasNext()) {
            String key = it.next();
            try {
                ret.put(key, dictionary.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
                resolve(false);
            }
        }

        try {
            notifyListeners(dictionary.getString(EVENT_ID), ret);
            resolve(true);
        } catch (JSONException e) {
            e.printStackTrace();
            resolve(true);
        }
    }

    private void resolve(Boolean finishPlugin) {
        if (pluginCall != null) {
            pluginCall.resolve();
            if (finishPlugin) {

                if (broadcastReceiver != null) {
                    getContext().unregisterReceiver(broadcastReceiver);
                }
                removeAllListeners(pluginCall);
                pluginCall = null;
            }
        }
    }
}
