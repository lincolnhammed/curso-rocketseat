package pt.lincolnsilva.todolist.task;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_tasks")
public class TaskModel {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String descricao;
    @Column(length = 50)
//    @Size(max = 50, message = "O título deve conter no máximo 50 caracteres")
    private String titulo;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String priority;

    private UUID idUser;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setTitulo(String titulo) throws Exception {
        if (titulo != null && titulo.length() > 50) {
            throw new Exception("o campo titulo deve conter no maximo 50 caracteres");
        }
        this.titulo = titulo;
    }
}
