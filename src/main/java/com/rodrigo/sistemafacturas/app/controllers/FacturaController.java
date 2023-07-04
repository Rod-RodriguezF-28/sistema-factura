package com.rodrigo.sistemafacturas.app.controllers;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import com.rodrigo.sistemafacturas.app.models.entity.Factura;
import com.rodrigo.sistemafacturas.app.models.entity.Producto;
import com.rodrigo.sistemafacturas.app.models.services.IClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
}
