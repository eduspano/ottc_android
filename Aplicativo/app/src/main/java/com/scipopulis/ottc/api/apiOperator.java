/**

 Projeto OTTC - Operadora de Tecnologia de Transporte Compartilhado
 Copyright (C) <2017> Scipopulis Desenvolvimento e Análise de Dados Ltda

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 Authors: Roberto Speicys Cardoso
 Date: 2017-03-20
 */

package com.scipopulis.ottc.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.scipopulis.ottc.Constants;
import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.R;
import com.scipopulis.ottc.api.object.Operator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;


public class apiOperator {

    private Context mContext;
    static private ArrayList<Target> targetList = new ArrayList<>();

    RestAdapter retrofit = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(Constants.HOST)
            .build();


    public interface LogoService{
        @GET("/api/v1/provider")
        void getOperator(Callback<JsonObject> callback);
    }

    public void getLogo(final Context cc){

        this.mContext = cc;

        LogoService service = retrofit.create(LogoService.class);

        service.getOperator(new Callback<JsonObject>() {

            Operator op = new Operator();

            @Override
            public void success(JsonObject response, Response response2) {

                ArrayList<String> opers = new ArrayList<String>();

                String props_old = "";
                OutputStream out = null;
                try {
                    Properties props = new Properties();
                    File f = new File(cc.getFilesDir().toString() + "ottc.properties");
                    if(f.exists()){
                        props.load(new FileReader(f));
                        props_old = props.get("ts").toString();

                        props.setProperty("ts", ""+System.currentTimeMillis() );
                    }
                    else{

                        props_old = ""+System.currentTimeMillis();
                        props.setProperty("ts", ""+System.currentTimeMillis() );
                        f.createNewFile();
                    }
                    out = new FileOutputStream( f );
                    props.store(out, "list operators");
                }
                catch (Exception e ) {
                    e.printStackTrace();
                }
                finally{
                    if(out != null){
                        try {
                            out.close();
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                for(int i=0;i<response.get("companies").getAsJsonArray().size();i++) {

                    String url = response.get("companies").getAsJsonArray().get(i).getAsJsonObject().get("logoUrl").getAsString();

                    final String name_file = response.get("companies").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                    opers.add(name_file);

                    if( ! props_old.equals( ""+System.currentTimeMillis() ) ){

                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                try {
                                    String root = cc.getFilesDir().toString();
                                    File myDir = new File(root + "/operator");

                                    if (!myDir.exists()) {
                                        myDir.mkdirs();
                                    }

                                    String name = name_file + ".png";
                                    myDir = new File(myDir, name);
                                    FileOutputStream out = new FileOutputStream(myDir);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

                                    out.flush();
                                    out.close();
                                } catch (Exception e) {
                                    // some action
                                    // Log.d("BITMAP", e.toString());
                                }
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                Log.d("BITMAP", "Failed..");
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                Log.d("BITMAP", "Preparing..");
                            }
                        };

                        targetList.add(target);

                        Picasso.with(mContext)
                                .load(url)
                                .into(target);
                    }

                }

                op.setOpers(opers);
                MainActivity.bus.post(op);

            }

            @Override
            public void failure(RetrofitError error) {
                op.setError(Constants.ErrorRetrofit);
                MainActivity.bus.post(op);
            }

        });

    }


}