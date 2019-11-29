package com.nlazzos.viajacomodo.Entities;

/**
 * Created by Nico on 12/12/2017.
 */
public class Empresa {
    private int idEmpresa;
    private String nombre;

    public Empresa(int idEmpresa, String nombre) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
    }

    public Empresa(){
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
