package pt.lincolnsilva.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    // Repository responsável por acessar a tabela "usuarios"
    @Autowired
    private IUserRepository userRepository;

    // Responsável por criptografar a senha usando BCrypt
    @Autowired
    private PasswordEncoder passwordEncoder;


    // ============================================================
    // CRIAR USUÁRIO
    // ============================================================

    // Endpoint:
    //
    // POST /users/
    //
    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel) {


        // ========================================================
        // 1. VERIFICAR SE O USERNAME JÁ EXISTE
        // ========================================================

        // Procura no banco um usuário com o username
        // enviado na requisição.
        var user = this.userRepository
                .findByUsername(userModel.getUsername());


        // Se encontrou um usuário...
        if (user != null) {

            // Retorna HTTP 400
            // dizendo que o usuário já existe.
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("usuario ja existe");
        }


        // ========================================================
        // 2. CRIPTOGRAFAR A SENHA
        // ========================================================

        // A senha recebida pode ser:
        //
        // 123456
        //
        // O BCrypt transforma em algo parecido com:
        //
        // $2a$10$abc123....
        //
        String passwordHash =
                passwordEncoder.encode(userModel.getPassword());


        // Substitui a senha original pela senha criptografada.
        userModel.setPassword(passwordHash);


        // ========================================================
        // 3. SALVAR NO BANCO
        // ========================================================

        // O JPA/Hibernate vai fazer o INSERT na tabela usuarios.
        var usuarioCriado =
                this.userRepository.save(userModel);


        // ========================================================
        // 4. RETORNAR RESPOSTA
        // ========================================================

        // HTTP 201 = recurso criado com sucesso.
        //
        // Retorna também o usuário criado.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioCriado);
    }
}