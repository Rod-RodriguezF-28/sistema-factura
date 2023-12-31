package com.rodrigo.sistemafacturas.app.controllers;

import com.rodrigo.sistemafacturas.app.models.entity.Cliente;
import com.rodrigo.sistemafacturas.app.models.services.IClienteService;
import com.rodrigo.sistemafacturas.app.models.services.IUploadFileService;
import com.rodrigo.sistemafacturas.app.models.services.UploadFileServiceImpl;
import com.rodrigo.sistemafacturas.app.util.paginator.PageRender;
import jakarta.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

    private final IClienteService clienteService;
    private final IUploadFileService uploadFileService;

    private final MessageSource messageSource;

    protected final Log logger = LogFactory.getLog(this.getClass());

    public ClienteController(IClienteService clienteService, UploadFileServiceImpl uploadFileService, MessageSource messageSource) {
        this.clienteService = clienteService;
        this.uploadFileService = uploadFileService;
        this.messageSource = messageSource;
    }

    @Secured("ROLE_USER")
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

        Resource recurso;
        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"").body(recurso);
    }

    @Secured("ROLE_USER")
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable("id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {

        Cliente cliente = clienteService.fetchByIdWithFacturas(id);

        if (cliente == null) {
            flash.addFlashAttribute("danger", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
            return "redirect:/";
        }

        model.put("cliente", cliente);
        model.put("titulo", messageSource.getMessage("text.cliente.detalle.titulo", null, locale).concat(": ").concat(cliente.getNombre()));

        return "ver";
    }

    @GetMapping("/")
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Locale locale) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (hasRole()) {
            logger.info("Hola, ".concat(auth.getName()).concat(" tienes acceso!"));
        } else {
            logger.info("Hola, ".concat(auth.getName()).concat(" no tienes acceso!"));
        }

        if (auth != null && !auth.getName().equals("anonymousUser")) {
            logger.info("Hola usuario autenticado, tu username es: ".concat(auth.getName()));
        }

        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Cliente> clientes = clienteService.findAll(pageRequest);
        PageRender<Cliente> pageRender = new PageRender<>("/", clientes);

        model.addAttribute("activeListar", "active");
        model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null, locale));
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return "listar";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/form")
    public String crear(Map<String, Object> model, Locale locale) {
        Cliente cliente = new Cliente();
        model.put("cliente", cliente);
        model.put("titulo", messageSource.getMessage("text.cliente.form.titulo.crear", null, locale));
        model.put("activeForm", "active");
        model.put("boton", messageSource.getMessage("text.cliente.form.boton", null, locale));
        model.put("estilo", "primary");
        return "form";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/form/{id}")
    public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {
        Cliente cliente;
        if (id > 0) {
            cliente = clienteService.findOne(id);
            if (cliente == null) {
                flash.addFlashAttribute("danger", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
                return "redirect:/";
            }
        } else {
            flash.addFlashAttribute("danger", messageSource.getMessage("text.cliente.flash.id.error", null, locale));
            return "redirect:/";
        }
        model.put("cliente", cliente);
        model.put("titulo", messageSource.getMessage("text.cliente.form.titulo.editar", null, locale));
        model.put("boton", messageSource.getMessage("text.cliente.form.boton.editar", null, locale));
        model.put("estilo", "warning");
        return "form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/form")
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
                          @RequestParam("file") MultipartFile foto,
                          RedirectAttributes flash, SessionStatus status, Locale locale) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", messageSource.getMessage("text.cliente.form.titulo", null, locale));
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
            flash.addFlashAttribute("info", messageSource.getMessage("text.cliente.flash.foto.subir.success", null, locale) + "'" + uniqueFileName + "'");
            cliente.setFoto(uniqueFileName);
        }
        if (cliente.getId() != null) {
            flash.addFlashAttribute("warning", messageSource.getMessage("text.cliente.flash.editar.success", null, locale));
            clienteService.save(cliente);
            return "redirect:/";
        }
        flash.addFlashAttribute("success", messageSource.getMessage("text.cliente.flash.crear.success", null, locale));
        clienteService.save(cliente);
        status.setComplete();
        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/eliminar/{id}")
    public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {
        if (id > 0) {
            Cliente cliente = clienteService.findOne(id);
            clienteService.delete(id);
            flash.addFlashAttribute("danger", messageSource.getMessage("text.cliente.flash.eliminar.success", null, locale));

            if (uploadFileService.delete(cliente.getFoto())) {
                String mensajeFotoEliminar = String.format(messageSource.getMessage("text.cliente.flash.foto.eliminar.success", null, locale), cliente.getFoto());
                flash.addFlashAttribute("info", mensajeFotoEliminar);
            }
        }
        return "redirect:/";
    }

    private boolean hasRole() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null) {
            return false;
        }

        Authentication auth = context.getAuthentication();

        if (auth == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        return authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

//        for (GrantedAuthority authority : authorities) {
//            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
//                logger.info("Hola usuario, ".concat(auth.getName()).concat(" tu rol es: ".concat(authority.getAuthority())));
//                return true;
//            }
//        }
//
//        return false;
    }
}
