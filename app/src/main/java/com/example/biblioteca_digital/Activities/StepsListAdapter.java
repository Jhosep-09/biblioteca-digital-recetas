package com.example.biblioteca_digital.Activities;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.biblioteca_digital.R;

import java.util.ArrayList;

public class StepsListAdapter extends android.widget.BaseAdapter{

    private final android.content.Context context;
    private final ArrayList<String> descripciones;
    private final ArrayList<String> imagenes;

    public StepsListAdapter(android.content.Context context,
                            ArrayList<String> descripciones,
                            ArrayList<String> imagenes) {
        this.context = context;
        this.descripciones = descripciones;
        this.imagenes = imagenes;
    }

    @Override
    public int getCount() {
        return descripciones.size();
    }

    @Override
    public Object getItem(int position) {
        return descripciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public android.view.View getView(int position, android.view.View convertView,
                                     android.view.ViewGroup parent) {

        if (convertView == null) {
            convertView = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.item_paso, parent, false);
        }

        TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcionPaso);
        ImageView ivPaso = convertView.findViewById(R.id.ivImagenPaso);

        String desc = descripciones.get(position);
        String imgUrl = imagenes.size() > position ? imagenes.get(position) : null;

        tvDescripcion.setText((position + 1) + ". " + desc);

        if (imgUrl != null && !imgUrl.isEmpty() && imgUrl.startsWith("http")) {
            ivPaso.setVisibility(android.view.View.VISIBLE);
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.nofot)
                    .error(R.drawable.nofot)
                    .into(ivPaso);
        } else {
            // si no hay imagen, ocultamos el ImageView
            ivPaso.setVisibility(android.view.View.GONE);
        }

        return convertView;
    }
}
