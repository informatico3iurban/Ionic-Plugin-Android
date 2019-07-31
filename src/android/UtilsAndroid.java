package cordova.plugin.utilsandroid;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cordova.plugin.utilsandroid.AsyncPrintTicket;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Build;
import android.content.Context;
import java.util.Arrays;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UtilsAndroid extends CordovaPlugin {
    private static final String TAG = "HOTEL_DIGITAL";
    private Context context;
    int counter = 0, timeout = 10;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Log.d(TAG, "in UtilsAndroid.java "+action);

        context = this.cordova.getActivity().getApplicationContext();

        Log.d(TAG, "in UtilsAndroid.java "+action);

        if (action.equals("printTicket")) {
            Log.d(TAG, "in action printTicket");
            this.printTicket(args, callbackContext);
            return true;
        } else if (action.equals("getMacAddress")) {
            Log.d(TAG, "in action getMacAddress");
            this.getMacAddress(callbackContext);
            return true;
        } else if (action.equals("connectToWifi")) {
            Log.d(TAG, "in action connectToWifi");
            this.connectToWifi(args, callbackContext);
            return true;
        } else if (action.equals("enableWifi")) {
            Log.d(TAG, "in action enableWifi");
            this.enableWifi(callbackContext);
            return true;
        } else if (action.equals("log")) {
            Log.d(TAG, "in action log");
            this.log(args);
            return true;
        } else if (action.equals("logError")) {
            Log.d(TAG, "in action logError");
            this.logError(args);
            return true;
        }
        return false;
    }

    private void printTicket(JSONArray args, CallbackContext callback){
        if(args != null){
            try{
                Log.d(TAG, "in print ticket.java");
                
                AsyncPrintTicket ticketPrinter = new AsyncPrintTicket(context, 
                args.getJSONObject(0).getString("message"), 
                args.getJSONObject(0).getString("ip"), 
                args.getJSONObject(0).getString("port"));
                //messagePrinter.asyncInternet = this;
                ticketPrinter.execute();

                callback.success("Ticket enviado a impresora"); 
            }catch(Exception e){
                callback.error("Something went wrong " + e);
            }
        }else{
            callback.error("Please donot pass null value");
        }
    }

    public void getMacAddress(CallbackContext callback) {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                      callback.success("02:00:00:00:00:00"); 
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                //return res1.toString();
                callback.success(res1.toString()); 
            }
        } catch (Exception ex) {
             callback.success("02:00:00:00:00:00");
        }        
    }

    public void connectToWifi(JSONArray args, CallbackContext callback) {
         try {            
            WifiManager wifiManager = (WifiManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            String networkSSID = args.getJSONObject(0).getString("ssid");
            String password = args.getJSONObject(0).getString("password");
            timeout = Integer.valueOf(args.getJSONObject(0).getString("timeout"));

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";

            conf.preSharedKey = "\"" + password + "\"";

            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            Log.d(TAG, conf.SSID + " " + conf.preSharedKey);

            int networkId = wifiManager.getConnectionInfo().getNetworkId();
            Log.d(TAG, "network disabled " + wifiManager.disableNetwork(networkId));
            Log.d(TAG, "network removed " + wifiManager.removeNetwork(networkId));
            Log.d(TAG, "network saved " + wifiManager.saveConfiguration());

            wifiManager.addNetwork(conf);

            Log.d(TAG, conf.SSID + " " + conf.preSharedKey);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();

                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    Log.d(TAG, i.SSID + " " + conf.preSharedKey);
                    break;
                }
            }

            verifyingConnection(callback);

            //WiFi Connection success, return true
           
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            callback.error("" + e);
            //return false;
        }
    }

    public void verifyingConnection(CallbackContext callback){
        counter = 0;
        final Handler handler = new Handler();    
        handler.postDelayed(new Runnable() {
            public void run() {
                //Log.d(CustomConstants.TAG, "CONNECTED " + InternetHelper.wifiIsConnected(context));
                if (!wifiIsConnected()) {
                     counter++;
                    if(counter < timeout) {
                        Log.d(TAG, "checking connection, counter " + counter);
                        handler.postDelayed(this, 1000);                       
                    }else{
                         callback.error("timeout");
                    }
                } else {
                        callback.success("true"); 
                }
            }
        }, 1000);
    }

    public boolean wifiIsConnected(){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public void enableWifi(CallbackContext callback) {
        try{
            boolean wifiEnabled = false;
            WifiManager wifiManager = (WifiManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                wifiEnabled = wifiManager.setWifiEnabled(true);
            }
            callback.success(""+wifiEnabled); 
        }catch(Exception e){
             callback.error("" + e);
        }
    }

    public void log(JSONArray args) {
        try{
            TAG = args.getJSONObject(0).getString("TAG");
            Log.d(TAG, args.getJSONObject(0).getString("message"));
        }catch(Exception e){
            Log.e(TAG, "" + e);
        }
    }

    public void logError(JSONArray args){
        try{
            TAG = args.getJSONObject(0).getString("TAG");
            Log.e(TAG, args.getJSONObject(0).getString("message"));
        }catch(Exception e){
            Log.e(TAG, "" + e);
        }
    }
}