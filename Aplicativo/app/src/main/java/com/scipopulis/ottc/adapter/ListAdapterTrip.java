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

package com.scipopulis.ottc.adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.scipopulis.ottc.R;
import com.scipopulis.ottc.api.apiPosition;
import com.scipopulis.ottc.dao.PointDAO;
import com.scipopulis.ottc.model.Point;
import com.scipopulis.ottc.model.Trip;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ListAdapterTrip extends BaseExpandableListAdapter {

    private List<Trip> triplist;
    private Context mContext;
    private Trip trip;

    public ListAdapterTrip(Context context, List<Trip> cs) {
        this.mContext = context;
        this.triplist = cs;
    }

    @Override
    public int getGroupCount() {
        return triplist.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return triplist.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return triplist.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        trip = triplist.get(i);

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (view == null) {
            view = inflater.inflate(R.layout.list_trip, viewGroup, false);
        }


        String root = mContext.getFilesDir().toString();
        File f = new File(root + "/operator/"+trip.getOperator()+".png");
        Picasso.with(mContext).load(f).into((ImageView) view.findViewById(R.id.item_icon));


        TextView startField = (TextView) view.findViewById(R.id.item_start);
        startField.setText(trip.getStart());

        TextView endField = (TextView) view.findViewById(R.id.item_end);
        endField.setText(trip.getEnd());


        long start_time = Long.parseLong(trip.getStart_time());
        long end_time = Long.parseLong(trip.getEnd_time());
        long data = Long.parseLong(trip.getStart_time());

        SimpleDateFormat date_format = new SimpleDateFormat("dd/MMM/yy");
        Date resultdate = new Date(data);

        SimpleDateFormat time_format = new SimpleDateFormat("HH:mm");
        Date result_start = new Date(start_time);
        Date result_end = new Date(end_time);

        TextView dataField = (TextView) view.findViewById(R.id.item_start_date);
        dataField.setText(date_format.format(resultdate));

        TextView cTimeStart = (TextView) view.findViewById(R.id.item_start_time);
        cTimeStart.setText(time_format.format(result_start));

        TextView cTimeEnd = (TextView) view.findViewById(R.id.item_end_time);
        cTimeEnd.setText(time_format.format(result_end));

        final int k = i;

        ImageView bot_tree = (ImageView) view.findViewById(R.id.bot_info_more);
        bot_tree.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:

                                AlertDialog.Builder adb=new AlertDialog.Builder(mContext);
                                adb.setTitle("Deletar?");
                                adb.setMessage("Você tem certeza que deseja deletar esse item");
                                adb.setNegativeButton("Cancelar", null);
                                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        apiPosition api = new apiPosition();
                                        api.removePosition( triplist.get(k).getTripid(), k );

                                    };
                                });
                                adb.show();

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_info_list, popup.getMenu());
                popup.show();
            }
        });


        return view;
    }




    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        MapView map = null;

        if (view == null) {
            view = inflater.inflate(R.layout.mapview_adapter, viewGroup, false);
            map = (MapView) view.findViewById(R.id.map_list);
            map.onCreate(null);
            map.onResume();
        }

        map = (MapView) view.findViewById(R.id.map_list);
        map.getMapAsync(
                new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googlemap) {

                        MapsInitializer.initialize(mContext);
                        //googlemap.getUiSettings().setZoomControlsEnabled(true);
                        googlemap.clear();

                        PointDAO daopoint = new PointDAO(mContext);
                        List<Point> cs = daopoint.search(triplist.get(i).getTripid());
                        daopoint.close();

                        PolylineOptions line = new PolylineOptions();

                        if(cs!=null) {
                            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                            for (int k = 0; k < cs.size(); k++) {
                                LatLng coord = new LatLng(Double.parseDouble(cs.get(k).getLat()), Double.parseDouble(cs.get(k).getLng()));
                                line.add(coord);
                                bounds.include(coord);
                            }
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(),300,200,10);
                            googlemap.moveCamera(cameraUpdate);
                        }

                        // Get back the mutable Polyline
                        Polyline polyline = googlemap.addPolyline(line.color(Color.BLUE).width(10));

                    }
                }
        );
        
        
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


}
