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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.scipopulis.ottc.api.apiOperator;
import com.scipopulis.ottc.controller.BackgroundMode;
import com.scipopulis.ottc.controller.Bubble;
import com.scipopulis.ottc.dao.UserDAO;
import com.scipopulis.ottc.model.User;
import com.scipopulis.ottc.api.object.Operator;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity {

    public static Bus bus = new Bus();
    private static final int NOTIFICATION_ID = 1234;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        MainActivity.bus.register(this);


        apiOperator op = new apiOperator();
        op.getLogo(MainActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, NOTIFICATION_ID);
            }else{
                init();
            }
        }else{
            init();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTIFICATION_ID) {

        }
    }


    private void init(){

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE}, 123);
        }else{
            carregar();
        }

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        carregar();
    }


    private void carregar(){
        UserDAO dao = new UserDAO(this);
        user = dao.logged();
        dao.close();

        if(user!=null){
            Intent intentform = new Intent(MainActivity.this, TabActivity.class);
            intentform.putExtra("user", user);
            startActivity(intentform);
        }else{
            Intent intentform = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentform);
        }
    }


    @Subscribe
    public void respBus(Operator result) {
        if(result.getError().isEmpty()){
            String opers = TextUtils.join(", ", result.getOpers());

            SharedPreferences.Editor editor = getSharedPreferences("key_op", MODE_PRIVATE).edit();
            editor.putString("operator", opers);
            editor.commit();
        }else{
            Toast.makeText(MainActivity.this,"Verifique sua conexão e tente novamente",Toast.LENGTH_SHORT).show();
        }
    }

}