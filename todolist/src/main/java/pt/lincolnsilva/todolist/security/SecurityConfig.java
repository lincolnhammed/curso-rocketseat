package pt.lincolnsilva.todolist.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import pt.lincolnsilva.todolist.filter.FilterTaskAuth;

@Configuration
public class SecurityConfig {


    // ============================================================
    // CRIAR O PASSWORD ENCODER
    // ============================================================

    // Aqui dizemos ao Spring:
    //
    // "Quando alguém precisar criptografar uma senha,
    // utilize BCrypt."
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // ============================================================
    // CONFIGURAÇÃO DO SPRING SECURITY
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            FilterTaskAuth filterTaskAuth) throws Exception {

        http

                // Desativa CSRF.
                // Normalmente é necessário em APIs REST que
                // utilizam autenticação própria/token/etc.
                .csrf(csrf -> csrf.disable())


                // =================================================
                // REGRAS DE ACESSO
                // guarda as regras de autorização:
                //montando a cadeia de filtros.
                // =================================================
                .authorizeHttpRequests(auth -> auth

                        // Permite que qualquer pessoa crie um usuário.
                        //
                        // IMPORTANTE:
                        // O usuário ainda não está autenticado
                        // nesse momento.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/users",
                                "/users/"
                        ).permitAll()


                        // Todas as outras requisições precisam
                        // estar autenticadas.
                        .anyRequest().authenticated()
                )


                // =================================================
                // ADICIONAR NOSSO FILTRO
                // =================================================

                // Nosso filtro será executado antes do filtro
                // padrão de autenticação por username/password
                // do Spring Security.

                //.httpBasic(Customizer.withDefaults())
                .addFilterBefore(
                        filterTaskAuth,
                        UsernamePasswordAuthenticationFilter.class
                );


        // Retorna a configuração pronta.
        return http.build();
    }
}