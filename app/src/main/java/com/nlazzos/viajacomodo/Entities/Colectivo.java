package com.nlazzos.viajacomodo.Entities;

/**
 * Created by Nico on 12/12/2017.
 */
public class Colectivo {
    private int nroColectivo;
    private int idEmpresa;
    private String empresa;
    private int idCalificacion;
    private String calificacion;
    private String observaciones;

    public Colectivo(int nroColectivo, int idEmpresa, int idCalificacion, String observaciones) {
        this.nroColectivo = nroColectivo;
        this.idEmpresa = idEmpresa;
        this.idCalificacion = idCalificacion;
        this.observaciones = observaciones;
    }

    public Colectivo(){
    }

    public int getNroColectivo() {
        return nroColectivo;
    }

    public void setNroColectivo(int nroColectivo) {
        this.nroColectivo = nroColectivo;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }
}
