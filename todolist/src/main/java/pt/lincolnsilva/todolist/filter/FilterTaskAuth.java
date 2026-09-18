package pt.lincolnsilva.todolist.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import pt.lincolnsilva.todolist.user.IUserRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    // Repository responsável por buscar os usuários no banco de dados
    @Autowired
    private IUserRepository userRepository;

    // PasswordEncoder será utilizado para comparar a senha
    // enviada pelo usuário com a senha criptografada no banco
    @Autowired
    private PasswordEncoder passwordEncoder;


    // ============================================================
    // ESSE MÉTODO É MUITO IMPORTANTE
    // ============================================================

    // Aqui decidimos se o filtro deve ou não ser executado.
    //
    // No cadastro de usuário:
    //
    // POST /users/
    //
    // ainda não existe usuário autenticado.
    //
    // Portanto, não podemos exigir o Authorization nesse momento.
    //
    // Retornando true, estamos dizendo:
    // "NÃO execute o filtro nessa requisição."
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        System.out.println("aqui estou");
        // Verifica se a requisição é:
        //
        // POST /users/
        //
        // Se for, o filtro será ignorado.
        return request.getServletPath().equals("/users/")
                && request.getMethod().equalsIgnoreCase("POST");
    }


    // ============================================================
    // FILTRO DE AUTENTICAÇÃO
    // ============================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        // ========================================================
        // 1. PEGAR O HEADER AUTHORIZATION
        // ========================================================

        // O cliente envia algo parecido com:
        //
        // Authorization: Basic bGlua29sbjpIZWxs...
        //
        // Esse valor contém username e password codificados em Base64.
        String authorization = request.getHeader("Authorization");


        // ========================================================
        // 2. VERIFICAR SE O AUTHORIZATION FOI ENVIADO
        // ========================================================

        // Se não existe Authorization
        // OU
        // se não começa com "Basic "
        //
        // significa que não temos credenciais.
        if (authorization == null || !authorization.startsWith("Basic ")) {

            // Retorna HTTP 401 = Não autorizado
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Credenciais não fornecidas");

            // Para a execução do filtro
            return;
        }


        try {

            // ====================================================
            // 3. REMOVER "Basic "
            // ====================================================

            // Antes temos:
            //
            // Basic bGlua29sbjpIZWxs...
            //
            // Depois teremos somente:
            //
            // bGlua29sbjpIZWxs...
            String authEncoded = authorization
                    .substring("Basic ".length())
                    .trim();


            // ====================================================
            // 4. DECODIFICAR O BASE64
            // ====================================================

            // Transforma o Base64 novamente em bytes.
            byte[] authDecode = Base64
                    .getDecoder()
                    .decode(authEncoded);


            // Transforma os bytes em String.
            //
            // UTF-8 garante que os caracteres sejam interpretados
            // corretamente.
            String authString = new String(
                    authDecode,
                    StandardCharsets.UTF_8
            );


            // ====================================================
            // 5. SEPARAR USERNAME E PASSWORD
            // ====================================================

            // O resultado do Base64 será algo parecido com:
            //
            // lincoln:123456
            //
            // O ":" separa o username da password.
            String[] credentials = authString.split(":", 2);


            // Se não encontramos username e password,
            // as credenciais são inválidas.
            if (credentials.length != 2) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Credenciais inválidas");

                return;
            }


            // Primeiro elemento = username
            String username = credentials[0]; //lincoln

            // Segundo elemento = password
            String password = credentials[1];// senha


            // ====================================================
            // 6. BUSCAR O USUÁRIO NO BANCO
            // ====================================================

            // Procura no banco um usuário que tenha esse username.
            var user = userRepository.findByUsername(username);


            // Se não encontrou usuário...
            if (user == null) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Usuário não autorizado");

                return;
            }


            // ====================================================
            // 7. COMPARAR A PASSWORD
            // ====================================================

            // A password que veio da requisição é:
            //
            // 123456
            //
            // A password armazenada no banco é algo parecido com:
            //
            // $2a$10$8dK....
            //
            // O BCrypt não deve ser comparado usando "equals".
            //
            // O método matches() faz a comparação corretamente.
            if (!passwordEncoder.matches(password, user.getPassword())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("SENHA INCORRETA");
                return;
            }


            // ====================================================
            // 8. CRIAR A AUTENTICAÇÃO
            // ====================================================

            // Aqui informamos ao Spring Security:
            //
            // "Esse usuário foi autenticado com sucesso."
            //
            // username = nome do usuário autenticado
            // null     = não precisamos guardar a senha aqui
            // emptyList = usuário sem roles/permissões específicas


            var authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList()
                    );


            // ====================================================
            // 9. GUARDAR O USUÁRIO AUTENTICADO
            // ====================================================

            // O SecurityContext guarda a informação de que
            // esse usuário está autenticado durante a requisição.

           /* SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);*/

            // ====================================================
            // 7. CRIA O SECURITY CONTEXT
            // ====================================================

            var context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            // ====================================================
            // 8. COLOCA O CONTEXTO NO SECURITYCONTEXTHOLDER
            // ====================================================

            SecurityContextHolder
                    .setContext(context);

            // ====================================================
            // 9. SALVA O CONTEXTO NA REQUEST
            // ====================================================

            new RequestAttributeSecurityContextRepository()
                    .saveContext(context, request, response);

            // ====================================================
            // 10. CONTINUAR A REQUISIÇÃO
            // ====================================================

            // Depois que o usuário foi autenticado,
            // deixamos a requisição continuar.
            //
            // Por exemplo:
            //
            // POST /tasks/
            //
            // vai chegar no TaskController.
            filterChain.doFilter(request, response);


        } catch (IllegalArgumentException e) {

            // Se o Base64 enviado estiver inválido,
            // entramos aqui.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Usuário não autorizado");
        }
    }
}