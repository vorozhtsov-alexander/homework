# Getting Started

##Requirements

* Jdk 11
* Docker

## Starting

* Build the project with help command:

```bash
gradle build
```
or with help gradle wrapper:

```bash
gradlew build
```

* Create docker image:

```bash
docker build -t vor/homework .
```

* Run the application and cassandra db in docker:

```bash
docker-compose up
```

## How to use

Open [http://localhost:8080/graphiql](http://localhost:8080/graphiql) in browser to make requests

Examples:
* Create user:
```text
mutation{
  createUser(details:{email : "email", firstName : "first name", lastName : "last name", role : "ADMIN"}) {
    id
  }
}
```
* List of users:
```text
{
  getAllUsers {
    id,
    email,
    firstName,
    lastName,
    role
  }
}
```