package com.mateus.resweb;

public class HolderAgendado {
    private String agendadonombre;
    private String agendadoid;
    private String agendadovalor;
    private String agendadoidioma;
    private String agendadocategoria;
    private String agendadodias;
    private String agendadohorario;
    private String agendadoimg;

    public HolderAgendado(String nombre, String id, String valor, String idioma, String categoria, String dias, String horario, String imgcancha) {
        this.agendadonombre = nombre;
        this.agendadoid = id;
        this.agendadovalor = valor;
        this.agendadoidioma = idioma;
        this.agendadocategoria = categoria;
        this.agendadodias = dias;
        this.agendadohorario = horario;
        this.agendadoimg = imgcancha;
    }

    public String getNombre() {
        return agendadonombre;
    }

    public void setNombre(String nombre) {
        this.agendadonombre = nombre;
    }

    public String getId() {
        return agendadoid;
    }

    public void setId(String id) {
        this.agendadoid = id;
    }

    public String getValor() {
        return agendadovalor;
    }

    public void setValor(String valor) {
        this.agendadovalor = valor;
    }

    public String getDireccion() {
        return agendadoidioma;
    }

    public void setDireccion(String idioma) {
        this.agendadoidioma = idioma;
    }

    public String getCiudad() {
        return agendadocategoria;
    }

    public void setCiudad(String categoria) {
        this.agendadocategoria = categoria;
    }

    public String getDias() {
        return agendadodias;
    }

    public void setDias(String dias) {
        this.agendadodias = dias;
    }

    public String getHorario() {
        return agendadohorario;
    }

    public void setHorario(String horario) {
        this.agendadohorario = horario;
    }

    public String getImg() {
        return agendadoimg;
    }

    public void setImgcancha(String imgcancha) {
        this.agendadoimg = imgcancha;
    }
}
