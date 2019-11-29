package com.nlazzos.viajacomodo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.nlazzos.viajacomodo.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Nico on 15/12/2017.
 */
public class ELVAdapter extends BaseExpandableListAdapter{
    private ArrayList<String> listaGroups;
    private Map<String, ArrayList<String>> mapChild;
    private Context context;

    public ELVAdapter(Context context, ArrayList<String> listaGroups, Map<String, ArrayList<String>> mapChild) {
        this.listaGroups = listaGroups;
        this.mapChild = mapChild;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return listaGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return mapChild.get(listaGroups.get(groupPosition)).size();
        //RETORNO SOLO 1, PORQUE LA LISTA DE CHILDS TIENE 2 ELEMENTOS, PERO UNO DE ELLOS LO ESTOY USANDO EN EL
        //GROUP Y NO QUIERO MOSTRARLO EN EL CHILD, ENTONCES SI ESTO RETORNA EL SIZE VERDADERO, ME REPITE EL MISMO
        //ELEMENTO EN EL CHILD, UNA CANTIDAD DE VECES IGUAL AL SIZE
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listaGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mapChild.get(listaGroups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String tituloCategoria = getGroup(groupPosition).toString();
        String calificacion =  getChild(groupPosition, 0).toString();
        convertView = LayoutInflater.from(context).inflate(R.layout.elv_group, null);
        TextView txtGroup = (TextView) convertView.findViewById(R.id.txtGroup);
        TextView txtCalificacion = (TextView) convertView.findViewById(R.id.txtCalificacion);
        txtGroup.setText(tituloCategoria);
        txtCalificacion.setText(calificacion);

        if (isExpanded) {
            txtGroup.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less_black_24dp, 0);
        } else {
            txtGroup.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_black_24dp, 0);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String tituloChild = getChild(groupPosition, 1).toString();
        convertView = LayoutInflater.from(context).inflate(R.layout.elv_child, null);
        TextView txtChild = (TextView) convertView.findViewById(R.id.txtChild);
        txtChild.setText(tituloChild);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

/* ESTE ES EL CODIGO ORIGINAL
public class ELVAdapter extends BaseExpandableListAdapter {
    private ArrayList<String> listaGroups;
    private Map<String, ArrayList<String>> mapChild;
    private Context context;

    public ELVAdapter(Context context, ArrayList<String> listaGroups, Map<String, ArrayList<String>> mapChild) {
        this.listaGroups = listaGroups;
        this.mapChild = mapChild;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return listaGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mapChild.get(listaGroups.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listaGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mapChild.get(listaGroups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String tituloCategoria = getGroup(groupPosition).toString();
        convertView = LayoutInflater.from(context).inflate(R.layout.elv_group, null);
        TextView txtGroup = (TextView) convertView.findViewById(R.id.txtGroup);
        txtGroup.setText(tituloCategoria);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String tituloChild = getChild(groupPosition, childPosition).toString();
        convertView = LayoutInflater.from(context).inflate(R.layout.elv_child, null);
        TextView txtChild = (TextView) convertView.findViewById(R.id.txtChild);
        txtChild.setText(tituloChild);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
*/
