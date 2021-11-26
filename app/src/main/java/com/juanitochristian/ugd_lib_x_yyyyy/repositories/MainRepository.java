package com.juanitochristian.ugd_lib_x_yyyyy.repositories;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.lifecycle.MutableLiveData;

import com.juanitochristian.ugd_lib_x_yyyyy.model.Response;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

public class MainRepository {

    private final Context context;

    @Inject
    public MainRepository(Context context) {
        this.context = context;
    }

    /**
     * TODO 1.5 isi logic untuk Method getResponse()
     *
     * @return MutableLiveData dari kelas response yang nanti disimpan di viewmodel
     * @throws Exception
     * @HINT: mirip guided tapi bungkus hasil keluaran dengan MutableLiveData
     * cara menggunakan live data ada didokumentasi resmi android google
     * https://developer.android.com/topic/libraries/architecture/livedata?hl=id
     */
    public MutableLiveData<Response> getResponse() throws Exception {
        String json = loadJson();
        MutableLiveData<Response> response = new MutableLiveData<Response>();

        if(json != null)
        {
            Gson gson = new Gson();

            response.setValue(gson.fromJson(json, Response.class));
        }

        return response;
    }

    /**
     * TODO 1.4 isi logic untuk Method loadJson()
     *
     * @return referensi file datagame.json dari folder assets
     * @throws Exception
     * @HINT: gunakan context yang ada dikelas ini agar bisa menggunakan method assets
     */
    private String loadJson() throws Exception {
        String json = null;
        try {
                AssetManager am = this.context.getAssets();
            InputStream is = am.open("datagame.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
