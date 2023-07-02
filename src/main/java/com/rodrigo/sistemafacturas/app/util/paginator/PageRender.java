package com.rodrigo.sistemafacturas.app.util.paginator;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PageRender<T> {
    private String url;
    private Page<T> page;
    private int totalPaginas;
    private int nroElementosPagina;
    private int paginaActual;
    private List<PageItem> pagina;

    public PageRender(String url, Page<T> page) {
        this.url = url;
        this.page = page;
        this.pagina = new ArrayList<>();

        nroElementosPagina = page.getSize();
        totalPaginas = page.getTotalPages();
        paginaActual = page.getNumber() + 1;

        int desde, hasta;
        if (totalPaginas <= nroElementosPagina) {
            desde = 1;
            hasta = totalPaginas;
        } else {
            if (paginaActual <= nroElementosPagina / 2) {
                desde = 1;
                hasta = nroElementosPagina;
            } else if (paginaActual >= totalPaginas - nroElementosPagina / 2) {
                desde = totalPaginas - nroElementosPagina + 1;
                hasta = nroElementosPagina;
            } else {
                desde = paginaActual - nroElementosPagina / 2;
                hasta = nroElementosPagina;
            }
        }

        for (int i = 0; i < hasta; i++) {
            pagina.add(new PageItem(desde + i, paginaActual == desde + i));
        }
    }

    public String getUrl() {
        return url;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public List<PageItem> getPagina() {
        return pagina;
    }

    public boolean isFirst() {
        return page.isFirst();
    }

    public boolean isLast() {
        return page.isLast();
    }

    public boolean isHasNext() {
        return page.hasNext();
    }

    public boolean isHasPrevious() {
        return page.hasPrevious();
    }
}

