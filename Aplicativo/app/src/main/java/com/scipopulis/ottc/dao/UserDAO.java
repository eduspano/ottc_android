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

package com.scipopulis.ottc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.scipopulis.ottc.model.User;


public class UserDAO extends SQLiteOpenHelper {
    private Context mContext;

    public UserDAO(Context cc) {
        super(cc, "Account", null, 1);
        this.mContext=cc;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String  sql = "CREATE TABLE User (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, cpf TEXT NOT NULL, gender TEXT NOT NULL, terms INTEGER DEFAULT 0, email TEXT NOT NULL, password TEXT NOT NULL, picture TEXT, logged INTEGER DEFAULT 0, token TEXT );";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i==2) {
            String sql = "DROP TABLE IF EXISTS User";
            sqLiteDatabase.execSQL(sql);
            onCreate(sqLiteDatabase);
        }
    }

    public void insert(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues data = getContentValuesUser(user);

        db.insert("User", null, data);
    }

    @NonNull
    private ContentValues getContentValuesUser(User user) {
        ContentValues data = new ContentValues();
        data.put("name", user.getName());
        data.put("cpf", user.getCpf());
        data.put("gender", user.getGender());
        data.put("terms", user.getTerms());
        data.put("email", user.getEmail());
        data.put("password", user.getPassword());
        data.put("picture", user.getPicture());
        data.put("logged", user.getLogged());
        data.put("token", user.getToken());
        return data;
    }

    public User search(String email, String pass) {
        String[] args={email,pass};
        SQLiteDatabase db = getReadableDatabase();
        Cursor c=db.rawQuery("Select * from User WHERE email = ? AND password = ?;",args);

        User user = new User();

        if (c.moveToFirst()) {
            user.setId(c.getLong(c.getColumnIndex("id")));
            user.setName(c.getString(c.getColumnIndex("name")));
            user.setCpf(c.getString(c.getColumnIndex("cpf")));
            user.setGender(c.getString(c.getColumnIndex("gender")));
            user.setTerms(c.getInt(c.getColumnIndex("terms")));
            user.setEmail(c.getString(c.getColumnIndex("email")));
            user.setPassword(c.getString(c.getColumnIndex("password")));
            user.setPicture(c.getString(c.getColumnIndex("picture")));
            user.setLogged(c.getInt(c.getColumnIndex("logged")));
            user.setToken(c.getString(c.getColumnIndex("token")));
        }else{
            user=null;
        }

        c.close();
        return user;
    }

    public void delete(User user){
        SQLiteDatabase db = getWritableDatabase();

        String[] params = {user.getId().toString()};
        db.delete("User", "id = ?", params);
    }

    public void update(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues data = getContentValuesUser(user);

        String[] params = {user.getId().toString()};
        db.update("User",data,"id = ?",params);
    }


    public User logged() {
        String sql = "Select * from User WHERE logged = 1;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        User user = new User();

        if (c.moveToFirst()) {
            user.setId(c.getLong(c.getColumnIndex("id")));
            user.setName(c.getString(c.getColumnIndex("name")));
            user.setCpf(c.getString(c.getColumnIndex("cpf")));
            user.setGender(c.getString(c.getColumnIndex("gender")));
            user.setTerms(c.getInt(c.getColumnIndex("terms")));
            user.setEmail(c.getString(c.getColumnIndex("email")));
            user.setPassword(c.getString(c.getColumnIndex("password")));
            user.setPicture(c.getString(c.getColumnIndex("picture")));
            user.setLogged(c.getInt(c.getColumnIndex("logged")));
            user.setToken(c.getString(c.getColumnIndex("token")));
        }else{
            user=null;
        }

        c.close();
        return user;
    }
}
