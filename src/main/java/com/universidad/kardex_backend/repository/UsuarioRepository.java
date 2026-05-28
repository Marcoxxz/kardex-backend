package com.universidad.kardex_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.universidad.kardex_backend.model.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Busca un usuario por sus credenciales ya procesadas
    Optional<Usuario> findByUsernameAndPassword(String username, String password);
}
