# Getting Started

## Install back-end

### Requirements

* Jdk 11
* Docker

### Starting

* Build the project (in root of sources) with help command:

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

In docker will be started application (available by address http://localhost:8080/) and 
cassandra db (available by address localhost:9042).

### Examples of queries for GraphQL:

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

## Install front-end

### Requirements

* Node js

### Starting

* Move to front-end folder: 

```text
cd ./front-end
```

* Install angular cli:
```text
npm install --save-dev @angular-devkit/build-angular
```

* Build the application:
```text
ng build
```

if you faced with exception in graphql-tag library then try to downgrade the library:

```text
npm install graphql-tag@2.10.4
```

* Start the application:
```text
ng serve
```

* Open in browser [http://localhost:4200/](http://localhost:4200/)
