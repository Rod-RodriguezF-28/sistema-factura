package com.rodrigo.sistemafacturas.app.controllers;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import com.rodrigo.sistemafacturas.app.models.entity.Factura;
import com.rodrigo.sistemafacturas.app.models.entity.ItemFactura;
import com.rodrigo.sistemafacturas.app.models.entity.Producto;
import com.rodrigo.sistemafacturas.app.models.services.IClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

    private final IClienteService clienteService;

    public FacturaController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable(name = "id") Long id,
                      Model model,
                      RedirectAttributes flash) {

        Factura factura = clienteService.findFacturaById(id);
        if (factura == null) {
            flash.addFlashAttribute("danger", "La factura no existe en la base de datos!");
            return "redirect:/";
        }

        model.addAttribute("factura", factura);
        model.addAttribute("titulo", "Factura: ".concat(factura.getDescripcion()));
        return "factura/ver";
    }

    @GetMapping("/form/{clienteId}")
    public String crear(@PathVariable(name = "clienteId") Long clienteId, Map<String, Object> model,
                        RedirectAttributes flash) {

        Cliente cliente =  clienteService.findOne(clienteId);

        if (cliente == null) {
            flash.addFlashAttribute("danger", "El cliente no existe en la base de datos!");
            return "redirect:/";
        }

        Factura factura = new Factura();
        factura.setCliente(cliente);
        model.put("factura", factura);
        model.put("titulo", "Crear factura");

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
                          RedirectAttributes flash, SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Crear factura");
            return "factura/form";
        }

        if (itemId == null || itemId.length == 0) {
            model.addAttribute("titulo", "Crear factura");
            model.addAttribute("danger", "La factura no puede no tener productos!");
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
        flash.addFlashAttribute("success", "Factura creada con exito!");
        return "redirect:/ver/" + factura.getCliente().getId();
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {

        Factura factura = clienteService.findFacturaById(id);
        if (factura != null) {
            clienteService.deleteFactura(id);
            flash.addFlashAttribute("success", "Factura eliminada con exito!");
            return "redirect:/ver/" + factura.getCliente().getId();
        }

        flash.addFlashAttribute("danger", "La factura no existe en la base de datos, no se pudo eliminar!");
        return "redirect:/";
    }
}
