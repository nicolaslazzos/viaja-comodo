package com.nlazzos.viajacomodo.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nlazzos.viajacomodo.BDHelper.BDHelper;
import com.nlazzos.viajacomodo.DAOs.DAOColectivo;
import com.nlazzos.viajacomodo.DAOs.DAOEmpresa;
import com.nlazzos.viajacomodo.R;

import java.util.ArrayList;
import java.util.List;

public class EmpresasActivity extends AppCompatActivity {

    private BDHelper helper;
    private SQLiteDatabase db;

    private DAOEmpresa daoEmpresa;
    private DAOColectivo daoColectivo;

    private Cursor cursor;
    private List<String> listaNombres = new ArrayList<String>();
    private List<Integer> listaID = new ArrayList<Integer>();
    private ArrayAdapter<String> adapter;

    private EditText txtNuevaEmpresa;
    private ListView lvEmpresas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ABRO LA CONEXION A LA BD
        helper = new BDHelper(this);
        db = helper.getWritableDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevaEmpresa();
            }
        });
        fab.setImageResource(R.drawable.ic_add_black_24dp);

        //CARGO LA LISTA DE EMPRESAS
        lvEmpresas = (ListView) findViewById(R.id.lvEmpresas);
        cargarLista();

        //PARA LAS OPCIONES CUANDO SE MANTIENE APRETADO UN ITEM
        registerForContextMenu(lvEmpresas);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.lvEmpresas){
            this.getMenuInflater().inflate(R.menu.menu_opciones, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.opcionEditar:
                editarEmpresa(info.position);
                return true;
            case R.id.opcionEliminar:
                deseaEiminar(info.position);
                return true;
                default:
                    return super.onContextItemSelected(item);
        }
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

    public boolean toActivity(Class activity){
        Intent nuevo = new Intent(this, activity);
        startActivity(nuevo);
        this.finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            toActivity(MainActivity.class);
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
                        dialog.dismiss();
                        cargarLista();
                    }else{
                        toast(getString(R.string.empresaExiste));
                    }
                }else{
                    toast(getString(R.string.debeIngresarEmpresa));
                }
            }
        });
    }

    public void editarEmpresa(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.nueva_empresa, null);

        txtNuevaEmpresa = (EditText) view.findViewById(R.id.txtNuevaEmpresa);
        txtNuevaEmpresa.setText(listaNombres.get(position));

        builder.setView(view);
        builder.setTitle(getString(R.string.titleEditarEmpresa));
        builder.setPositiveButton(getString(R.string.guardar), null);
        builder.setNegativeButton(getString(R.string.cancelar), null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button aceptar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtNuevaEmpresa.getText().toString().matches("")){
                    if (daoEmpresa.validarExistencia(db, txtNuevaEmpresa.getText().toString()) ||
                            txtNuevaEmpresa.getText().toString().matches(listaNombres.get(position))){
                        daoEmpresa.modificar(db,listaID.get(position),txtNuevaEmpresa.getText().toString());
                        dialog.dismiss();
                        cargarLista();
                    }else{
                        toast(getString(R.string.empresaExiste));
                    }
                }else{
                    toast(getString(R.string.debeIngresarEmpresa));
                }
            }
        });
    }

    public void deseaEiminar(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.estaSeguro));
        builder.setMessage(getString(R.string.deseaEliminarEmpresa));
        builder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                daoColectivo = new DAOColectivo();
                daoColectivo.eliminarPorEmpresa(db, listaID.get(position));
                daoEmpresa.eliminar(db, listaID.get(position));
                cargarLista();
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void cargarLista(){
        daoEmpresa = new DAOEmpresa();
        cursor = daoEmpresa.consultar(db);
        listaNombres = new ArrayList<String>();
        listaID = new ArrayList<Integer>();

        while(cursor.moveToNext()){
            listaNombres.add(cursor.getString(2));
            listaID.add(cursor.getInt(1));
        }

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item,listaNombres);
        lvEmpresas.setAdapter(adapter);
    }

    public void toast(String mensaje){
        Toast.makeText(this,mensaje, Toast.LENGTH_SHORT).show();
    }

}
