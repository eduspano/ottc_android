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
package com.scipopulis.ottc.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scipopulis.ottc.AccountActivity;
import com.scipopulis.ottc.R;
import com.scipopulis.ottc.model.User;


public class AccountHelper {
    private final EditText field_name;
    private final EditText field_cpf;
    private final EditText field_email;
    private final EditText field_email2;
    private final ImageView field_picture;
    private final EditText field_pass;
    private final CheckBox field_term;
    private final CheckBox field_male;
    private final CheckBox field_female;
    private final EditText field_actual_pass;
    private final EditText field_new_pass;
    private final EditText field_new_pass_confirm;
    private final LinearLayout field_box_cpf;

    private KeyListener listener;

    private final Button icon_camera;
    private final LinearLayout view_alt_pass;

    private User user;

    public AccountHelper(AccountActivity activity){
        field_name = (EditText) activity.findViewById(R.id.form_name);
        field_cpf = (EditText) activity.findViewById(R.id.form_cpf);

        field_male = (CheckBox) activity.findViewById(R.id.form_male);
        field_female = (CheckBox) activity.findViewById(R.id.form_female);

        field_email = (EditText) activity.findViewById(R.id.form_email);
        field_email2 = (EditText) activity.findViewById(R.id.form_email2);
        field_pass = (EditText) activity.findViewById(R.id.form_pass);
        field_picture = (ImageView) activity.findViewById(R.id.form_picture);
        field_term = (CheckBox) activity.findViewById(R.id.form_term);
        user = new User();

        field_actual_pass = (EditText) activity.findViewById(R.id.form_actual_pass);
        field_new_pass = (EditText) activity.findViewById(R.id.form_new_pass);
        field_new_pass_confirm = (EditText) activity.findViewById(R.id.form_new_pass_confirm);

        field_box_cpf = (LinearLayout) activity.findViewById(R.id.box_cpf);
                
        listener = field_name.getKeyListener();

        icon_camera = (Button) activity.findViewById(R.id.picture_button);
        icon_camera.setVisibility(LinearLayout.VISIBLE);

        view_alt_pass = (LinearLayout) activity.findViewById(R.id.view_alt_pass);
        view_alt_pass.setVisibility(LinearLayout.GONE);
    }

    public void viewUserData(){
        field_name.setKeyListener(null);
        field_cpf.setKeyListener(null);
        field_email.setKeyListener(null);
        field_email2.setVisibility(EditText.GONE);
        field_pass.setVisibility(EditText.GONE);
        field_male.setEnabled(false);
        field_female.setEnabled(false);
        field_term.setEnabled(false);

        view_alt_pass.setVisibility(LinearLayout.GONE);
        icon_camera.setVisibility(LinearLayout.GONE);
    }

    public void viewUserEdit(){
        field_name.setKeyListener(listener);
        field_box_cpf.setVisibility(LinearLayout.GONE);
        field_email.setKeyListener(listener);
        field_email2.setVisibility(EditText.VISIBLE);
        field_pass.setVisibility(EditText.GONE);
        field_male.setEnabled(true);
        field_female.setEnabled(true);
        field_term.setEnabled(true);

        view_alt_pass.setVisibility(LinearLayout.VISIBLE);
        icon_camera.setVisibility(LinearLayout.VISIBLE);
    }

    public User getUser() {
        user.setName(field_name.getText().toString());
        user.setCpf(field_cpf.getText().toString());
        user.setEmail(field_email.getText().toString());
        user.setPicture( (String) field_picture.getTag() );
        user.setPassword(field_pass.getText().toString());

        if(field_male.isChecked())
            user.setGender("masculino");
        else
            user.setGender("feminino");

        if( field_term.isChecked()  )
            user.setTerms( 1 );
        else
            user.setTerms( 0 );

        return user;
    }

    public void setForm(User user) {
        field_name.setText(user.getName());
        field_cpf.setText(user.getCpf());
        field_email.setText(user.getEmail());
        field_email2.setText(user.getEmail());
        field_term.setChecked( user.getTerms()==1 );
        field_male.setChecked( user.getGender().equals("masculino") );
        field_female.setChecked( user.getGender().equals("feminino") );
        field_term.setChecked( true );

        this.user = user;
    }

