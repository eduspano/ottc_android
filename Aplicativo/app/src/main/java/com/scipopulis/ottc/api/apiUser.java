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

package com.scipopulis.ottc.api;

import android.util.Log;

import com.google.gson.JsonObject;
import com.scipopulis.ottc.Constants;
import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.R;
import com.scipopulis.ottc.model.User;
import com.scipopulis.ottc.api.object.Login;
import com.scipopulis.ottc.api.object.Reset;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


public class apiUser {

    RestAdapter retrofit = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(Constants.HOST)
            .build();


    public interface UserService{
        @FormUrlEncoded
        @POST("/api/v1/user/signup")
        void add(@Field("name") String name,
                 @Field("cpf") String cpf,
                 @Field("email") String email,
                 @Field("user_id") String user_id,
                 @Field("gender") String gender,
                 @Field("password") String password,
                 Callback<JsonObject> callback);



        @FormUrlEncoded
        @POST("/api/v1/user/update")
        void update(@Field("user_id") String user_id,//nao precisa
                 @Field("name") String name,
                 @Field("password") String password,//nao precisa
                 @Field("gender") String gender,
                 @Field("password_new") String password_new,
                 @Field("email") String email,
                 Callback<JsonObject> callback);

        @FormUrlEncoded
        @POST("/api/v1/user/update")
        void pushToken(@Field("email") String email,
                    @Field("password") String password,
                    @Field("push_token") String pushToken,
                    Callback<JsonObject> callback);
    }


    public void addUser(User user){

        UserService service = retrofit.create(UserService.class);

        service.add(user.getName(), user.getCpf(), user.getEmail(), user.getCpf(), user.getGender(), user.getPassword(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject response, Response response2) {
                if( response.get("status").getAsString().equals("ok") ) {
                    MainActivity.bus.post("ok");
                }else{
                    MainActivity.bus.post( response.get("message").getAsString() );
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MainActivity.bus.post(Constants.ErrorRetrofit);
            }
        });

    }


    public void updateUser(final User user, String old_pass, String new_pass){

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", "Bearer " + user.getToken());
            }
        };

        RestAdapter retrofit = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.HOST)
                .setRequestInterceptor(requestInterceptor)
                .build();

        UserService service = retrofit.create(UserService.class);

        service.update(user.getCpf(), user.getName(), old_pass, user.getGender(), new_pass, user.getEmail(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject response, Response response2) {
                if( response.get("status").getAsString().equals("ok") ) {
                    MainActivity.bus.post("ok");
                }else{
                    MainActivity.bus.post( response.get("message").getAsString() );
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MainActivity.bus.post(Constants.ErrorRetrofit);
            }
        });

    }

    public void pushToken(String email, String password, String token){

        UserService service = retrofit.create(UserService.class);

        service.pushToken(email, password, token, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                if( jsonObject.get("status").getAsString().equals("ok") ) {
                    Log.d("PUSH_TOKEN", "Push token successfully updated");
                } else{
                    Log.d("PUSH_TOKEN", jsonObject.get("message").getAsString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("PUSH_TOKEN", "Erro atualizando token para mensagens push");
            }
        });
    }


    public interface LoginService{
        @FormUrlEncoded
        @POST("/api/v1/user/login")
        void add(@Field("email") String email,
                 @Field("password") String password,
                 Callback<JsonObject> callback);
    }

    public void addLogin(String email, String password){

        LoginService service = retrofit.create(LoginService.class);

        service.add(email, password, new Callback<JsonObject>() {
            Login ll = new Login();
            @Override
            public void success(JsonObject response, Response response2) {
                if( response.get("status").getAsString().equals("error") ) {
                    ll.setError( response.get("message").getAsString() );
                }else{
                    ll.setUsuario(response.get("message").getAsJsonObject().get("user").getAsJsonObject(), response.get("message").getAsJsonObject().get("access_token").getAsString());
                }
                MainActivity.bus.post(ll);
            }

            @Override
            public void failure(RetrofitError error) {
                ll.setError(Constants.ErrorRetrofit);
                MainActivity.bus.post(ll);
            }
        });

    }




    public interface ResetService{
        @FormUrlEncoded
        @POST("/api/v1/user/resetpassword")
        void add(@Field("email") String email,
                       Callback<JsonObject> callback);
    }

    public void addReset(String email){

        ResetService service = retrofit.create(ResetService.class);

        service.add(email, new Callback<JsonObject>() {

            Reset rr = new Reset();

            @Override
            public void success(JsonObject response, Response response2) {
                if( response.get("status").getAsString().equals("error") ) {
                    rr.setError(response.get("message").getAsString());
                }else{
                    rr.setResult(response);
                }
                MainActivity.bus.post(rr);
            }

            @Override
            public void failure(RetrofitError error) {
                rr.setError(Constants.ErrorRetrofit);
                MainActivity.bus.post(rr);
            }
        });

    }


}
