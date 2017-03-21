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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.R;
import com.scipopulis.ottc.adapter.ImageAdapterOperator;
import com.scipopulis.ottc.api.object.Company;
import com.scipopulis.ottc.dao.PointDAO;
import com.scipopulis.ottc.model.Point;
import com.scipopulis.ottc.model.User;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;


public class Bubble {

    private final String[] opers;
    private Context mContext;
    private LocationService ll;
    protected BubblesManager bubblesManager;
    private ImageView playorpause;
    private String mobileId = "none";
    private String mobkey = "";

    private User user;
    private BubbleLayout bubbleView;
    private BubbleLayout bubbleViewOperator;
    Bundle extras = new Bundle();


    public Bubble(Context cc, User user) {
        this.mContext = cc;
        this.user = user;

        MainActivity.bus.register(this);

        ll = new LocationService(cc);

        permissionMethod();

        SharedPreferences prefs = cc.getSharedPreferences("key_op", cc.MODE_PRIVATE);
        String ss = prefs.getString("operator", null);
        opers = ss.split(", ");

    }

    public void permissionMethod() {

        initializeBubbleManager();

        //aciona o botao
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // Update the User Interface
                addNewNotification();
            }

        }.execute();

    }


    /**
     * Configure the trash layout with your BubblesManager builder.
     */
    private void initializeBubbleManager() {
        bubblesManager = new BubblesManager.Builder(mContext)
                .setTrashLayout(R.layout.notification_trash_layout)
                .build();
        bubblesManager.initialize();
    }


    /*
     * Inflate notifation layout  into bubble layout
     */
    private void addNewNotification() {

        UUID uuid = UUID.randomUUID();
        extras.putSerializable("user", user.getCpf());
        extras.putSerializable("flag", "ok");//flag marca se o ponto foi para o servidor api com sucesso
        extras.putSerializable("status", "W");
        extras.putSerializable("mobileid", "none");
        extras.putSerializable("tripid", "none");
        extras.putSerializable("operator", "none" );
        ll.extras = extras;

        ll.client.connect();

        //box play pause
        bubbleView = (BubbleLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.notification_layout, null);

        //box operators
        bubbleViewOperator = (BubbleLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.activity_operator, null);


        SharedPreferences prefs = mContext.getSharedPreferences("key_op", mContext.MODE_PRIVATE);
        int countRun = prefs.getInt("numRun", 0);
        countRun = countRun + 1;

        SharedPreferences.Editor editor = mContext.getSharedPreferences("key_op", mContext.MODE_PRIVATE).edit();
        editor.putInt("numRun", countRun);
        editor.commit();

        final RelativeLayout viewIntro = (RelativeLayout) bubbleView.findViewById(R.id.introPlay);

        if(countRun<3) {
            viewIntro.setVisibility(View.VISIBLE);
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(5000);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    viewIntro.setVisibility(View.GONE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            viewIntro.startAnimation(fadeOut);

        }else{
            viewIntro.setVisibility(View.GONE);
        }


        GridView gridview = (GridView) bubbleViewOperator.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapterOperator(mContext));


        Button button = (Button) bubbleViewOperator.findViewById(R.id.bot_close_op);

        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                bubbleView.setVisibility(View.VISIBLE);
                bubblesManager.removeBubble(bubbleViewOperator);
            }
        });




        // this method call when user remove notification layout
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
                //Toast.makeText(mContext, "Removido !", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        });
        // this methoid call when cuser click on the notification layout( bubble layout)
        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
            @Override
            public void onBubbleClick(BubbleLayout bubble) {
            if( ll.extras.get("status").equals("RB") || ll.extras.get("status").equals("R") ){
                playorpause.setImageResource(R.drawable.ic_play);
                extras.putSerializable("mobileid", getMobileid());
                extras.putSerializable("status", "RE");
                ll.extras = extras;
                ll.endTrip();
                //ll.client.disconnect();
            }else{
                if(bubbleView.getVisibility()==View.VISIBLE){
                    bubbleView.setVisibility(View.INVISIBLE);
                    bubblesManager.addBubble(bubbleViewOperator, 60, 400);
                    playorpause = (ImageView) bubble.findViewById(R.id.bot_trip);
                }
            }
                ImageView imgIconOp = (ImageView) bubbleView.findViewById(R.id.icon_operator);
                imgIconOp.setVisibility(View.GONE);
            }
        });

        // add bubble view into bubble manager
        bubblesManager.addBubble(bubbleView, 60, 400);

    }

    private void startTrip(String operator) {
        playorpause.setImageResource(R.drawable.ic_stop);
        bubblesManager.removeBubble(bubbleViewOperator);

        UUID uuid = UUID.randomUUID();
        extras.putSerializable("user", user.getCpf());
        extras.putSerializable("flag", "");//flag marca se o ponto foi para o servidor api com sucesso
        extras.putSerializable("status", "RB");
        extras.putSerializable("mobileid", getMobileid());
        extras.putSerializable("tripid", uuid.toString());
        extras.putSerializable("operator", operator );
        ll.extras = extras;

        //ll.client.connect();

        bubbleView.setVisibility(View.VISIBLE);

        ImageView imgIconOp = (ImageView) bubbleView.findViewById(R.id.icon_operator);
        String root = mContext.getFilesDir().toString();
        File f = new File(root + "/operator/"+operator+".png");
        Picasso.with(mContext).load(f).into(imgIconOp);
        imgIconOp.setVisibility(View.VISIBLE);

        ll.startTrip();
    }

    private String getMobileid(){

        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyyMMdd");
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        format.setTimeZone(gmtTime);
        String codstring = format.format(date);

        if( !mobileId.equals(codstring) ){
            if( ll.extras.get("status").equals("RB") ){
                mobkey = UUID.randomUUID().toString();
            }
        }
        return mobkey;
    }


    @Subscribe
    public void respBus(Company cc) {
        startTrip(cc.getCompany());
    }



}