package com.nomad.mrg5s.View.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.Model.Area;
import com.nomad.mrg5s.Model.Foto;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.FuncionesPublicas;
import com.nomad.mrg5s.View.Adapter.AdapterArea;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSeleccionArea extends Fragment {

    private RealmList<Area> listaAreas;
    private RecyclerView recyclerAreas;
    private AdapterArea adapterArea;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton fabNuevaArea;
    private TextView textoSinAreas;

    private File fotoOriginal;
    private File fotoComprimida;

    private Notificable notificable;
    private LinearLayout linear;

    public FragmentSeleccionArea() {
        // Required empty public constructor
    }

    public interface Notificable{
        public void comenzarAuditoria(Area unArea);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_seleccion_aerea, container, false);
        linear=view.findViewById(R.id.vistaCentral);
        textoSinAreas =view.findViewById(R.id.textoSinAreas);
        //String usuario= FirebaseAuth.getInstance().getCurrentUser().getEmail();

        tengoQueMostrarTextoSinAreas();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Area> result2 = realm.where(Area.class)
                //las areas son de todos los usuarios//    .equalTo("usuario", usuario)
                .findAll();

        listaAreas=new RealmList<>();
        listaAreas.addAll(result2);
        recyclerAreas= view.findViewById(R.id.recyclerArea);
        adapterArea= new AdapterArea();
        adapterArea.setContext(getContext());
        layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerAreas.setLayoutManager(layoutManager);
        adapterArea.setListaAreasOriginales(listaAreas);
        recyclerAreas.setAdapter(adapterArea);

        View.OnClickListener listenerArea = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerAreas.getChildAdapterPosition(v);
                RealmList<Area> listaAreas = adapterArea.getListaAreasOriginales();
                Area areaClickeada = listaAreas.get(posicion);
                notificable.comenzarAuditoria(areaClickeada);
            }
        };
        adapterArea.setListener(listenerArea);

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        Typeface robotoR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        TextView unText=toolbar.findViewById(R.id.textoToolbar);
        unText.setTypeface(robotoR);
        unText.setTextColor(getResources().getColor(R.color.blancoNomad));
        unText.setText(getResources().getString(R.string.selectArea));

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

       SharedPreferences config = getActivity().getSharedPreferences("prefs",0);
        boolean quiereVerTuto = config.getBoolean("quiereVerTuto",false);
        boolean primeraVezFragmentSubitem =config.getBoolean("primeraVezFragmentSeleccion",false);
        //SI EL USUARIO ELIGIO VER TUTORIALES ME FIJO SI YA PASO POR ESTA PAGINA.
        if (quiereVerTuto) {
            if (!primeraVezFragmentSubitem) {

                if (result2.size()>=1) {
                    SharedPreferences.Editor editor = config.edit();
                    editor.putBoolean("primeraVezFragmentSeleccion",true);
                    editor.commit();

                    seguirConTutorial();
                }
            }
        }
        
        fabNuevaArea=view.findViewById(R.id.fabNuevaArea);
        fabNuevaArea.setImageResource(R.drawable.ic_note_add_black_24dp);
        fabNuevaArea.setColorNormal(ContextCompat.getColor(fabNuevaArea.getContext(),R.color.mirgorNaranja));
        fabNuevaArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FuncionesPublicas.isExternalStorageWritable()) {
                    if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        EasyImage.openChooserWithGallery(FragmentSeleccionArea.this, getResources().getString(R.string.seleccionaImagen), 1);
                    }
                    else {
                        if (Nammu.shouldShowRequestPermissionRationale(FragmentSeleccionArea.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            //User already refused to give us this permission or removed it
                            //Now he/she can mark "never ask again" (sic!)
                            Snackbar.make(getView(), getResources().getString(R.string.appNecesitaPermiso),
                                    Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    Nammu.askForPermission(FragmentSeleccionArea.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            new PermissionCallback() {
                                                @Override
                                                public void permissionGranted() {

                                                    EasyImage.openChooserWithGallery(FragmentSeleccionArea.this, getResources().getString(R.string.seleccionaImagen), 1);
                                                }

                                                @Override
                                                public void permissionRefused() {
                                                    Toast.makeText(getContext(), getResources().getString(R.string.permisoParaFotos), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).show();
                        } else {
                            //First time asking for permission
                            // or phone doesn't offer permission
                            // or user marked "never ask again"
                            Nammu.askForPermission(FragmentSeleccionArea.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    new PermissionCallback() {
                                        @Override
                                        public void permissionGranted() {

                                            EasyImage.openChooserWithGallery(FragmentSeleccionArea.this, getResources().getString(R.string.seleccionaImagen), 1);
                                        }

                                        @Override
                                        public void permissionRefused() {
                                            Toast.makeText(getContext(), getResources().getString(R.string.permisoParaFotos), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                    }
                }
                else {
                    new MaterialDialog.Builder(getContext())
                            .title(getResources().getString(R.string.titNoMemoria))
                            .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                            .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                            .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                            .positiveText(getResources().getString(R.string.ok))
                            .content(getResources().getString(R.string.noMemoria))
                            .show();
                }
                
            }
        });
        
        return view;
    }

    private void tengoQueMostrarTextoSinAreas() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Area> result2 = realm.where(Area.class)
                //las areas son de todos los usuarios//    .equalTo("usuario", usuario)
                .findAll();

        if (result2.size() < 1) {
            textoSinAreas.setVisibility(View.VISIBLE);
        } else {
            textoSinAreas.setVisibility(View.GONE);
        }
    }

    private void seguirConTutorial() {
        Typeface roboto=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                TapTarget.forView(linear, getResources().getString(R.string.tutorial_tit_subitem_general), getResources().getString(R.string.tutorial_desc_subitem_general))
                        .transparentTarget(true)
                        .textColor(R.color.primary_text)
                        .outerCircleColor(R.color.tutorial1)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.95f)            // Specify the alpha amount for the outer circle
                        .textTypeface(roboto)  // Specify a typeface for the text
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false)
                        .icon(getResources().getDrawable(R.drawable.ic_check_black_24dp))
                        .id(1));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.notificable=(Notificable) context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                if (type == 1) {
                    fotoOriginal = imageFile;
                    existeDirectorioImagenesAreas();
                    try {
                        fotoComprimida = new Compressor(getContext())
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(getContext().getExternalFilesDir(null)+ File.separator + "nomad" + File.separator + "audit5s" +File.separator+FirebaseAuth.getInstance().getCurrentUser().getEmail()+File.separator + "images" + File.separator + "areas")
                                .compressToFile(fotoOriginal);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Foto unaFoto = new Foto();
                    unaFoto.setIdFoto("foto_"+ UUID.randomUUID());
                    unaFoto.setRutaFoto(fotoComprimida.getAbsolutePath());
                    if (source == EasyImage.ImageSource.CAMERA) {
                        Boolean seBorro = imageFile.delete();
                        if (seBorro) {
                            //   Toast.makeText(getContext(), R.string.seEliminoFoto, Toast.LENGTH_SHORT).show();

                        } else {
                            // Toast.makeText(getContext(), R.string.noSeEliminoFoto, Toast.LENGTH_SHORT).show();
                        }
                    }
                    FuncionesPublicas.crearDialogoNombreArea(unaFoto, FragmentSeleccionArea.this,FuncionesPublicas.SELECCION_AREAS);


                }

            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                super.onCanceled(source, type);
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    public void existeDirectorioImagenesAreas() {

        Boolean sePudo = true;
        File dir = new File(getContext().getExternalFilesDir(null)+ File.separator + "nomad" + File.separator + "audit5s" +File.separator +FirebaseAuth.getInstance().getCurrentUser().getEmail()+ File.separator + "images" + File.separator + "areas");
        if (!dir.exists() || !dir.isDirectory()) {
            sePudo = dir.mkdirs();
        }
    }

    public void updateAdapter() {
        //String usuario=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Realm realm= Realm.getDefaultInstance();
        RealmResults<Area> result3 = realm.where(Area.class)
                //las areas son de todos los usuarios//   .equalTo("usuario",usuario)
                .findAll();
        listaAreas=new RealmList<>();
        listaAreas.addAll(result3);
        adapterArea.setListaAreasOriginales(listaAreas);
        adapterArea.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
