# Guia de Execução: Library Application

Este documento descreve os pré-requisitos e os passos necessários para configurar, compilar e executar o sistema de gerenciamento de biblioteca baseado em Quarkus.

## 1. Pré-requisitos

Para executar este sistema, você precisará ter instalado:

- Java JDK 17
- Maven 3.8.1+ (ou o Maven Wrapper `./mvnw` incluso no projeto)
- PostgreSQL (rodando localmente ou via Docker na porta 5432)
- Docker (opcional, para execução via containers)

## 2. Configuração do Banco de Dados

As credenciais e URL estão definidas em `src/main/resources/application.properties`:

- URL: `jdbc:postgresql://localhost:5432/library_db`
- Usuario: `postgres`
- Senha: `123456`

Observação: o Hibernate está configurado para atualizar o esquema automaticamente:
`quarkus.hibernate-orm.database.generation=update`.

## 3. Configuração de Segurança (JWT)

O sistema utiliza JSON Web Tokens (JWT) para autenticação. As chaves devem estar nos caminhos configurados:

- Chave Publica: `src/main/resources/keys/publicKey.pem`
- Chave Privada: `src/main/resources/keys/privateKey.pem`

Para gerar as chaves (exemplo com OpenSSL):

```bash
openssl genrsa -out privateKey.pem 2048
openssl rsa -pubout -in privateKey.pem -out publicKey.pem
```

Coloque os arquivos gerados dentro de `src/main/resources/keys/`.

## 4. Execução em Modo de Desenvolvimento

O modo de desenvolvimento permite "live coding" (alteracoes aplicadas sem reiniciar o servidor).

```bash
./mvnw quarkus:dev
```

A aplicação ficará disponivel em: `http://localhost:8080`.

## 5. Compilação e Execucao (Modo Produção)

Empacotar a aplicação:

```bash
./mvnw package
```

Executar o JAR:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## 6. Execução via Docker (JVM Mode)

Gere o pacote Maven primeiro:

```bash
./mvnw package
```

Construa a imagem Docker:

```bash
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/libraryapp-jvm .
```

Inicie o container:

```bash
docker run -i --rm -p 8080:8080 quarkus/libraryapp-jvm
```

## 7. Informacoes do Sistema

- Público-alvo: Membros (clientes) e Administradores (gestores)
- Rotas públicas: `/auth/login`, `/user/create`, `/`, `/login`, `/register`, `/css/*`, `/js/*`, `/favicon.ico`
- Rotas protegidas: todas as demais exigem token JWT via cookie `jwt`
- Tarefa agendada: verificação de atrasos a cada 1 minuto (multas de usuarios inadimplentes)

