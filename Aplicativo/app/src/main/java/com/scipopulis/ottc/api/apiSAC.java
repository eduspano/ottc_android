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


import com.scipopulis.ottc.Constants;
import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.api.object.Sac;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;


public class apiSAC {

    public interface SACService{
        @POST(Constants.SACEndpoint)
        void add(@Body Sac dd,
                 Callback<String> callback);
    }

    public void addNotification(final Sac objeto){

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", "Token "+ Constants.SACKey);
            }
        };

        RestAdapter retrofit = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.SACAddress)
                .setRequestInterceptor(requestInterceptor)
                .build();

        SACService service = retrofit.create(SACService.class);

        service.add(objeto, new Callback<String>() {
            @Override
            public void success(String response, Response response2) {
                objeto.setError("ok");
                MainActivity.bus.post(objeto);
            }

            @Override
            public void failure(RetrofitError error) {
                objeto.setError("Erro de conexão da internet, tente novamente mais tarde.");
                MainActivity.bus.post(objeto);
            }
        });

    }

}
