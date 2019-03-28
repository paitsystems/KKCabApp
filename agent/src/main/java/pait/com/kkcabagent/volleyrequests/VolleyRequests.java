package pait.com.kkcabagent.volleyrequests;

//Created by ANUP on 6/7/2018.

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import pait.com.kkcabagent.constant.AppSingleton;
import pait.com.kkcabagent.constant.Constant;
import pait.com.kkcabagent.interfaces.ServerCallback;

public class VolleyRequests {

    private Context context;
    private String writeFilename = "Write.txt";

    public VolleyRequests(Context _context) {
        this.context = _context;
    }

    public void getOTPCode(String url, final ServerCallback callback) {
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Constant.showLog(response);
                            response = response.replace("\\", "");
                            response = response.replace("\"", "");
                            //JSONArray jsonArray = new JSONArray(response);
                            //response = jsonArray.getJSONObject(0).getString("Auto");
                            callback.onSuccess(response);
                        }catch (Exception e){
                            e.printStackTrace();
                            callback.onFailure("getOTPCode_VolleyError_"+e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure("getOTPCode_VolleyError_"+error.getMessage());
                        Constant.showLog(error.getMessage());
                    }
                }
        );
        AppSingleton.getInstance(context).addToRequestQueue(request, "OTP");
    }
}
