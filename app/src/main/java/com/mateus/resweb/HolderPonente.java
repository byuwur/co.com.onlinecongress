package com.mateus.resweb;

public class HolderPonente {
    private String pontentenombre;
    private String pontenteid;
    private String pontentenivel;
    private String pontenteinst;
    private String pontenteidioma;
    private String pontentepais;
    private String pontenteimg;

    public HolderPonente(String nombre, String id, String nivel, String inst, String idioma, String pais, String img) {
        this.pontentenombre = nombre;
        this.pontenteid = id;
        this.pontentenivel = nivel;
        this.pontenteinst = inst;
        this.pontenteidioma = idioma;
        this.pontentepais = pais;
        this.pontenteimg = img;
    }

    public String getNombre() {
        return pontentenombre;
    }

    public void setNombre(String nombre) {
        this.pontentenombre = nombre;
    }

    public String getIdC() {
        return pontenteid;
    }

    public void setIdC(String id) {
        this.pontenteid = id;
    }

    public String getDireccion() {
        return pontentenivel;
    }

    public void setDireccion(String nivel) {
        this.pontentenivel = nivel;
    }

    public String getCiudad() {
        return pontenteinst;
    }

    public void setCiudad(String inst) {
        this.pontenteinst = inst;
    }

    public String getIdR() {
        return pontenteidioma;
    }

    public void setIdR(String idioma) {
        this.pontenteidioma = idioma;
    }

    public String getDia() {
        return pontentepais;
    }

    public void setDia(String pais) {
        this.pontentepais = pais;
    }

    public String getImg() {
        return pontenteimg;
    }

    public void setImgcancha(String img) {
        this.pontenteimg = img;
    }
}