package com.nomad.mrg5s.View.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nomad.mrg5s.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentZoom extends Fragment {

    public static final String RUTAFOTO="RUTAFOTO";
    public static final String COMENTARIOFOTO="COMENTARIOFOTO";
    private String rutaFoto;
    private String comentarioFoto;


    public FragmentZoom() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_zoom, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            rutaFoto=bundle.getString(RUTAFOTO);
            comentarioFoto=bundle.getString(COMENTARIOFOTO);
        }

        PhotoView laFoto= view.findViewById(R.id.imagenZoomeada);
        TextView elTexto= view.findViewById(R.id.textoImagenZoomeada);
        if (comentarioFoto!=null && !comentarioFoto.isEmpty()) {
            elTexto.setVisibility(View.VISIBLE);
            elTexto.setText(comentarioFoto);
        }
        else {
           elTexto.setVisibility(View.GONE);
        }
        File f = new File(rutaFoto);
        Picasso.with(getContext())
                .load(f)
                .into(laFoto);
        return view;
    }

}