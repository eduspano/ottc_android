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


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.R;
import com.scipopulis.ottc.dao.TripDAO;
import com.scipopulis.ottc.dao.PointDAO;
import com.scipopulis.ottc.model.Trip;
import com.scipopulis.ottc.model.Point;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.List;


public class UpdateListMap {

    private final Context mContext;
    private final GoogleMap gmap;

    private final ExpandableListView listaCorridas;


    public UpdateListMap(Context cc, GoogleMap map) {

        this.gmap = map;
        this.mContext = cc;

        Activity vv = (Activity) cc;
        listaCorridas = (ExpandableListView) vv.findViewById(R.id.trip_listView);

        MainActivity.bus.register(this);
    }


    @Subscribe
    public void respBus(Location location) {

        gmap.clear();

        String tripid = location.getExtras().getString("tripid");
        String mobileid = location.getExtras().getString("mobileid");
        String status = location.getExtras().getString("status");
        String flag = location.getExtras().getString("flag");
        String operator = location.getExtras().getString("operator");

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        String time = String.valueOf(System.currentTimeMillis());

        Point point = new Point();
        point.setLat( String.valueOf(lat) );
        point.setLng( String.valueOf(lng) );
        point.setTime( time );
        point.setMobile_id( mobileid );
        point.setTrip_id(tripid);
        point.setFlag( flag );
        point.setStatus( status );
        point.setOperator( operator );


        PointDAO daopoint = new PointDAO(mContext);
        daopoint.insert(point);
        List<Point> cs = daopoint.search(tripid);
        daopoint.close();



        String local="Sem localização";

        Geocoder geocoder = new Geocoder(mContext);
        List<Address> resultados = null;

        try {
            resultados = geocoder.getFromLocation( lat, lng, 1 );
            if (!resultados.isEmpty()) {
                local = resultados.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        TripDAO dao = new TripDAO(mContext);
        List<Trip> co = dao.search(tripid);
        Trip trip;

        if ( co.size()>0 ){
            trip = co.get(0);
            trip.setTripid(tripid);
            if( trip.getStart().equals("Sem localização") ){
                trip.setStart( local );
            }
            trip.setEnd_time( time );
            if( !local.equals("Sem localização") ){
                trip.setEnd( local );
            }
            trip.setOperator( operator );
            trip.setStatus(1);
            dao.update(trip);
        }else{
            trip = new Trip();
            trip.setTripid(tripid);
            trip.setStart_time( time );
            trip.setEnd_time( time );
            trip.setStart( local );
            trip.setEnd( local );
            trip.setOperator( operator );
            trip.setStatus(0);
            dao.insert(trip);
        }

        dao.close();

        MainActivity.bus.post(trip);





        gmap.addMarker(new MarkerOptions().position( new LatLng(Double.valueOf( cs.get(0).getLat() ), Double.valueOf( cs.get(0).getLng() ) ) ).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_flag)));

        if ( status.equals("RE") ) {
            gmap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end_flag)));
        }
        else {
            gmap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place)));
        }


        PolylineOptions line = new PolylineOptions();
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        for (int k = 0; k < cs.size(); k++) {
            LatLng coord = new LatLng(Double.valueOf( cs.get(k).getLat() ), Double.valueOf( cs.get(k).getLng() ) );
            bounds.include(coord);
            line.add(coord);
        }


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100);
        gmap.moveCamera(cameraUpdate);
        Polyline polyline = gmap.addPolyline(line.color(Color.parseColor("#1f1a17")).width(10));


    }



}
