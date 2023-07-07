package com.rodrigo.sistemafacturas.app.models.services;

import com.rodrigo.sistemafacturas.app.models.dao.IUsuarioDao;
import com.rodrigo.sistemafacturas.app.models.entity.Role;
import com.rodrigo.sistemafacturas.app.models.entity.Usuario;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {

    private final IUsuarioDao usuarioDao;

    protected final Log logger = LogFactory.getLog(this.getClass());

    public JpaUserDetailsService(IUsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioDao.findByUsername(username);

        if (usuario == null) {
            logger.error("Error login: no existe el usuario: '" + username + "'");
            throw new UsernameNotFoundException("Username: " + username + " no existe!");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : usuario.getRoles()) {
            logger.info("Role: ".concat(role.getAuthority()));
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        if (authorities.isEmpty()) {
            logger.error("Error login: usuario: '" +username + "' no tiene roles asignados");
            throw new UsernameNotFoundException("Error login: usuario: '" + username + "' no tiene roles asignados");
        }

        return new User(username, usuario.getPassword(), usuario.getEnable(), true,
                true, true, authorities);
    }
}
