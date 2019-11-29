package com.nlazzos.viajacomodo.Dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.nlazzos.viajacomodo.DAOs.DAOEmpresa;

/**
 * Created by Nico on 14/12/2017.
 */
public class Dialogo {

    private DAOEmpresa daoEmpresa = new DAOEmpresa();

    public Dialogo() {
    }

    public void crearDialogo(Context context, String title, String message, String si, String no){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(no, null);
        Dialog dialog = builder.create();
        dialog.show();
    }


}
