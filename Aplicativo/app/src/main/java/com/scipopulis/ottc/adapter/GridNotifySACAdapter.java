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
package com.scipopulis.ottc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.scipopulis.ottc.R;


public class GridNotifySACAdapter extends BaseAdapter {

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

    private final int[] icon_sac = {
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

    private Context mContext;

    public GridNotifySACAdapter(Context cc) {
        this.mContext = cc;
    }

    @Override
    public int getCount() {
        return icon_sac.length;
    }

    @Override
    public Object getItem(int i) {
        return icon_sac[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);


            convertView = inflater.inflate(R.layout.notif_grid_sac, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.sac_logo);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setImageResource(icon_sac[position]);

            TextView campoTexto = (TextView) convertView.findViewById(R.id.sac_titulo);
            campoTexto.setText(notifs[position]);


        return convertView;
    }

}
