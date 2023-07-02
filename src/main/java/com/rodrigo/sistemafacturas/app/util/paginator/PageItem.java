package com.rodrigo.sistemafacturas.app.util.paginator;

public class PageItem {

    private int nro;
    private boolean actual;

    public PageItem(int nro, boolean actual) {
        this.nro = nro;
        this.actual = actual;
    }

    public int getNro() {
        return nro;
    }

    public boolean isActual() {
        return actual;
    }
}
