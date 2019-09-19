package com.byuwur.onlinecongress;

public class HolderNotif {
    private String notiftext;
    private String notiffecha;

    HolderNotif(String nombre, String id) {
        this.notiftext = nombre;
        this.notiffecha = id;
    }

    public String getNombre() {
        return notiftext;
    }

    public void setNombre(String nombre) {
        this.notiftext = nombre;
    }

    public String getId() {
        return notiffecha;
    }

    public void setId(String id) {
        this.notiffecha = id;
    }
}
