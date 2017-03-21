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
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.scipopulis.ottc.api.apiUser;
import com.scipopulis.ottc.dao.UserDAO;
import com.scipopulis.ottc.model.User;
import com.scipopulis.ottc.api.object.Login;
import com.squareup.otto.Subscribe;

public class LoginActivity extends AppCompatActivity {

    private User user;
    private UserDAO dao;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MainActivity.bus.register(this);

        TextView forgot_login = (TextView) findViewById(R.id.forgot_login);
        forgot_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intentform = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(intentform);
            }
        });

        TextView sign_login = (TextView) findViewById(R.id.sign_login);
        sign_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intentform = new Intent(LoginActivity.this, AccountActivity.class);
            startActivity(intentform);
            }
        });

        Button send_but = (Button) findViewById(R.id.send_but);
        send_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            AutoCompleteTextView auto_email = (AutoCompleteTextView) findViewById(R.id.login_email);
            String email = auto_email.getText().toString();

            EditText pass = (EditText) findViewById(R.id.login_pass);
            password = pass.getText().toString();

            dao = new UserDAO(LoginActivity.this);
            user = dao.search(email,password);
            apiUser userAPI = new apiUser();

            if(user!=null) {
                user.setLogged(1);
                dao.update(user);

                Intent intentform = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intentform);
            } else{
                userAPI.addLogin(email,password);
            }

            OTTCApplication app=(OTTCApplication)getApplication();
            if(app.getPushToken()!=null) {
                userAPI.pushToken(email, password, app.getPushToken());
            }

            dao.close();

            }

        });

    }


    @Subscribe
    public void respBus(Login result) {
        if(result.getError().isEmpty()){
            user = result.getUsuario();
            user.setPassword(password);
            user.setLogged(1);
            user.setTerms(1);
            dao.insert(user);

            Intent intentform = new Intent(LoginActivity.this, TabActivity.class);
            intentform.putExtra("user", user);
            startActivity(intentform);

        }else{
            Toast.makeText(LoginActivity.this, result.getError(), Toast.LENGTH_LONG).show();
        }
    }


}
