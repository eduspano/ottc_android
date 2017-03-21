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

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import com.scipopulis.ottc.MainActivity;
import com.scipopulis.ottc.R;


public class BackgroundMode extends Service {

    protected Integer NOTIFICATION_ID = 23213123; // Some random integer

    private LoadNotification loadNotification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        loadNotification = new LoadNotification("OTTC", "Em execução..");
        loadNotification.notifyMessage();

        return START_STICKY;
    }

    class LoadNotification {

        private String titleMessage;
        private String textMessage;


        public LoadNotification(String titleMessage, String textMessage) {
            this.titleMessage = titleMessage;
            this.textMessage = textMessage;
        }

        public void notifyMessage() {
            NotificationCompat.Builder builder = getNotificationBuilder(MainActivity.class);
            startForeground(NOTIFICATION_ID, builder.build());
        }

        protected NotificationCompat.Builder getNotificationBuilder(Class clazz) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

            builder.setSmallIcon(R.drawable.ic_account_box_black);  // icon id of the image

            builder.setContentTitle(this.titleMessage)
                    .setContentText(this.textMessage)
                    .setContentInfo("Aplicativo");

            Intent foregroundIntent = new Intent(getApplicationContext(), clazz);

            foregroundIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, foregroundIntent, 0);

            builder.setContentIntent(contentIntent);
            return builder;
        }

    }

}
