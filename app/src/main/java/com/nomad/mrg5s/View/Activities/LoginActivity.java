package com.nomad.mrg5s.View.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.Model.Usuario;
import com.nomad.mrg5s.R;
import com.nomad.mrg5s.Utils.HTTPConnectionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import io.realm.Realm;
import io.realm.RealmResults;

public class LoginActivity extends AppCompatActivity {


    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private EditText editUsuario;
    private EditText editPass;
    private TextInputLayout til1;
    private TextInputLayout til2;
    private Button botonLogin;
    private Button botonRegister;
    private String mail;
    private String contraseña;
    private TextView passwordOlvidada;
    private Usuario nuevoUsuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressBar);
        passwordOlvidada = findViewById(R.id.passwordOlvidada);
        passwordOlvidada.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mDatabase = FirebaseDatabase.getInstance().getReference();
                if (user != null) {
                    irALanding();
                }
            }
        };

        TextView unTextview = findViewById(R.id.textViewLogin);
        til1 = findViewById(R.id.inputLayout1);
        til2 = findViewById(R.id.inputLayout2);
        botonLogin = findViewById(R.id.buttonLogin);
        botonRegister = findViewById(R.id.buttonRegister);
        editUsuario = findViewById(R.id.editTextUsuario);
        editPass = findViewById(R.id.editTextPassword);

        Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        unTextview.setTypeface(roboto);
        editUsuario.setTypeface(roboto);

        //COMPROBAR SI COINCIDE USUARIO Y CONTRASEÑA
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                View focusedView = getCurrentFocus();

                if (focusedView != null) {
                    inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

                til1.setError(null);
                til2.setError(null);
                Integer validar = 0;
                if (editUsuario.getText().toString().isEmpty()) {
                    til1.setError(getResources().getString(R.string.ingreseUsuarioValido));
                    validar = validar + 1;
                }

                if (editPass.getText().toString().toLowerCase().isEmpty()) {
                    til2.setError(getResources().getString(R.string.ingreseContraseniaValida));
                    validar = validar + 1;
                }
                if (editPass.getText().toString().length() < 6) {
                    til2.setError(getResources().getString(R.string.caracteresContrasenia));
                    validar = validar + 1;
                }

                if (validar > 0) {
                    return;
                }

                mail = editUsuario.getText().toString().trim()
                        .toLowerCase();
                contraseña = editPass.getText().toString();

                loguearFirebaseManual(mail, contraseña);
            }
        });
        //BOTON REGISTER

        botonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SI EL BOTON ESTA INVISIBLE QUE REGISTRE EL USUARIO
                if (botonLogin.getVisibility() == View.GONE) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    View focusedView = getCurrentFocus();

                    if (focusedView != null) {
                        inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    Integer control = 0;
                    til1.setError(null);
                    til2.setError(null);

                    if (editUsuario.getText().toString().isEmpty()) {
                        til1.setError(getResources().getString(R.string.ingreseUsuarioValido));
                        control = control + 1;
                    }

                    if (editPass.getText().toString().isEmpty()) {
                        til2.setError(getResources().getString(R.string.ingreseContraseniaValida));
                        control = control + 1;
                    }
                    if (editPass.getText().toString().length() < 6) {
                        til2.setError(getResources().getString(R.string.caracteresContrasenia));
                        control = control + 1;
                    }

                    if (control > 0) {
                        return;
                    }

                    if (HTTPConnectionManager.isNetworkingOnline(LoginActivity.this)) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setIndeterminate(true);
                        String usuario = editUsuario.getText().toString().trim();
                        String pass = editPass.getText().toString();

                        crearCuentaFirebase(usuario, pass);
                    } else {
                        Snackbar.make(editUsuario, R.string.noHayInternet, Snackbar.LENGTH_LONG)
                                .setAction(R.string.reintentar, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        botonRegister.performClick();
                                    }
                                })
                                .show();
                    }

                } else {

                    botonLogin.setVisibility(View.GONE);
                    passwordOlvidada.setVisibility(View.GONE);
                    editPass.setText(null);
                    editUsuario.setText(null);
                    editUsuario.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editUsuario, InputMethodManager.SHOW_IMPLICIT);

                }
            }
        });

        //CLICK PASSWORD OLVIDADA
        passwordOlvidada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editUsuario == null || editUsuario.getText().toString().isEmpty()) {
                    Snackbar.make(view, getResources().getString(R.string.noMail), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    reestablecerContrasenia();
                }
            }
        });
    }

    public void irALanding() {
        progressBar.setVisibility(View.GONE);
        Intent unIntent = new Intent(this, LandingActivity.class);
        startActivity(unIntent);
        Toast.makeText(this, getResources().getString(R.string.bienVenida) + " " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        LoginActivity.this.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void loguearFirebaseManual(final String usuario, final String pass) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        mAuth.signInWithEmailAndPassword(usuario, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        if (!task.isSuccessful()) {

                            if (HTTPConnectionManager.isNetworkingOnline(LoginActivity.this)) {

                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.datosErroneos), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.autenticacionFallida), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Snackbar.make(editUsuario, R.string.noHayInternetlogin, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.reintentar, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                loguearFirebaseManual(usuario, pass);
                                            }
                                        })
                                        .show();
                            }

                        } else {
                            til1.setError(null);
                            til2.setError(null);
                            editPass.setText(null);
                            editUsuario.setText(null);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void crearCuentaFirebase(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            botonLogin.setVisibility(View.VISIBLE);
                            botonRegister.setVisibility(View.VISIBLE);
                            passwordOlvidada.setVisibility(View.VISIBLE);
                            nuevoUsuario= new Usuario();
                            nuevoUsuario.setMail(editUsuario.getText().toString().trim()
                                    .toLowerCase());
                            nuevoUsuario.setPass(editPass.getText().toString());
                            guardarUsuarioDatabase();
                            Realm realm = Realm.getDefaultInstance();
                            RealmResults<Usuario> mResults = realm.where(Usuario.class)
                                    .equalTo("mail", email)
                                    .findAll();
                            if (mResults.size() < 1) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Usuario mUser = realm.copyToRealm(nuevoUsuario);
                                    }
                                });
                            }
                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.usuarioRepetido), Toast.LENGTH_LONG).show();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.datosErroneos), Toast.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(editUsuario, R.string.noHayInternet, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.reintentar, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    crearCuentaFirebase(email, password);
                                                }
                                            })
                                            .show();

                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {

        if (botonLogin.getVisibility() == View.GONE) {
            botonLogin.setVisibility(View.VISIBLE);
            passwordOlvidada.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
            finishAffinity();
        }


    }

