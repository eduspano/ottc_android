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

package com.scipopulis.ottc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.scipopulis.ottc.adapter.ListAdapterTrip;
import com.scipopulis.ottc.api.object.TripList;
import com.scipopulis.ottc.controller.Bubble;
import com.scipopulis.ottc.dao.TripDAO;
import com.scipopulis.ottc.dao.PointDAO;
import com.scipopulis.ottc.dao.UserDAO;
import com.scipopulis.ottc.fragment.MapFragment;
import com.scipopulis.ottc.fragment.SACFragment;
import com.scipopulis.ottc.model.Point;
import com.scipopulis.ottc.model.Trip;
import com.scipopulis.ottc.model.User;
import com.squareup.otto.Subscribe;

import java.util.List;

public class TabActivity extends AppCompatActivity {

    private User user;
    private ListAdapterTrip mc;
    private List<Trip> cs;

    private Bubble bb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        MainActivity.bus.register(this);


        try {
            int versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            this.setTitle("OTTC "+versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        View tab1 = LayoutInflater.from(TabActivity.this).
                inflate(R.layout.tabs_item, null);
        ((ImageView)tab1.findViewById(R.id.tab_sel)).setImageResource(R.drawable.tab_selector_car);
        ((TextView)tab1.findViewById(R.id.tab_text)).setText("CORRIDAS");

        TabHost.TabSpec spec1=tabHost.newTabSpec("TAB 1");
        spec1.setContent(R.id.linearLayout);
        spec1.setIndicator(tab1);


        View tab2 = LayoutInflater.from(TabActivity.this).
                inflate(R.layout.tabs_item, null);
        ((ImageView)tab2.findViewById(R.id.tab_sel)).setImageResource(R.drawable.tab_selector_extrato);
        ((TextView)tab2.findViewById(R.id.tab_text)).setText("EXTRATO");

        TabHost.TabSpec spec2=tabHost.newTabSpec("TAB 2");
        spec2.setContent(R.id.linearLayout2);
        spec2.setIndicator(tab2);


        View tab3 = LayoutInflater.from(TabActivity.this).
                inflate(R.layout.tabs_item, null);
        ((ImageView)tab3.findViewById(R.id.tab_sel)).setImageResource(R.drawable.tab_selector_sac);
        ((TextView)tab3.findViewById(R.id.tab_text)).setText("SAC");

        TabHost.TabSpec spec3=tabHost.newTabSpec("TAB 3");
        spec3.setContent(R.id.linearLayout3);
        spec3.setIndicator(tab3);


        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);




        //tab mapa
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();
        tx.replace(R.id.frame_map, new MapFragment());
        tx.commit();



        TripDAO dao = new TripDAO(TabActivity.this);
        cs = dao.search("all");
        dao.close();

        ExpandableListView list = (ExpandableListView) findViewById(R.id.trip_listView);
        mc = new ListAdapterTrip(TabActivity.this, cs);
        list.setAdapter(mc);



        //tab sac
        FragmentManager manager_sac = getSupportFragmentManager();
        FragmentTransaction tx_sac = manager_sac.beginTransaction();
        tx_sac.replace(R.id.frame_sac, new SACFragment());
        tx_sac.commit();



        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");


        if(bb==null) {
            bb = new Bubble(TabActivity.this, user);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        intro();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        UserDAO dao = new UserDAO(this);
        user = dao.logged();
        dao.close();

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intentform = new Intent(TabActivity.this, AccountActivity.class);
            intentform.putExtra("user", user);
            startActivity(intentform);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }


    private void intro(){
        ImageView imgIntro = (ImageView) findViewById(R.id.imageViewExtrato);
        if(cs.size()==0){
            imgIntro.setVisibility(ImageView.VISIBLE);
        }else{
            imgIntro.setVisibility(ImageView.GONE);
        }
    }



    @Subscribe
    public void respBus(TripList list) {
        if(list.getError().isEmpty()){
            PointDAO daopoint = new PointDAO(TabActivity.this);
            daopoint.delete( list.getCorridaId() );
            daopoint.close();

            TripDAO dao = new TripDAO(TabActivity.this);
            dao.delete( list.getCorridaId() );
            dao.close();

            cs.remove(list.getIndex());
            mc.notifyDataSetChanged();
            Toast.makeText(TabActivity.this,"Deletado com sucesso",Toast.LENGTH_SHORT).show();

            intro();
        }else{
            Toast.makeText(TabActivity.this,"Verifique sua conexão e tente novamente",Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void respBus2(Trip trip) {
        if(trip.getStatus()==0)
            cs.add(0,trip);
        else
            cs.set(0,trip);
        mc.notifyDataSetChanged();

        intro();
    }

    @Subscribe
    public void respBus3(Point point) {
        PointDAO daopoint = new PointDAO(TabActivity.this);
        point.setFlag("ok");
        daopoint.update(point);
        daopoint.close();
    }


}
