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


import android.location.Location;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.scipopulis.ottc.Constants;
import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.R;
import com.scipopulis.ottc.api.object.TripList;
import com.scipopulis.ottc.helper.DateHelper;
import com.scipopulis.ottc.model.Point;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;



public class apiPosition {

    RestAdapter retrofit = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(Constants.HOST)
            .build();


    public interface PositionService{
        @FormUrlEncoded
        @POST("/api/v1/position")
        void add(@Field("user_id") String userID,
                         @Field("status") String status,
                         @Field("battery") Float battery,
                         @Field("provider") String provider,
                         @Field("service") String service,
                         @Field("agency") String agency,
                         @Field("ride_id") String ride_id,
                         @Field("lat") String lat,
                         @Field("lng") String lng,
                         @Field("ts") String ts,
                         @Field("mobile_id") String mobile_id,
                         Callback<JsonObject> callback);
    }

    public void addPosition(final Location location){

        PositionService service = retrofit.create(PositionService.class);

        Date date=new Date();
        DateHelper helper = new DateHelper();
        String ts = helper.formatISO8601(date);

        service.add(location.getExtras().getString("user"), location.getExtras().getString("status"), (float) 100, location.getExtras().getString("operator"), "rideshare", "saopaulo_sp", location.getExtras().getString("tripid") , Double.toString(location.getLatitude()), Double.toString(location.getLongitude()), ts, location.getExtras().getString("mobileid"), new Callback<JsonObject>() {

            @Override
            public void success(JsonObject response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }




    public void updatePosition(final Point ff, String userid){

        PositionService service = retrofit.create(PositionService.class);

        Date date=new Date();
        DateHelper helper = new DateHelper();
        String ts = helper.formatISO8601(date);

        service.add(userid, ff.getStatus(), (float) 100, ff.getOperator(), "rideshare", "saopaulo_sp", ff.getTrip_id(), ff.getLat(), ff.getLng(), ts, ff.getMobile_id(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject response, Response response2) {
                MainActivity.bus.post(ff);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("http","error"+error);
            }
        });

    }






    public interface RemoveService{
        @FormUrlEncoded
        @POST("/ride/delete")
        void remove(@Field("ride_id") String tripid,
                 Callback<JsonObject> callback);
    }

    public void removePosition(final String tripid, final int index){

        RemoveService service = retrofit.create(RemoveService.class);

        service.remove(tripid, new Callback<JsonObject>() {

            TripList triplist = new TripList();

            @Override
            public void success(JsonObject response, Response response2) {

                if( response.get("status").getAsString().equals("error") ) {
                    triplist.setError(response.get("message").getAsString());
                }else{
                    triplist.setIndex( index );
                    triplist.setCorridaId( tripid );
                }
                MainActivity.bus.post(triplist);

            }

            @Override
            public void failure(RetrofitError error) {
                triplist.setError(Constants.ErrorRetrofit);
                MainActivity.bus.post(triplist);
            }
        });

    }



}
