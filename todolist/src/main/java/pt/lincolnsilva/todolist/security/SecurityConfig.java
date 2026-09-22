package pt.lincolnsilva.todolist.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import pt.lincolnsilva.todolist.filter.FilterTaskAuth;

import java.util.List;

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
    // CONFIGURAÇÃO DE CORS
    // ============================================================

    // O CORS permite que o nosso frontend React,
    // que estará em outro endereço,
    // possa fazer requisições para esta API.
    //
    // Durante o desenvolvimento, nosso React estará
    // rodando normalmente em:
    //
    // http://localhost:5173
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();


        // Define quais endereços podem acessar nossa API.
        //
        // Aqui estamos permitindo o React que está
        // rodando localmente na porta 5173.
        config.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://frontend-curso-rocketseat.onrender.com"
                )
        );


        // Define quais métodos HTTP o frontend
        // pode utilizar na nossa API.
        //
        // GET    -> buscar dados
        // POST   -> criar dados
        // PUT    -> atualizar dados
        // DELETE -> apagar dados
        // OPTIONS -> requisição utilizada pelo navegador
        //            para verificar as permissões do CORS.
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );


        // Permite que o frontend envie qualquer
        // cabeçalho HTTP necessário para a API.
        //
        // Por exemplo:
        // Authorization
        // Content-Type
        config.setAllowedHeaders(
                List.of("*")
        );


        // Permite o envio de credenciais nas requisições.
        //
        // Isso pode ser utilizado quando trabalhamos
        // com autenticação baseada em credenciais/cookies.
        config.setAllowCredentials(true);


        // Cria a fonte de configuração do CORS.
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        // Aplica essa configuração para todas as rotas
        // da nossa API.
        source.registerCorsConfiguration("/**", config);


        return source;
    }


    // ============================================================
    // CONFIGURAÇÃO DO SPRING SECURITY
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            FilterTaskAuth filterTaskAuth,
            CorsConfigurationSource corsConfigurationSource) throws Exception {

        http


                // Ativa o CORS no Spring Security.
                //
                // Aqui estamos dizendo para o Spring Security
                // utilizar a configuração que criamos acima.
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource)
                )


                // Desativa CSRF.
                // Normalmente é necessário em APIs REST que
                // utilizam autenticação própria/token/etc.
                .csrf(csrf -> csrf.disable())


                // =================================================
                // REGRAS DE ACESSO
                // =================================================

                // Aqui definimos quem pode acessar
                // cada endpoint da nossa API.
                .authorizeHttpRequests(auth -> auth


                        // Permite requisições OPTIONS.
                        //
                        // O navegador pode enviar uma requisição
                        // OPTIONS antes da requisição principal
                        // para verificar as regras do CORS.
                        //
                        // Sem essa permissão, o Spring Security
                        // poderia bloquear essa verificação.
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


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

