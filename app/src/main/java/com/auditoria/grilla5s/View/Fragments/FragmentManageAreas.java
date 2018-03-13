package com.auditoria.grilla5s.View.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.auditoria.grilla5s.Model.Area;
import com.auditoria.grilla5s.Model.Foto;
import com.auditoria.grilla5s.R;
import com.auditoria.grilla5s.Utils.FuncionesPublicas;
import com.auditoria.grilla5s.View.Adapter.AdapterArea;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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
public class FragmentManageAreas extends Fragment {

    private RealmList<Area> listaAreas;
    private RecyclerView recyclerAreas;
    private AdapterArea adapterArea;
    private LinearLayoutManager layoutManager;

    private SharedPreferences config;


    private File fotoOriginal;
    private File fotoComprimida;

    private Notificable notificable;
    private FloatingActionMenu fabMenuManage;
    private FloatingActionButton fabAgregarArea;
    private FloatingActionButton fabSalir;


    private Avisable unAvisable;

    private TextView textView;
    private DatabaseReference mDatabase;

    private LinearLayout linearSnackbar;


    public FragmentManageAreas() {
        // Required empty public constructor
    }

    public LinearLayout getLinearCoordinator() {
        return linearSnackbar;
    }

    public interface Avisable{
        public void salirDeAca();
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

    public interface Notificable{
        public void comenzarAuditoria(Area unArea);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_manage_area, container, false);

        linearSnackbar =view.findViewById(R.id.linearParaCoordinar);

       // String usuario=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Area> result2 = realm.where(Area.class)
               //las areas son para todos los usuarios// .equalTo("usuario",usuario)
                .findAll();
        listaAreas=new RealmList<>();
        listaAreas.addAll(result2);
        recyclerAreas= (RecyclerView)view.findViewById(R.id.recyclerArea);
        adapterArea= new AdapterArea();
        adapterArea.setContext(getContext());
        layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerAreas.setLayoutManager(layoutManager);
        adapterArea.setListaAreasOriginales(listaAreas);
        recyclerAreas.setAdapter(adapterArea);

        textView=(TextView)view.findViewById(R.id.textoTituloManage);
        Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        textView.setTypeface(roboto);


