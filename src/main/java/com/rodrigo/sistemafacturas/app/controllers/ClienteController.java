package com.rodrigo.sistemafacturas.app.controllers;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import com.rodrigo.sistemafacturas.app.models.services.IClienteService;
import com.rodrigo.sistemafacturas.app.models.services.IUploadFileService;
import com.rodrigo.sistemafacturas.app.models.services.UploadFileServiceImpl;
import com.rodrigo.sistemafacturas.app.util.paginator.PageRender;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

    private final IClienteService clienteService;
    private final IUploadFileService uploadFileService;

    public ClienteController(IClienteService clienteService, UploadFileServiceImpl uploadFileService) {
        this.clienteService = clienteService;
        this.uploadFileService = uploadFileService;
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

        Resource recurso;
        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"").
                body(recurso);
    }


    @GetMapping("/ver/{id}")
    public String ver(@PathVariable("id") Long id, Map<String, Object> model, RedirectAttributes flash) {

        Cliente cliente = clienteService.fetchByIdWithFacturas(id);

        if (cliente == null) {
            flash.addFlashAttribute("danger", "El cliente no existe en la base de datos!");
            return "redirect:/";
        }

        model.put("cliente", cliente);
        model.put("titulo", "Detalle cliente: " + cliente.getNombre());

        return "ver";
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
                          Model model, @RequestParam("file") MultipartFile foto,
                          RedirectAttributes flash,
                          SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de cliente");
            return "form";
        }
        if (!foto.isEmpty()) {

            if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null && cliente.getFoto().length() > 0) {
                uploadFileService.delete(cliente.getFoto());
            }
            String uniqueFileName;
            try {
                uniqueFileName = uploadFileService.copy(foto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFileName + "'");
            cliente.setFoto(uniqueFileName);
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

    @GetMapping("/eliminar/{id}")
    public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            Cliente cliente = clienteService.findOne(id);
            clienteService.delete(id);
            flash.addFlashAttribute("danger", "Cliente eliminado con exito!");

            if (uploadFileService.delete(cliente.getFoto())) {
                flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + "eliminada con exito!");
            }
        }
        return "redirect:/";
    }
}
