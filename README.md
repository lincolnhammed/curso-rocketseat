# Todo List - Backend

API REST para gerenciamento de tarefas, desenvolvida com Java e Spring Boot.

## Sobre o projeto

Este projeto foi desenvolvido durante meus estudos de desenvolvimento backend com Java e Spring Boot.

A aplicação possui autenticação de usuários e permite criar, consultar e atualizar tarefas associadas ao usuário autenticado.

O projeto também possui um frontend desenvolvido em React, responsável pela interface da aplicação.

## Tecnologias

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Lombok
- Git
- GitHub

## Funcionalidades

### Usuários

- Cadastro de usuários
- Autenticação utilizando Basic Authentication
- Senhas protegidas utilizando BCrypt

### Tarefas

O usuário autenticado pode:

- Criar tarefas
- Listar suas tarefas
- Atualizar suas tarefas
- Definir título
- Definir descrição
- Definir prioridade
- Definir data e hora de início
- Definir data e hora de término

Cada tarefa é associada ao usuário que a criou.

## Segurança

A aplicação utiliza Spring Security para controlar o acesso à API.

Foi implementado um filtro personalizado para realizar a autenticação utilizando Basic Authentication.

As senhas dos usuários são protegidas utilizando `BCryptPasswordEncoder`.

Também existe uma validação para impedir que um usuário autenticado atualize uma tarefa pertencente a outro usuário.

## API

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/users/` | Cadastro de usuário |
| POST | `/tasks/` | Criação de tarefa |
| GET | `/tasks/lista` | Lista as tarefas do usuário autenticado |
| PUT | `/tasks/{id}` | Atualiza uma tarefa |

### Cadastro de usuário

```http
POST /users/
```

Exemplo:

```json
{
  "username": "lincoln",
  "nome": "Lincoln Silva",
  "password": "123456"
}
```

### Criar tarefa

```http
POST /tasks/
```

Exemplo:

```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Estudar Spring Security e APIs REST",
  "priority": "Alta",
  "startAt": "2026-09-22T18:00",
  "endAt": "2026-09-22T20:00"
}
```

### Listar tarefas

```http
GET /tasks/lista
```

Retorna as tarefas pertencentes ao usuário autenticado.

Quando o usuário não possui tarefas, a API retorna `204 No Content`.

### Atualizar tarefa

```http
PUT /tasks/{id}
```

Exemplo:

```json
{
  "titulo": "Estudar Spring Security",
  "descricao": "Continuar os estudos de autenticação",
  "priority": "Alta",
  "startAt": "2026-09-22T19:00",
  "endAt": "2026-09-22T21:00"
}
```

## Validações

A API possui validações para os dados recebidos.

Por exemplo, o título da tarefa possui limite máximo de 50 caracteres.

## CORS

A API possui configuração de CORS para permitir a comunicação com o frontend.

A configuração permite o acesso tanto do ambiente de desenvolvimento quanto do frontend publicado em produção.

## Banco de dados

O projeto utiliza MySQL para persistência dos dados.

As principais entidades são:

- Usuário
- Tarefa

Cada tarefa possui uma referência ao usuário responsável.

## Estrutura do projeto

```text
src/
└── main/
    ├── java/
    │   └── pt/
    │       └── lincolnsilva/
    │           └── todolist/
    │               ├── controller/
    │               ├── filter/
    │               ├── model/
    │               ├── repository/
    │               ├── security/
    │               └── util/
    │
    └── resources/
        └── application.properties
```

## Deploy

Backend publicado utilizando Render:

https://curso-rocketseat.onrender.com

## Frontend

O projeto possui também um frontend desenvolvido em React.

Frontend publicado utilizando Render:

https://frontend-curso-rocketseat.onrender.com

## Como executar localmente

### 1. Clonar o projeto

```bash
git clone URL_DO_REPOSITORIO
```

### 2. Entrar na pasta do projeto

```bash
cd todolist
```

### 3. Configurar o banco de dados

É necessário possuir um servidor MySQL instalado e configurado.

Configure as informações do banco no arquivo:

```text
src/main/resources/application.properties
```

### 4. Executar a aplicação

Utilizando Maven:

```bash
./mvnw spring-boot:run
```

Ou execute a aplicação através da IDE.

A API ficará disponível em:

```text
http://localhost:8080
```

## Objetivo

Projeto desenvolvido para praticar conceitos de desenvolvimento backend utilizando Java e Spring Boot, incluindo:

- APIs REST
- Spring Security
- Autenticação
- Criptografia de senhas
- Spring Data JPA
- Hibernate
- MySQL
- Validações
- CORS
- Integração com frontend
- Git e GitHub
- Deploy