        View.OnClickListener listenerArea = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer posicion = recyclerAreas.getChildAdapterPosition(v);
                RealmList<Area> listaAreas = adapterArea.getListaAreasOriginales();
                Area areaClickeada = listaAreas.get(posicion);
               // notificable.comenzarAuditoria(areaClickeada);
            }
        };
        adapterArea.setListener(listenerArea);

        fabMenuManage =(FloatingActionMenu) view.findViewById(R.id.agregarArea);
        fabMenuManage.setMenuButtonColorNormal(ContextCompat.getColor(getContext(), R.color.colorAccent));
        fabAgregarArea=new FloatingActionButton(getActivity());


        fabAgregarArea.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabAgregarArea.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
        fabAgregarArea.setLabelText(getString(R.string.addNewArea));
        fabAgregarArea.setImageResource(R.drawable.ic_note_add_black_24dp);
        fabMenuManage.addMenuButton(fabAgregarArea);

        fabAgregarArea.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile3),
                ContextCompat.getColor(getActivity(), R.color.light_grey),
                ContextCompat.getColor(getActivity(), R.color.white_transparent));
        fabAgregarArea.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        fabAgregarArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                if (FuncionesPublicas.isExternalStorageWritable()) {
                    if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        fabMenuManage.close(true);
                        EasyImage.openChooserWithGallery(FragmentManageAreas.this, getResources().getString(R.string.seleccionaImagen), 1);
                    }
                    else {
                        if (Nammu.shouldShowRequestPermissionRationale(FragmentManageAreas.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            //User already refused to give us this permission or removed it
                            //Now he/she can mark "never ask again" (sic!)
                            Snackbar.make(getView(), getResources().getString(R.string.appNecesitaPermiso),
                                    Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.ok), new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    Nammu.askForPermission(FragmentManageAreas.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            new PermissionCallback() {
                                                @Override
                                                public void permissionGranted() {
                                                    fabMenuManage.close(true);
                                                    EasyImage.openChooserWithGallery(FragmentManageAreas.this, getResources().getString(R.string.seleccionaImagen), 1);
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
                            Nammu.askForPermission(FragmentManageAreas.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    new PermissionCallback() {
                                        @Override
                                        public void permissionGranted() {
                                            fabMenuManage.close(true);
                                            EasyImage.openChooserWithGallery(FragmentManageAreas.this, getResources().getString(R.string.seleccionaImagen), 1);
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

        fabSalir = new FloatingActionButton(getActivity());
        fabSalir.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabSalir.setColorNormal(ContextCompat.getColor(getContext(), R.color.tile3));
        fabSalir.setLabelText(getString(R.string.salir));
        fabSalir.setImageResource(R.drawable.ic_exit_to_app_black_24dp);
        fabMenuManage.addMenuButton(fabSalir);

        fabSalir.setLabelColors(ContextCompat.getColor(getActivity(), R.color.tile3),
                ContextCompat.getColor(getActivity(), R.color.light_grey),
                ContextCompat.getColor(getActivity(), R.color.white_transparent));
        fabSalir.setLabelTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        fabSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenuManage.close(true);
                unAvisable.salirDeAca();

            }
        });

        config = getActivity().getSharedPreferences("prefs",0);
        boolean quiereVerTuto = config.getBoolean("quiereVerTuto",false);
        boolean primeraVezFragmentManage=config.getBoolean("primeraVezFragmentManage",false);

        //SI EL USUARIO ELIGIO VER TUTORIALES ME FIJO SI YA PASO POR ESTA PAGINA.
        if (quiereVerTuto) {
            if (!primeraVezFragmentManage) {

                    SharedPreferences.Editor editor = config.edit();
                    editor.putBoolean("primeraVezFragmentManage",true);
                    editor.commit();

                    seguirConTutorial();

            }
        }

        return view;
    }



    private void seguirConTutorial() {
        Typeface roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        fabMenuManage.open(true);

        TapTargetView.showFor(getActivity(),                 // `this` is an Activity

                TapTarget.forView(fabAgregarArea, getResources().getString(R.string.tutorial_tit_manage), getResources().getString(R.string.tutorial_desc_manage))
                        .outerCircleColor(R.color.tutorial2)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.85f)            // Specify the alpha amount for the outer circle
                        //.targetCircleColor(R.color.white)   // Specify a color for the target circle
                        //.titleTextSize(20)                  // Specify the size (in sp) of the title text
                        //.titleTextColor(R.color.white)      // Specify the color of the title text
                        //.descriptionTextSize(10)            // Specify the size (in sp) of the description text
                        // .descriptionTextColor(R.color.red)  // Specify the color of the description text
                        //.textColor(R.color.blue)            // Specify a color for both the title and description text
                        .textTypeface(roboto)  // Specify a typeface for the text
                        //.dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false)                   // Whether to tint the target view's color
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                        //.icon(Drawable)                     // Specify a custom drawable to draw as the target
                        //.targetRadius(70)                  // Specify the target radius (in dp)
                ,
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        

                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.unAvisable=(Avisable) context;
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
                    crearDialogoNombreArea(unaFoto);


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

    public void crearDialogoNombreArea(final Foto unaFoto){

        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(R.string.addNewArea))
                .inputRange(1,40)
                .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                .content(getResources().getString(R.string.nombreArea))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getResources().getString(R.string.areaName),"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        final Area unArea = new Area();
                        unArea.setNombreArea(input.toString());
                        unArea.setFotoArea(unaFoto);
                        unArea.setIdArea("area" + UUID.randomUUID());

                        //guardo nueva area en Realm
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Area realmArea = realm.copyToRealm(unArea);
                            }
                        });

                        registrarCantidadAreasFirebase();
                        updateAdapter();

                        Toast.makeText(getContext(), unArea.getNombreArea()+" "+getResources().getString(R.string.creadoExitosamente), Toast.LENGTH_SHORT).show();


                    }
                }).show();

    }

    private void registrarCantidadAreasFirebase() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        if (user!=null) {
            final DatabaseReference reference = mDatabase.child("usuarios").child(user.getUid()).child("estadisticas").child("cantidadAreasCreadas");

            //---LEER Y SUMAR UN AREA AL USUARIO---//
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null) {
                            String cantidadAreas =dataSnapshot.getValue().toString();
                            Integer numeroAreas= Integer.parseInt(cantidadAreas)+1;
                            reference.setValue(numeroAreas.toString());
                    } else {
                        reference.setValue("1");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //--------------por UX se reemplaza este metodo por un snackbar-----------------//
    /*
    public void dialogoExito(Area unArea) {

        new MaterialDialog.Builder(getContext())
                .title("New area successfully created")
                .contentColor(ContextCompat.getColor(getContext(), R.color.primary_text))
                .titleColor(ContextCompat.getColor(getContext(), R.color.tile4))
                .backgroundColor(ContextCompat.getColor(getContext(), R.color.tile1))
                .content("The area: " + unArea.getNombreArea() +"\n"+ "has been succesfully added to the system")
                .positiveText("Go back")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                   updateAdapter();
                    }
                })
                .negativeText("Add new area")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateAdapter();
                        EasyImage.openChooserWithGallery(FragmentManageAreas.this, "Select image", 1);
                    }
                })
                .show();



    }
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
