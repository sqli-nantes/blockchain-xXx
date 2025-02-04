package com.sqli.automotive;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sqli.automotive.contract.ChoupetteContract;
import com.sqli.blockchain.android_geth.EthereumApplication;

import ethereumjava.EthereumJava;
import ethereumjava.net.provider.AndroidIpcProvider;

/**
 * Created by root on 25/10/16.
 */

public class MyApplication extends EthereumApplication {


    public static final String PASSWORD = "toto";
    public static final String NAMES_JSON_IP = "172.17.0.4";

    public String accountId;

    public EthereumJava ethereumjava;
    public ChoupetteContract choupetteContract;

    RequestQueue queue;

    @Override
    public void onEthereumServiceReady() {

        String dir = ethereumService.getIpcFilePath();
        ethereumjava = new EthereumJava.Builder()
                .provider(new AndroidIpcProvider(dir))
                .build();

        accountId = ethereumjava.personal.newAccount("toto");
        Log.d("GETH",accountId);

        queue = Volley.newRequestQueue(this);

        registerBootnode();

        getMoney();

        super.onEthereumServiceReady();

    }

    private void registerBootnode() {
        String sendIdRequestURL = "http://"+NAMES_JSON_IP+":8081/names?name=JIM&address="+accountId;

        StringRequest sendIdRequest = new StringRequest(Request.Method.GET, sendIdRequestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String enode) {
                        Log.d("GETH",enode);
                        enode = enode.replace("\"","").replace("\n","").replace("\u0000","");
                        boolean added = ethereumjava.admin.addPeer(enode);
                        Log.d("GETH",String.valueOf(added));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    Log.e("GETH", error.getMessage());
                }
                else {
                    Log.e("GETH", "error in http request using registerBootnode() method");
                }
            }
        });

        queue.add(sendIdRequest);
    }

    private void getMoney() {
        String sendIdRequestURL = "http://"+NAMES_JSON_IP+":8081/send?name=JIM&address="+accountId+"&amount=12";

        StringRequest sendIdRequest = new StringRequest(Request.Method.GET, sendIdRequestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String enode) {
                        Log.d("GETH",enode);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GETH","error getMoney()");
            }
        });

        queue.add(sendIdRequest);
    }


    public void setContractAtAdress(String address){
        choupetteContract = (ChoupetteContract) ethereumjava.contract.withAbi(ChoupetteContract.class).at(address);
    }
}