//    private void sendEmailVerification() {
//        // Disable button
//        botonRegister.setEnabled(false);
//
//        // Send verification email
//        // [START send_email_verification]
//        final FirebaseUser user = mAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // [START_EXCLUDE]
//                        // Re-enable button
//                        botonRegister.setEnabled(true);
//
//                        if (task.isSuccessful()) {
//                            Toast.makeText(LoginActivity.this,
//                                    R.string.mailVerificacionEnviado + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//
//                            Log.e("SENDMAIL_EXCEPTION", "sendEmailVerification", task.getException());
//                            Toast.makeText(LoginActivity.this,
//                                    R.string.errorMailVerificacion, Toast.LENGTH_SHORT).show();
//                        }
//                        // [END_EXCLUDE]
//                    }
//                });
//        // [END send_email_verification]
//    }

    private void guardarUsuarioDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = mDatabase.child("usuarios").child(user.getUid()).child("datos");
        //String mail;
        //String foto;
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            mail = user.getDisplayName();
            reference.child("nombre").setValue(user.getDisplayName());
        } else {
            mail = user.getEmail();
            reference.child("uid").setValue(user.getUid());
            reference.child("email").setValue(user.getEmail());
            reference.child("nombre").setValue(user.getDisplayName());
        }
    }

    public void reestablecerContrasenia() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        mail = editUsuario.getText().toString().trim();

        new MaterialDialog.Builder(LoginActivity.this)
                .contentColor(ContextCompat.getColor(LoginActivity.this, R.color.primary_text))
                .titleColor(ContextCompat.getColor(LoginActivity.this, R.color.tile4))
                .title(R.string.passwordOlvidada)
                .cancelable(true)
                .content(getResources().getString(R.string.enviaremosInstrucciones) + "\n" + mail + "\n" + getResources().getString(R.string.deseaContinuar))
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mAuth.sendPasswordResetEmail(mail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.mailReestablecimientoEnviado) + mail, Toast.LENGTH_LONG).show();
                                } else {
                                    if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                        Toast.makeText(LoginActivity.this, "no such user", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                })
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .show();
    }
}