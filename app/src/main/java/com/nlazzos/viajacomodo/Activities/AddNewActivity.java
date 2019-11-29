package com.nlazzos.viajacomodo.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.nlazzos.viajacomodo.BDHelper.BDHelper;
import com.nlazzos.viajacomodo.DAOs.DAOCalificacion;
import com.nlazzos.viajacomodo.DAOs.DAOColectivo;
import com.nlazzos.viajacomodo.DAOs.DAOEmpresa;
import com.nlazzos.viajacomodo.Entities.Colectivo;
import com.nlazzos.viajacomodo.Entities.Empresa;
import com.nlazzos.viajacomodo.R;

import java.util.ArrayList;

public class AddNewActivity extends AppCompatActivity {
    //CONEXION CON BD
    private BDHelper helper;
    private SQLiteDatabase db;
    //INSTANCIAS DE CLASES
    private DAOEmpresa daoEmpresa;
    private DAOColectivo daoColectivo;
    private DAOCalificacion daoCalificacion;
    private Colectivo colectivo;
    //ARRAYS Y VARIABLES
    private ArrayAdapter<CharSequence> adaptador;
    private ArrayList<String> listaCombo;
    private ArrayList<Empresa> listaEmpresas;
    //WIDGETS
    private Spinner cmbEmpresas;
    private EditText nroColectivo;
    private RadioButton rbtExcelente;
    private RadioButton rbtBueno;
    private RadioButton rbtRegular;
    private RadioButton rbtMalo;
    private EditText observaciones;
    private Button cmdGuardar;
    private Button cmdCancelar;
    private Button cmdNuevaEmpresa;
    private EditText txtNuevaEmpresa;

    //ESTO SE USA PARA EL PASO DE VARIABLES
    //private Bundle datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        //ABRO LA CONEXION A LA BD
        helper = new BDHelper(this);
        db = helper.getWritableDatabase();

        //HAGO EL FIND DE LOS WIDGETS
        cmbEmpresas = (Spinner) findViewById(R.id.cmbEmpresa);
        nroColectivo = (EditText) findViewById(R.id.txtNroBondi);
        rbtExcelente = (RadioButton) findViewById(R.id.rbtExcelente);
        rbtBueno = (RadioButton) findViewById(R.id.rbtBueno);
        rbtRegular = (RadioButton) findViewById(R.id.rbtRegular);
        rbtMalo = (RadioButton) findViewById(R.id.rbtMalo);
        observaciones = (EditText) findViewById(R.id.txtObservaciones);
        cmdGuardar = (Button) findViewById(R.id.cmdGuardar);
        cmdCancelar = (Button) findViewById(R.id.cmdCancelar);
        cmdNuevaEmpresa = (Button) findViewById(R.id.cmdAgregarEmpresa);

        llenarCombo();

        //INICIALIZO EL FLAG DE EDITAR COLECTIVO (PASO DE VARIABLES ENTRE ACTIVITIES)
        //Bundle datos = this.getIntent().getExtras();
        //final int editFlag = datos.getInt("editFlag");

