package pt.lincolnsilva.todolist.user;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "usuarios")
public class UserModel {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
