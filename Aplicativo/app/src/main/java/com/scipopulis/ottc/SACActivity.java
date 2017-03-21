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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scipopulis.ottc.api.apiSAC;
import com.scipopulis.ottc.api.apiUser;
import com.scipopulis.ottc.api.object.Endereco;
import com.scipopulis.ottc.api.object.Sac;
import com.scipopulis.ottc.api.object.Solicitante;
import com.scipopulis.ottc.api.object.Telefone;
import com.scipopulis.ottc.dao.UserDAO;
import com.scipopulis.ottc.model.User;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.List;

public class SACActivity extends AppCompatActivity {

    private final String[] notifs = {
            "Desefa de Autuação",
            "Consulta de multas de trânsito",
            "Recursos de multas de trânsito",
            "Restituição de multas pagas e deferidas",
            "Reclamação de eventos na via",
            "Aplicação de Advertência por Escrito",
            "2a via notificação de autuação de trânsito",
            "2a via auto de infração de trânsito",
            "2a via notificação de penalidade de trânsito",
            "Emissão de boleto pagamento",
            "Aviso de interferências no trânsito",
            "Recurso/defesa de Multa inscrita no CADIN",
            "Indicação de Condutor",
            "Criação/ampliação de estacionamento",
            "Denúncia de má conduta de funcionário da CET"
    };

    private final int[] logos = {
            R.drawable.ic_defesa_de_autuacao,
            R.drawable.ic_consulta_multa,
            R.drawable.ic_recursos_de_multas,
            R.drawable.ic_restituicao_de_multa,
            R.drawable.ic_reclamacao_de_eventos,
            R.drawable.ic_aplicacao_de_advertencia,
            R.drawable.ic_2via_notificacao_autuacao,
            R.drawable.ic_2via_auto_de_inflacao,
            R.drawable.ic_2via_notificacao_penalidade,
            R.drawable.ic_emissao_boleto,
            R.drawable.ic_aviso_interferencias,
            R.drawable.ic_defesa_multa_cadin,
            R.drawable.ic_indicacao_de_condutor,
            R.drawable.ic_criacao_estacionamento,
            R.drawable.ic_denuncia_ma_conduta_cet
    };

    private final int[] sac_codes = {
            918,
            920,
            919,
            921,
            769,
            926,
            923,
            922,
            924,
            925,
            1556,
            927,
            928,
            761,
            768
    };

    private Sac object;
    private int pkindice;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_sac);

        MainActivity.bus.register(this);


        Intent intent = getIntent();
        int indice = (int) intent.getSerializableExtra("notification");

        TextView notif_sac = (TextView) findViewById(R.id.notif_sac);
        notif_sac.setText(notifs[indice]);

        ImageView imageView = (ImageView) findViewById(R.id.logo_sac);
        imageView.setImageResource(logos[indice]);

        pkindice = indice;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sac, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_send:
                send_sac();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void send_sac(){

        progress = new ProgressDialog(SACActivity.this);
        progress.setTitle("Enviando");
        progress.setMessage("Aguarde enquanto enviamos sua notificação...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
        progress.show();

        EditText descricao = (EditText) findViewById(R.id.sac_desc);
        EditText logradouro = (EditText) findViewById(R.id.sac_logra);

        Geocoder geocoder = new Geocoder(SACActivity.this);
        List<Address> resultados = null;
        double lat = -0.0;
        double lng = -0.0;
        String rua = "";
        String bairro = "";
        String cep = "";
        String cidade = "";
        String uf = "";
        try {
            resultados = geocoder.getFromLocationName( logradouro.getText().toString(), 1 );
            if (!resultados.isEmpty()) {
                lat = resultados.get(0).getLatitude();
                lng = resultados.get(0).getLongitude();
                rua = resultados.get(0).getAddressLine(0);
                bairro = resultados.get(0).getSubAdminArea();
                cep = resultados.get(0).getPostalCode();
                cidade = resultados.get(0).getLocality();
                uf = "SP";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        object = new Sac();
        object.setAssunto(sac_codes[pkindice]);
        object.setDescricao( descricao.getText().toString() );

        Endereco end = new Endereco();
        end.setBairro(bairro);
        end.setCep(cep);
        end.setCidade(cidade);
        end.setLatitude( String.valueOf(lat) );
        end.setLongitude( String.valueOf(lng) );
        end.setLogradouro(rua);
        end.setNumero(0);
        end.setReferencia("none");
        end.setUf(uf);

        object.setEndereco(end);


        UserDAO dao = new UserDAO(SACActivity.this);
        User usuario = dao.logged();
        dao.close();


        Solicitante sol = new Solicitante();
        sol.setCpf(usuario.getCpf());
        sol.setEmail(usuario.getEmail());
        sol.setNome(usuario.getName());


        TelephonyManager tMgr = (TelephonyManager) getSystemService(SACActivity.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        int numtel = 12345678;
        //if(!mPhoneNumber.isEmpty())
        //    numtel = Long.parseLong( mPhoneNumber.toString() );

        Telefone tel = new Telefone();
        tel.setDdd(11);
        tel.setNumero( numtel );
        tel.setPreferencial(true);
        tel.setRamal(0);
        tel.setSms(true);
        tel.setTipo("CELULAR");

        sol.setTelefone(tel);

        object.setSolicitante(sol);

        apiSAC sacAPI = new apiSAC();
        sacAPI.addNotification(object);
    }


    @Subscribe
    public void respBus(Sac sac) {
        
        progress.dismiss();
        
        if( sac.getError().equals("ok") ){
            Toast.makeText(SACActivity.this,"Enviado com sucesso!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(SACActivity.this,sac.getError().toString(),Toast.LENGTH_LONG).show();
        }

    }


}
