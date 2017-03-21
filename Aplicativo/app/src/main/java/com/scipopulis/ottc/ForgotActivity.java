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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.scipopulis.ottc.api.apiUser;
import com.scipopulis.ottc.api.object.Reset;
import com.squareup.otto.Subscribe;

public class ForgotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        MainActivity.bus.register(this);

        Button forgot_but = (Button) findViewById(R.id.send);
        forgot_but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            AutoCompleteTextView auto_email = (AutoCompleteTextView) findViewById(R.id.email);
            String email = auto_email.getText().toString();

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                auto_email.setError("Campo vazio");
            } else if (!isEmailValid(email)) {
                auto_email.setError("Email invalido");
            }else{
                apiUser userAPI = new apiUser();
                userAPI.addReset(email);
            }

            }

        });
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    @Subscribe
    public void respBus(Reset result) {
        if(result.getError().isEmpty()){
            Toast.makeText(ForgotActivity.this,"Senha enviada com sucesso!",Toast.LENGTH_LONG).show();
            finish();
        }else{
            Toast.makeText(ForgotActivity.this, "Verifique seu email e tente novamente",Toast.LENGTH_LONG).show();
        }
    }


}
