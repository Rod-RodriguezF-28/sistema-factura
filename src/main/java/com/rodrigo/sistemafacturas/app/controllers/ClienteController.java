package com.rodrigo.sistemafacturas.app.controllers;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import com.rodrigo.sistemafacturas.app.models.services.IClienteService;
import com.rodrigo.sistemafacturas.app.util.paginator.PageRender;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

    private final IClienteService clienteService;

    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/")
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Cliente> clientes = clienteService.findAll(pageRequest);
        PageRender<Cliente> pageRender = new PageRender<>("/", clientes);

        model.addAttribute("activeListar", "active");
        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return "listar";
    }

    @GetMapping("/form")
    public String crear(Map<String, Object> model) {
        Cliente cliente = new Cliente();
        model.put("cliente", cliente);
        model.put("titulo", "Formulario de cliente");
        model.put("activeForm", "active");
        model.put("boton", "Crear cliente");
        model.put("estilo", "primary");
        return "form";
    }

    @GetMapping("/form/{id}")
    public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model,
                         RedirectAttributes flash) {
        Cliente cliente;
        if (id > 0) {
            cliente = clienteService.findOne(id);
            if (cliente == null) {
                flash.addFlashAttribute("danger", "El cliente no existe en la base de datos!");
                return "redirect:/";
            }
        } else {
            flash.addFlashAttribute("danger", "El id del cliente no puede ser cero!");
            return "redirect:/";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Editar cliente");
        model.put("boton", "Editar cliente");
        model.put("estilo", "warning");
        return "form";
    }

    @PostMapping("/form")
    public String guardar(@Valid Cliente cliente, BindingResult result,
                          Model model, RedirectAttributes flash,
                          SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de cliente");
            return "form";
        }
        if (cliente.getId() != null) {
            flash.addFlashAttribute("warning", "Cliente editado con exito!");
            clienteService.save(cliente);
            return "redirect:/";
        }
        flash.addFlashAttribute("success", "Cliente creado con exito!");
        clienteService.save(cliente);
        status.setComplete();
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            clienteService.delete(id);
            flash.addFlashAttribute("danger", "Cliente eliminado con exito!");
        }
        return "redirect:/";
    }
}
