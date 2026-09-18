package pt.lincolnsilva.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import pt.lincolnsilva.todolist.user.UserModel;
import pt.lincolnsilva.todolist.util.Utils;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        // Pega a autenticação atual
        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        /* Se não encontrar, tenta pelo Repository
        if (authentication == null) {

            var repository =
                    new RequestAttributeSecurityContextRepository();

            authentication =
                    repository
                            .loadDeferredContext(request)
                            .get()
                            .getAuthentication();
        }

        // Verifica se existe usuário autenticado
        if (authentication == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não autenticado");
        }
        */
        var user = (UserModel) authentication.getPrincipal();

        taskModel.setIdUser(user.getId());

        var currentDate = LocalDateTime.now();
        if (taskModel.getStartAt() == null
                || taskModel.getEndAt() == null) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("As datas são obrigatórias");
        }

        if (!taskModel.getStartAt().isAfter(currentDate)
                || !taskModel.getEndAt().isAfter(currentDate)) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("As datas devem ser posteriores à data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser anterior à data de término");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> list(HttpServletRequest request) {

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        var user = (UserModel) authentication.getPrincipal();

        var tasks = this.taskRepository.findByIdUser(user.getId());

        if (tasks.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        var user = (UserModel) authentication.getPrincipal();

        var taskUp = this.taskRepository.findById(id).orElse(null);



        if (taskUp == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)//404
                    .body("Usuario nao existe");
        }
        if (!taskUp.getIdUser().equals(user.getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403 - Proibido
                    .body("Você não tem permissão para alterar esta tarefa.");

        }
        // System.out.println("Titulo recebido: " + taskModel.getTitulo());
        // System.out.println("Titulo antigo: " + taskUp.getTitulo());

        Utils.copyNonNullProperties(taskModel, taskUp);

        // System.out.println("Titulo depois do copy: " + taskUp.getTitulo());
        //taskUp.setTitulo(taskModel.getTitulo());
        //taskUp.setDescricao(taskModel.getDescricao());

        var task = this.taskRepository.save(taskUp);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

}
