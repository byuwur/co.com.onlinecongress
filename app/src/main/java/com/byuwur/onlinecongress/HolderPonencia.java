package com.byuwur.onlinecongress;

public class HolderPonencia {
    private String ponencianombre;
    private String ponenciaid;
    private String ponenciavalor;
    private String ponenciaidioma;
    private String ponenciacategoria;
    private String ponenciadias;
    private String ponenciahorario;
    private String ponenciaimg;

    public HolderPonencia(String nombre, String id, String valor, String idioma, String categoria, String dias, String horario, String imgcancha) {
        this.ponencianombre = nombre;
        this.ponenciaid = id;
        this.ponenciavalor = valor;
        this.ponenciaidioma = idioma;
        this.ponenciacategoria = categoria;
        this.ponenciadias = dias;
        this.ponenciahorario = horario;
        this.ponenciaimg = imgcancha;
    }

    public String getNombre() {
        return ponencianombre;
    }

    public void setNombre(String nombre) {
        this.ponencianombre = nombre;
    }

    public String getId() {
        return ponenciaid;
    }

    public void setId(String id) {
        this.ponenciaid = id;
    }

    public String getValor() {
        return ponenciavalor;
    }

    public void setValor(String valor) {
        this.ponenciavalor = valor;
    }

    public String getDireccion() {
        return ponenciaidioma;
    }

    public void setDireccion(String idioma) {
        this.ponenciaidioma = idioma;
    }

    public String getCiudad() {
        return ponenciacategoria;
    }

    public void setCiudad(String categoria) {
        this.ponenciacategoria = categoria;
    }

    public String getDias() {
        return ponenciadias;
    }

    public void setDias(String dias) {
        this.ponenciadias = dias;
    }

    public String getHorario() {
        return ponenciahorario;
    }

    public void setHorario(String horario) {
        this.ponenciahorario = horario;
    }

    public String getImg() {
        return ponenciaimg;
    }

    public void setImgcancha(String imgcancha) {
        this.ponenciaimg = imgcancha;
    }
}
