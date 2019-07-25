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

import android.content.Context;

public class UtilsAndroid extends CordovaPlugin {
    private static final String TAG = "HOTEL_DIGITAL";
    private Context context;

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
                      callback.success(res1.toString()); 
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
}
