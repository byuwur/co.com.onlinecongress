package com.byuwur.onlinecongress;

public class HolderConferencia {
    private String conferencianombre;
    private String conferenciaid;
    private String conferenciavalor;
    private String conferenciaidioma;
    private String conferenciacategoria;
    private String conferenciadias;
    private String conferenciahorario;
    private String conferenciaimg;

    public HolderConferencia(String nombre, String id, String valor, String idioma, String categoria, String dias, String horario, String imgcancha) {
        this.conferencianombre = nombre;
        this.conferenciaid = id;
        this.conferenciavalor = valor;
        this.conferenciaidioma = idioma;
        this.conferenciacategoria = categoria;
        this.conferenciadias = dias;
        this.conferenciahorario = horario;
        this.conferenciaimg = imgcancha;
    }

    public String getNombre() {
        return conferencianombre;
    }

    public void setNombre(String nombre) {
        this.conferencianombre = nombre;
    }

    public String getId() {
        return conferenciaid;
    }

    public void setId(String id) {
        this.conferenciaid = id;
    }

    public String getValor() {
        return conferenciavalor;
    }

    public void setValor(String valor) {
        this.conferenciavalor = valor;
    }

    public String getDireccion() {
        return conferenciaidioma;
    }

    public void setDireccion(String idioma) {
        this.conferenciaidioma = idioma;
    }

    public String getCiudad() {
        return conferenciacategoria;
    }

    public void setCiudad(String categoria) {
        this.conferenciacategoria = categoria;
    }

    public String getDias() {
        return conferenciadias;
    }

    public void setDias(String dias) {
        this.conferenciadias = dias;
    }

    public String getHorario() {
        return conferenciahorario;
    }

    public void setHorario(String horario) {
        this.conferenciahorario = horario;
    }

    public String getImg() {
        return conferenciaimg;
    }

    public void setImgcancha(String imgcancha) {
        this.conferenciaimg = imgcancha;
    }
}
