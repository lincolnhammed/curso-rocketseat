package pt.lincolnsilva.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * GET - busca informacao
     * POST - adiciona um dado/informacao
     * PUT - alterar um dado/informacao
     * DELETE - remover dado
     * PATCH - altera somente uma parte da informcao
     */
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public UserModel create(@RequestBody UserModel userModel) {
     var user = this.userRepository.findByUsername(userModel.getUsername());
     if (user != null){
         System.out.println("usuario ja existe");
         return null;
     }
     var usuarioCriado =  this.userRepository.save(userModel);

     return usuarioCriado;
    }
}
