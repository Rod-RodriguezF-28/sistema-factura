package com.rodrigo.sistemafacturas.app.controllers;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import com.rodrigo.sistemafacturas.app.models.entity.Factura;
import com.rodrigo.sistemafacturas.app.models.entity.ItemFactura;
import com.rodrigo.sistemafacturas.app.models.entity.Producto;
import com.rodrigo.sistemafacturas.app.models.services.IClienteService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Secured("ROLE_ADMIN")
@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

    private final IClienteService clienteService;
    private final MessageSource messageSource;

    public FacturaController(IClienteService clienteService, MessageSource messageSource) {
        this.clienteService = clienteService;
        this.messageSource = messageSource;
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable(name = "id") Long id,
                      Model model,
                      RedirectAttributes flash, Locale locale) {

        Factura factura = clienteService.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id); //clienteService.findFacturaById(id);
        if (factura == null) {
            flash.addFlashAttribute("error", messageSource.getMessage("text.factura.flash.db.error", null, locale));
            return "redirect:/";
        }

        model.addAttribute("factura", factura);
        model.addAttribute("titulo", String.format(messageSource.getMessage("text.factura.ver.titulo", null, locale), factura.getDescripcion()));
        return "factura/ver";
    }

    @GetMapping("/form/{clienteId}")
    public String crear(@PathVariable(name = "clienteId") Long clienteId, Map<String, Object> model,
                        RedirectAttributes flash, Locale locale) {

        Cliente cliente =  clienteService.findOne(clienteId);

        if (cliente == null) {
            flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
            return "redirect:/";
        }

        Factura factura = new Factura();
        factura.setCliente(cliente);
        model.put("factura", factura);
        model.put("titulo", messageSource.getMessage("text.factura.form.titulo", null, locale));

        return "factura/form";
    }

    @GetMapping(value = "/cargar-productos/{term}", produces = {"application/json"})
    public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {
        return clienteService.findByNombre(term);
    }

    @PostMapping("/form")
    public String guardar(@Valid Factura factura,
                          BindingResult result,
                          Model model,
                          @RequestParam(name = "item_id[]", required = false) Long[] itemId,
                          @RequestParam(name = "cantidad[]", required = false) Integer[] cantidad,
                          RedirectAttributes flash, SessionStatus status, Locale locale) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", messageSource.getMessage("text.factura.form.titulo", null, locale));
            return "factura/form";
        }

        if (itemId == null || itemId.length == 0) {
            model.addAttribute("titulo", messageSource.getMessage("text.factura.form.titulo", null, locale));
            model.addAttribute("danger", messageSource.getMessage("text.factura.flash.lineas.error", null, locale));
            return "factura/form";
        }

        for (int i=0; i < itemId.length; i++) {
            Producto producto = clienteService.findProductoById(itemId[i]);
            ItemFactura linea = new ItemFactura();
            linea.setCantidad(cantidad[i]);
            linea.setProducto(producto);
            factura.addItemFactura(linea);
        }

        clienteService.saveFactura(factura);
        status.setComplete();
        flash.addFlashAttribute("success", messageSource.getMessage("text.factura.flash.crear.success", null, locale));
        return "redirect:/ver/" + factura.getCliente().getId();
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash, Locale locale) {

        Factura factura = clienteService.findFacturaById(id);
        if (factura != null) {
            clienteService.deleteFactura(id);
            flash.addFlashAttribute("success", messageSource.getMessage("text.factura.flash.eliminar.success", null, locale));
            return "redirect:/ver/" + factura.getCliente().getId();
        }

        flash.addFlashAttribute("danger", messageSource.getMessage("text.factura.flash.db.error", null, locale));
        return "redirect:/";
    }
}
