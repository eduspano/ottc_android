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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.scipopulis.ottc.api.apiUser;
import com.scipopulis.ottc.dao.UserDAO;
import com.scipopulis.ottc.helper.AccountHelper;
import com.scipopulis.ottc.model.User;
import com.squareup.otto.Subscribe;

import java.io.File;

public class AccountActivity extends AppCompatActivity {

    private AccountHelper helper;
    public static final int CAMERA_CODE = 567;
    private Menu menu;
    private String picture;
    private File file;
    private User user;
    private String old_pass;
    private boolean view_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_account);

        setTitle("Cadastrar");

        MainActivity.bus.register(this);

        helper = new AccountHelper(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if(user!=null){
            view_edit = true;
            setTitle("Perfil");
            old_pass = user.getPassword();
            helper.setForm(user);
            String path = user.getPicture();
            if (path != null && !path.isEmpty() && !path.equals("null"))
                helper.getPicture(path, getCameraPhotoOrientation(AccountActivity.this,Uri.fromFile(new File(path)), path) );
        }

        Button Pic_but = (Button) findViewById(R.id.picture_button);
        Pic_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            picture = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
            file = new File(picture);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intentCamera, CAMERA_CODE);
            }

        });


        final CheckBox checkBoxMale = (CheckBox) findViewById(R.id.form_male);
        final CheckBox checkBoxFem = (CheckBox) findViewById(R.id.form_female);

        checkBoxMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
            if ( isChecked )
            {
                if (checkBoxMale.isChecked()) {
                    checkBoxFem.setChecked(false);
                }
            }
            }
        });

        checkBoxFem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
            if ( isChecked )
            {
                if (checkBoxFem.isChecked()) {
                    checkBoxMale.setChecked(false);
                }
            }
            }
        });


        if(user!=null){
            helper.viewUserData();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                helper.getPicture(picture, getCameraPhotoOrientation(AccountActivity.this,Uri.fromFile(file),picture));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_form, menu);

        if(user!=null){
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }else{
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        }

        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_edit:
                helper.viewUserEdit();
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                break;
            case R.id.menu_ok:
                user = helper.getUser();
                if( helper.check(user, view_edit, old_pass, AccountActivity.this) ) {
                    apiUser userAPI = new apiUser();
                    if( user.getId()==null ) {
                        userAPI.addUser(user);
                    }else {
                        user.setPassword( helper.getPass(old_pass) );
                        userAPI.updateUser(user,old_pass, helper.getPass(old_pass) );
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    @Subscribe
    public void respBus(String resp) {
        if( resp.equals("ok") ){
            Toast.makeText(AccountActivity.this,"Operação realizada com sucesso.",Toast.LENGTH_SHORT).show();

            UserDAO dao = new UserDAO(AccountActivity.this);
            if (view_edit == true) {
                dao.update(user);
            } else {
                dao.insert(user);
            }
            dao.close();

            finish();
        }else{
            Toast.makeText(AccountActivity.this,resp,Toast.LENGTH_SHORT).show();
        }
    }



}
