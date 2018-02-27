package com.auditoria.grilla5s.View.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.auditoria.grilla5s.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_fragment_zoom, container, false);
        Bundle bundle = getArguments();

        rutaFoto=bundle.getString(RUTAFOTO);
        comentarioFoto=bundle.getString(COMENTARIOFOTO);

        PhotoView laFoto=(PhotoView) view.findViewById(R.id.imagenZoomeada);
        TextView elTexto=(TextView) view.findViewById(R.id.textoImagenZoomeada);
        elTexto.setText(comentarioFoto);
        File f = new File(rutaFoto);
        Picasso.with(getContext())
                .load(f)
                .into(laFoto);
        return view;
    }

}
