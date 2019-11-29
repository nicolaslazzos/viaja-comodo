package com.nlazzos.viajacomodo.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.nlazzos.viajacomodo.Adapters.ELVAdapter;
import com.nlazzos.viajacomodo.BDHelper.BDHelper;
import com.nlazzos.viajacomodo.DAOs.DAOCalificacion;
import com.nlazzos.viajacomodo.DAOs.DAOColectivo;
import com.nlazzos.viajacomodo.Entities.Colectivo;
import com.nlazzos.viajacomodo.R;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BDHelper helper;
    private SQLiteDatabase db;

    private DAOColectivo daoColectivo = new DAOColectivo();
    private DAOCalificacion daoCalificacion = new DAOCalificacion();
    private Colectivo colectivo;

    private ELVAdapter adapter;
    private ArrayList<Colectivo> listaColectivos;
    private ArrayList<String> listaObservaciones;
    private ArrayList<String> listaGroups;
    private Map<String, ArrayList<String>> mapChilds;

    private ExpandableListView elvColectivos;
    private FloatingActionButton fab;

    private static final int PICKFILE_RESULT_CODE = 1;
    private String rutaBackup;

    private RadioButton rbtExcelente;
    private RadioButton rbtBueno;
    private RadioButton rbtRegular;
    private RadioButton rbtMalo;
    private EditText observaciones;

    private int idMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivity(AddNewActivity.class);
            }
        });
        fab.setImageResource(R.drawable.ic_add_black_24dp);

        //CREACION DE LA BD
        helper = new BDHelper(this);
        db = helper.getWritableDatabase();

        //HAGO EL FIND DE LOS WIDGETS
        elvColectivos = (ExpandableListView) findViewById(R.id.lvColectivos);

        //RELLENAR LISTA
        cargarLista(daoColectivo.consultar(db));

        //PARA LAS OPCIONES CUANDO SE MANTIENE APRETADO UN ITEM
        registerForContextMenu(elvColectivos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchViewItem = menu.findItem(R.id.actionSearch);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setQueryHint(getText(R.string.hintBuscar));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscar(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        idMenu = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (idMenu){
            case R.id.opcionAdmEmpresas:
                toActivity(EmpresasActivity.class);
                return true;
            case R.id.opcionExportarBD:
                if(isStoragePermissionGranted()){
                    try{
                        helper.exportDB();
                        toast(getString(R.string.seExporto));
                    }catch(IOException e){
                        toast(getString(R.string.noSeExporto));
                    }
                }
                return true;
            case R.id.opcionImportarBD:
                if(isStoragePermissionGranted()){
                    openFolder();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.lvColectivos){
            this.getMenuInflater().inflate(R.menu.menu_opciones, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int position = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        switch(item.getItemId()){
            case R.id.opcionEditar:
                editarColectivo(position);
                return true;
            case R.id.opcionEliminar:
                deseaEiminar(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    rutaBackup = data.getData().getPath();
                    if(rutaBackup.indexOf(".vc") != -1){
                        db.close();
                        try{
                            helper.importDB(rutaBackup);
                            toast(getString(R.string.seImporto));
                        }catch(IOException e){
                            toast(getString(R.string.noSeImporto));
                        }
                        db = helper.getWritableDatabase();
                        cargarLista(daoColectivo.consultar(db));
                    }else{
                        openFolder();
                        toast(getString(R.string.archivoInvalido));
                    }
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(RESULT_OK);
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean toActivity(Class activity){
        Intent nuevo = new Intent(this, activity);
        //FLAG PARA CUANDO LA EDICION DEL COLECTIVO USABA EL MISMO  ACTIVITY QUE EL AGREGAR COLECTIVO
        //nuevo.putExtra("editFlag", 0);
        startActivity(nuevo);
        this.finish();
        return true;
    }

    public void openFolder(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
        intent.setDataAndType(uri, "text/csv");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.abrirArchivo)),PICKFILE_RESULT_CODE);
    }

    public void cargarLista(Cursor cursor){
        db = helper.getWritableDatabase();
        listaGroups = new ArrayList<>();
        mapChilds = new HashMap<>();
        listaColectivos = new ArrayList<Colectivo>();

        while (cursor.moveToNext()){
            colectivo = new Colectivo();
            colectivo.setNroColectivo(cursor.getInt(1));
            colectivo.setEmpresa(cursor.getString(2));
            colectivo.setCalificacion(cursor.getString(3));
            colectivo.setObservaciones(cursor.getString(4));
            colectivo.setIdEmpresa(cursor.getInt(5));
            listaColectivos.add(colectivo);
        }

        for (int i = 0; i < listaColectivos.size(); i++){
            listaGroups.add(listaColectivos.get(i).getNroColectivo() + " - " + listaColectivos.get(i).getEmpresa());
            listaObservaciones = new ArrayList<String>();
            listaObservaciones.add(listaColectivos.get(i).getCalificacion());
            listaObservaciones.add(listaColectivos.get(i).getObservaciones());
            mapChilds.put(listaGroups.get(i), listaObservaciones);
        }

        adapter = new ELVAdapter(this, listaGroups, mapChilds);
        elvColectivos.setAdapter(adapter);
    }

    public void buscar(String busqueda){
        if (!busqueda.matches("")) {
            cargarLista(daoColectivo.consultarPorNro(db, busqueda));
        }else{
            cargarLista(daoColectivo.consultar(db));
        }
    }

    public void deseaEiminar(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirmar));
        builder.setMessage(getString(R.string.deseaEliminarColectivo));
        builder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                daoColectivo.eliminar(db,listaColectivos.get(position).getNroColectivo(),listaColectivos.get(position).getIdEmpresa());
                cargarLista(daoColectivo.consultar(db));
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void editarColectivo(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.editar_colectivo, null);

        rbtExcelente = (RadioButton) view.findViewById(R.id.rbtExcelente);
        rbtBueno = (RadioButton) view.findViewById(R.id.rbtBueno);
        rbtRegular = (RadioButton) view.findViewById(R.id.rbtRegular);
        rbtMalo = (RadioButton) view.findViewById(R.id.rbtMalo);
        observaciones = (EditText) view.findViewById(R.id.txtObservaciones);

        switch (listaColectivos.get(position).getCalificacion()){
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
        observaciones.setText(listaColectivos.get(position).getObservaciones());

        builder.setView(view);
        builder.setTitle(getString(R.string.editarColectivo));
        builder.setPositiveButton(getString(R.string.guardar), null);
        builder.setNegativeButton(getString(R.string.cancelar), null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button aceptar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colectivo = new Colectivo();
                //SETEO LAS CLAVES PRIMARIAS QUE NO SE MODIFICAN
                colectivo.setIdEmpresa(listaColectivos.get(position).getIdEmpresa());
                colectivo.setNroColectivo(listaColectivos.get(position).getNroColectivo());
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
                    toast(getString(R.string.debeSeleccionarCalificacion));
                    return;
                }
                //VALIDO SI SE INGRESO O NO UNA DESCRIPCION
                if (!observaciones.getText().toString().matches("")){
                    colectivo.setObservaciones(observaciones.getText().toString());
                }else{
                    colectivo.setObservaciones("");
                }
                daoColectivo.modificar(db, colectivo.getNroColectivo(), colectivo.getIdEmpresa(), colectivo.getIdCalificacion(), colectivo.getObservaciones());
                toast(getString(R.string.seGuardo));
                cargarLista(daoColectivo.consultar(db));
                dialog.dismiss();
            }
        });
    }

    public  boolean isStoragePermissionGranted() {
        if(Build.VERSION.SDK_INT >= 23){
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }else{
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            switch(idMenu){
                case R.id.opcionExportarBD:
                    try{
                        helper.exportDB();
                        toast(getString(R.string.seExporto));
                    }catch(IOException e){
                        toast(getString(R.string.noSeExporto));
                    }
                    break;
                case R.id.opcionImportarBD:
                    openFolder();
                    break;
                default:
                    break;
            }
        }
    }

    public void toast(String mensaje){
        Toast.makeText(this,mensaje, Toast.LENGTH_SHORT).show();
    }

    /*
    public boolean editarColectivo(final int position){
        //METODO QUE ENVIA VARIABLES A OTRA ACTIVITY
        //BUEN EJEMPLO PARA VER EL FUNCIONAMIENTO
        Intent nuevo = new Intent(this, AddNewActivity.class);
        nuevo.putExtra("editFlag", 1);
        nuevo.putExtra("idEmpresa", listaColectivos.get(position).getIdEmpresa());
        nuevo.putExtra("nroColectivo", listaColectivos.get(position).getNroColectivo());
        nuevo.putExtra("calificacion", listaColectivos.get(position).getCalificacion());
        nuevo.putExtra("observaciones", listaColectivos.get(position).getObservaciones());
        startActivity(nuevo);
        this.finish();
        return true;
    }
    */
}
