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

package com.scipopulis.ottc.controller;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.R;
import com.scipopulis.ottc.api.apiPosition;
import com.scipopulis.ottc.dao.PointDAO;
import com.scipopulis.ottc.model.Point;

import java.util.ArrayList;
import java.util.List;


public class LocationService implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final int REQUEST_LOCATION = 123;
    public GoogleApiClient client;
    private Context mContext;
    private LocationRequest request;
    public Bundle extras;
    private Location temp_local;


    public LocationService(Context cc) {
        client = new GoogleApiClient.Builder(cc).addApi(LocationServices.API).addConnectionCallbacks(this).build();

        this.mContext = cc;

        MainActivity.bus.register(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        request = new LocationRequest();
        request.setSmallestDisplacement(40);
        request.setInterval(40);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

    }

    @Override
    public void onLocationChanged(Location location) {


        if(location!=null){
            location.setExtras(extras);

            if(location.getAccuracy()>20) {
                LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                Location local = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (local != null && location.getTime()-local.getTime()<600000 ) {
                    location.setLatitude(local.getLatitude());
                    location.setLongitude(local.getLongitude());
                }
            }

            if( extras.get("status").equals("RB") || extras.get("status").equals("R") ){
                extras.putSerializable("status", "R");
                location.setExtras(extras);

                apiPosition positionAPI = new apiPosition();
                positionAPI.addPosition(location);
                MainActivity.bus.post(location);

            }else{

                extras.putSerializable("status", "W");
                extras.putSerializable("tripid", "none");
                extras.putSerializable("mobileid", "none");
                extras.putSerializable("operator", "none" );

                location.setExtras(extras);

                apiPosition positionAPI = new apiPosition();
                positionAPI.addPosition(location);

            }

            temp_local = location;

        }


    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    public void startTrip(){

        Long time = System.currentTimeMillis();

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location local = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (local != null && time-local.getTime()<600000 ) {
            local.setExtras(extras);

            apiPosition positionAPI = new apiPosition();
            positionAPI.addPosition(local);
            MainActivity.bus.post(local);

        }else{
            temp_local.setExtras(extras);

            apiPosition positionAPI = new apiPosition();
            positionAPI.addPosition(temp_local);
            MainActivity.bus.post(temp_local);
        }

    }

    public void endTrip(){

        Long time = System.currentTimeMillis();

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location local = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (local != null && time-local.getTime()<600000 ) {
            local.setExtras(extras);

            apiPosition positionAPI = new apiPosition();
            positionAPI.addPosition(local);
            MainActivity.bus.post(local);
        }else{
            temp_local.setExtras(extras);

            apiPosition positionAPI = new apiPosition();
            positionAPI.addPosition(temp_local);
            MainActivity.bus.post(temp_local);
        }


    }


    private void send_position(){
        PointDAO daopoint = new PointDAO(mContext);
        List<Point> cs = daopoint.searchError("error");
        daopoint.close();
        apiPosition positionAPI = new apiPosition();
        for (int i = 0; i < cs.size(); i++) {
            Point ff = cs.get(i);
            positionAPI.updatePosition(ff, extras.get("user").toString() );
            ff.setFlag("ok");
            daopoint.update(ff);
        }
    }



}





