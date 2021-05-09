# Getting Started

## Requirements

* Jdk 11
* Docker

## Starting

* Build the project with help command:

```bash
gradle build -x test
```
or with help gradle wrapper:

```bash
gradlew build -x test
```

* Create docker image:

```bash
docker build -t vor/homework .
```

* Run the application and cassandra db in docker:

```bash
docker-compose up
```

## Examples for GraphQL:

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