        //LISTENER PARA EL BOTON GUARDAR
        cmdGuardar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                guardarColectivo();
            }
        });
        //LISTENER PARA EL BOTON CANCELAR
        cmdCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });
        //LISTENER PARA EL BOTON AGREGAR EMPRESA
        cmdNuevaEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevaEmpresa();
            }
        });

        //VERIFICAR SI ES PARA EDITAR COLECTIVO O NO (PASO DE VARIABLES ENTRE ACTIVITIES)
        //modoEdicion(editFlag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                toActivity(MainActivity.class);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void llenarCombo(){
        daoEmpresa = new DAOEmpresa();
        Cursor cursor = daoEmpresa.consultar(db);

        Empresa empresa = null;
        listaEmpresas = new ArrayList<Empresa>();

        while (cursor.moveToNext()){
            empresa = new Empresa();
            empresa.setIdEmpresa(cursor.getInt(1));
            empresa.setNombre(cursor.getString(2));
            listaEmpresas.add(empresa);
        }

        obtenerLista();
        adaptador = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaCombo);
        cmbEmpresas.setAdapter(adaptador);
    }

    private void seleccionarCombo(int idEmpresa){
        llenarCombo();

        for(int i=0; i<listaEmpresas.size(); i++){
            if(listaEmpresas.get(i).getIdEmpresa() == idEmpresa){
                cmbEmpresas.setSelection(i+1);
                return;
            }
        }
    }

    private void obtenerLista(){
        listaCombo = new ArrayList<String>();
        listaCombo.add(getString(R.string.seleccione));

        for (int i=0; i<listaEmpresas.size(); i++){
            listaCombo.add(listaEmpresas.get(i).getNombre());
        }
    }

    private void guardarColectivo(){
        daoColectivo = new DAOColectivo();
        daoCalificacion = new DAOCalificacion();
        colectivo = new Colectivo();

        //VALIDO QUE SE HAYA SELECCIONADO UNA OPCION DIFERENTE A LA POR DEFECTO
        if (cmbEmpresas.getSelectedItemPosition() != 0){
            colectivo.setIdEmpresa(listaEmpresas.get(cmbEmpresas.getSelectedItemPosition()-1).getIdEmpresa());
        }else {
            Toast.makeText(this, getString(R.string.debeSeleccionarEmpresa), Toast.LENGTH_SHORT).show();
            return;
        }
        //VALIDO QUE EL NUMERO DE COLECTIVO SEA TIPO INTEGER
        if (!nroColectivo.getText().toString().matches("")){
            if (daoColectivo.consultarExistencia(db, nroColectivo.getText().toString(),
                    String.valueOf(listaEmpresas.get(cmbEmpresas.getSelectedItemPosition()-1).getIdEmpresa()))){
                colectivo.setNroColectivo(Integer.parseInt(nroColectivo.getText().toString()));
            }else{
                Toast.makeText(this, getString(R.string.yaExiste), Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(this, getString(R.string.debeIngresarNumero), Toast.LENGTH_SHORT).show();
            return;
        }
        //SETEO DE LA CALIFICACION RECUPERANDO EL ID CON UN METODO DESDE LA BD
        if (rbtExcelente.isChecked()){
            colectivo.setIdCalificacion(daoCalificacion.consultarId(db,getString(R.string.rbtExcelente)));
        }else if(rbtBueno.isChecked()){
            colectivo.setIdCalificacion(daoCalificacion.consultarId(db,getString(R.string.rbtBueno)));
        }else if(rbtRegular.isChecked()){
            colectivo.setIdCalificacion(daoCalificacion.consultarId(db,getString(R.string.rbtRegular)));
        }else if(rbtMalo.isChecked()){
            colectivo.setIdCalificacion(daoCalificacion.consultarId(db,getString(R.string.rbtMalo)));
        }else{
            Toast.makeText(this,getString(R.string.debeSeleccionarCalificacion), Toast.LENGTH_SHORT).show();
            return;
        }
        //VALIDO SI SE INGRESO O NO UNA DESCRIPCION
        if (!observaciones.getText().toString().matches("")){
            colectivo.setObservaciones(observaciones.getText().toString());
        }else{
            colectivo.setObservaciones("");
        }

        daoColectivo.insertar(db, colectivo.getNroColectivo(), colectivo.getIdEmpresa(), colectivo.getIdCalificacion(), colectivo.getObservaciones());

        Toast.makeText(this,getString(R.string.seGuardo), Toast.LENGTH_SHORT).show();
        toActivity(MainActivity.class);
    }

    public void dialogo(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirmar));
        builder.setMessage(getString(R.string.deseaCancelar));
        builder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toActivity(MainActivity.class);
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void cancelar(){
        if ((cmbEmpresas.getSelectedItemPosition() == 0) && (nroColectivo.getText().toString().matches("")) &&
                (!rbtExcelente.isChecked()) && (!rbtBueno.isChecked()) && (!rbtRegular.isChecked()) &&
                (!rbtMalo.isChecked()) && (observaciones.getText().toString().matches(""))){
            toActivity(MainActivity.class);
        }else{
            dialogo();
        }
    }

    public boolean toActivity(Class activity){
        Intent nuevo = new Intent(this, activity);
        startActivity(nuevo);
        this.finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            cancelar();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void nuevaEmpresa(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.nueva_empresa, null);

        txtNuevaEmpresa = (EditText) view.findViewById(R.id.txtNuevaEmpresa);

        builder.setView(view);
        builder.setTitle(getString(R.string.nuevaEmpresaTitle));
        builder.setPositiveButton(getString(R.string.guardar), null);
        builder.setNegativeButton(getString(R.string.cancelar), null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button aceptar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtNuevaEmpresa.getText().toString().matches("")){
                    if (daoEmpresa.validarExistencia(db, txtNuevaEmpresa.getText().toString())){
                        daoEmpresa.insertar(db, txtNuevaEmpresa.getText().toString());
                        seleccionarCombo(daoEmpresa.obtenerUltimoID(db));
                        dialog.dismiss();
                    }else{
                        toast(getString(R.string.empresaExiste));
                    }
                }else{
                    toast(getString(R.string.debeIngresarEmpresa));
                }
            }
        });
    }

    public void toast(String mensaje){
        Toast.makeText(this,mensaje, Toast.LENGTH_SHORT).show();
    }

    /*
    public void modoEdicion(int flag){
        //CAPTA LAS VARIABLES PASADAS DESDE EL MAIN ACTIVITY
        //BUEN METODO PARA VER EL PASO DE VARIABLES ENTRE ACTIVITIES
        datos = this.getIntent().getExtras();
        if (flag == 1){
            for (int i=0; i<listaEmpresas.size(); i++){
                if (listaEmpresas.get(i).getIdEmpresa() == datos.getInt("idEmpresa")){
                    cmbEmpresas.setSelection(i+1);
                    break;
                }
            }

            //cmbEmpresas.setEnabled(false);
            nroColectivo.setText(String.valueOf(datos.getInt("nroColectivo")));
            //nroColectivo.setEnabled(false);

            switch (datos.getString("calificacion")){
                case "Excelente":
                    rbtExcelente.setChecked(true);
                    break;
                case "Regular":
                    rbtRegular.setChecked(true);
                    break;
                case "Bueno":
                    rbtBueno.setChecked(true);
                    break;
                case "Malo":
                    rbtMalo.setChecked(true);
                    break;
                default:
                    break;
            }

            observaciones.setText(String.valueOf(datos.getString("observaciones")));
            //cmdNuevaEmpresa.setEnabled(false);
        }
    }
    */
}
