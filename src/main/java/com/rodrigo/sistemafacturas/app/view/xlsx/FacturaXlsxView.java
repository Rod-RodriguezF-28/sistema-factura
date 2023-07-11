package com.rodrigo.sistemafacturas.app.view.xlsx;

import com.rodrigo.sistemafacturas.app.models.entity.Factura;
import com.rodrigo.sistemafacturas.app.models.entity.ItemFactura;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import java.util.Map;

@Component("factura/ver.xlsx")
public class FacturaXlsxView extends AbstractXlsxView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) {


        Factura factura = (Factura) model.get("factura");
        Sheet sheet = workbook.createSheet("Factura Cliente - " + factura.getCliente().getNombre());

        String fileName = "Factura Cliente - " + factura.getCliente().getNombre() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        MessageSourceAccessor mensajes = getMessageSourceAccessor();

        Cell cell;
/*
        cell.setCellValue("Datos del cliente");
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue(factura.getCliente().getEmail());
*/
        assert mensajes != null;
        sheet.createRow(0).createCell(0).setCellValue(mensajes.getMessage("text.factura.ver.datos.cliente"));
        sheet.createRow(1).createCell(0).setCellValue("Nombre Cliente: " +factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
        sheet.createRow(2).createCell(0).setCellValue("Email Cliente: " + factura.getCliente().getEmail());

        sheet.createRow(4).createCell(0).setCellValue(mensajes.getMessage("text.factura.ver.datos.factura"));
        sheet.createRow(5).createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.folio") + ": " + factura.getId());
        sheet.createRow(6).createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.descripcion") + ": " + factura.getDescripcion());
        sheet.createRow(7).createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.fecha") + ": " + factura.getCreateAt());

        CellStyle theaderStyle = workbook.createCellStyle();
        theaderStyle.setBorderBottom(BorderStyle.MEDIUM);
        theaderStyle.setBorderTop(BorderStyle.MEDIUM);
        theaderStyle.setBorderRight(BorderStyle.MEDIUM);
        theaderStyle.setBorderLeft(BorderStyle.MEDIUM);
        theaderStyle.setFillForegroundColor(IndexedColors.GOLD.index);
        theaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle tbodyStyle = workbook.createCellStyle();
        tbodyStyle.setBorderBottom(BorderStyle.MEDIUM);
        tbodyStyle.setBorderTop(BorderStyle.MEDIUM);
        tbodyStyle.setBorderRight(BorderStyle.MEDIUM);
        tbodyStyle.setBorderLeft(BorderStyle.MEDIUM);

        Row header = sheet.createRow(9);
        header.createCell(0).setCellValue(mensajes.getMessage("text.factura.form.item.nombre"));
        header.createCell(1).setCellValue(mensajes.getMessage("text.factura.form.item.precio"));
        header.createCell(2).setCellValue(mensajes.getMessage("text.factura.form.item.cantidad"));
        header.createCell(3).setCellValue(mensajes.getMessage("text.factura.form.item.total"));
        header.getCell(0).setCellStyle(theaderStyle);
        header.getCell(1).setCellStyle(theaderStyle);
        header.getCell(2).setCellStyle(theaderStyle);
        header.getCell(3).setCellStyle(theaderStyle);

        int rownum = 10;

        for (ItemFactura item: factura.getItems()) {
            Row fila = sheet.createRow(rownum ++);
            cell = fila.createCell(0);
            cell.setCellValue(item.getProducto().getNombre());
            cell.setCellStyle(tbodyStyle);

            cell = fila.createCell(1);
            cell.setCellValue(item.getProducto().getPrecio());
            cell.setCellStyle(tbodyStyle);

            cell = fila.createCell(2);
            cell.setCellValue(item.getCantidad());
            cell.setCellStyle(tbodyStyle);

            cell = fila.createCell(3);
            cell.setCellValue(item.calcularImporte());
            cell.setCellStyle(tbodyStyle);
        }

        Row filaTotal = sheet.createRow(rownum);
        cell = filaTotal.createCell(2);
        cell.setCellValue(mensajes.getMessage("text.factura.form.total") + ": ");
        cell.setCellStyle(tbodyStyle);

        cell = filaTotal.createCell(3);
        cell.setCellValue(factura.getTotal());
        cell.setCellStyle(tbodyStyle);

    }
}