    public void getPicture(String picture, int rotate) {
        if(picture!=null) {

            Bitmap bitmap = BitmapFactory.decodeFile(picture);

            int outWidth;
            int outHeight;
            int inWidth = bitmap.getWidth();
            int inHeight = bitmap.getHeight();
            if(inWidth > inHeight){
                outWidth = 300;
                outHeight = (inHeight * outWidth) / inWidth;
            } else {
                outHeight = 140;
                outWidth = (inWidth * outHeight) / inHeight;
            }

            Bitmap bitmapReduzido = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, true);
            field_picture.setImageBitmap(bitmapReduzido);
            field_picture.setScaleType(ImageView.ScaleType.FIT_XY);
            field_picture.setTag(picture);

            if(rotate>=0)
                field_picture.setRotation(rotate);
        }
    }

    public String getPass(String old_pass){
        if( field_new_pass.getText().toString().equals( field_new_pass_confirm.getText().toString() ) && field_new_pass.getText().length()>3 ){
            return field_new_pass.getText().toString();
        }else{
            return old_pass;
        }
    }

    public boolean check(User user, boolean view_edit, String old_pass, Context context){

        if (TextUtils.isEmpty(user.getName())) {
            field_name.setError("Campo vazio");

            return false;
        }

        else if ( field_male.isChecked()==false && field_female.isChecked()==false ) {
            Toast.makeText(context, "Obrigatorio escolha do sexo", Toast.LENGTH_SHORT).show();

            return false;
        }


        else if (TextUtils.isEmpty(user.getCpf()) && view_edit != true) {
            field_cpf.setError("Campo vazio");
            field_cpf.requestFocus();
            return false;
        } else if (!isCPFValid(user.getCpf()) && view_edit != true) {
            field_cpf.setError("CPF invalido");
            field_cpf.requestFocus();
            return false;
        }


        else if (TextUtils.isEmpty(user.getEmail())) {
            field_email.setError("Campo vazio");
            field_email.requestFocus();
            return false;
        } else if (!isEmailValid(user.getEmail())) {
            field_email.setError("Email invalido");
            field_email.requestFocus();
            return false;
        } else if ( !user.getEmail().equals(field_email2.getText().toString()) ) {
            field_email2.setError("Email não conferem");
            field_email2.requestFocus();
            return false;
        }

        else if ( !field_term.isChecked() ) {
            Toast.makeText(context, "Obrigatorio aceite do termo", Toast.LENGTH_SHORT).show();
            field_term.setError("Obrigatorio aceite do termo");
            return false;
        }


        else if( !view_edit ){
            if (TextUtils.isEmpty(user.getPassword())) {
                field_pass.setError("Campo vazio");
                field_pass.requestFocus();
                return false;
            }
            else if (user.getPassword().length() < 4) {
                field_pass.setError("Senha pequena");
                field_pass.requestFocus();
                return false;
            }

            return true;
        }


        else if( !TextUtils.isEmpty( field_actual_pass.getText().toString() ) ){

            if( view_edit ){
                if( TextUtils.isEmpty( field_actual_pass.getText().toString() ) ){
                    field_actual_pass.setError("Campo vazio");
                    field_actual_pass.requestFocus();
                    return false;
                }
                else if( TextUtils.isEmpty( field_new_pass.getText().toString() ) ){
                    field_new_pass.setError("Campo vazio");
                    field_new_pass.requestFocus();
                    return false;
                }
                else if( TextUtils.isEmpty( field_new_pass_confirm.getText().toString() ) ){
                    field_new_pass_confirm.setError("Campo vazio");
                    field_new_pass_confirm.requestFocus();
                    return false;
                }

                else if (field_actual_pass.getText().toString().length() < 4) {
                    field_actual_pass.setError("Senha pequena");
                    field_actual_pass.requestFocus();
                    return false;
                }
                else if (field_new_pass.getText().toString().length() < 4) {
                    field_new_pass.setError("Senha pequena");
                    field_new_pass.requestFocus();
                    return false;
                }
                else if (field_new_pass_confirm.getText().toString().length() < 4) {
                    field_new_pass_confirm.setError("Senha pequena");
                    field_new_pass_confirm.requestFocus();
                    return false;
                }
                else if ( !field_new_pass_confirm.getText().toString().equals( field_new_pass.getText().toString() ) ) {
                    field_new_pass.setError("Senhas diferentes");
                    field_new_pass.requestFocus();
                    return false;
                }
                else if ( !field_actual_pass.getText().toString().equals( old_pass ) ) {
                    field_actual_pass.setError("Senha anterior inválida");
                    field_actual_pass.requestFocus();
                    return false;
                }
            }

            return true;
        }

        else{
            return true;
        }


    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isCPFValid(String cpf) {
        int soma=0;
        int resto;

        if (cpf == "00000000000" || cpf.length()<11) {
            return false;
        }

        for (int i=1; i<=9; i++) {
            soma = soma + Integer.parseInt(cpf.substring(i - 1, i)) * (11 - i);
        }
        resto = (soma * 10) % 11;

        if ((resto == 10) || (resto == 11)) {
            resto = 0;
        }

        if (resto != Integer.parseInt(cpf.substring(9, 10)) ){
            return false;
        }

        soma = 0;
        for (int i = 1; i <= 10; i++) {
            soma = soma + Integer.parseInt(cpf.substring(i - 1, i)) * (12 - i);
        }
        resto = (soma * 10) % 11;

        if ((resto == 10) || (resto == 11)) {
            resto = 0;
        }

        if (resto != Integer.parseInt(cpf.substring(10, 11) ) ) {
            return false;
        }

        return true;
    }


}
