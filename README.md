# Akka Http QuickStart

Fully featured, easy to understand akka http seed project. This a perfect seed for
constructing a micro service. This seed has a fully functional `Slick` instance using MySql and H2 for data
persistence.

#### Dependencies
- [Java -v0.8](https://java.com/en/download/) 
- [Scala -v 2.12.2](https://www.scala-lang.org/download/)
- [Mysql](https://www.mysql.com/)
- [H2](https://www.h2database.com/html/main.html)

#### Database

Database is powered by `Slick` driver running `MySql` for production and `H2` for testing.

If you would like to change either of these that is fine you will just need to change the drivers in `application.conf`

```
mysql {
    profile = "slick.jdbc.MySQLProfile$"
    db {
       url = "jdbc:mysql://localhost:3306/todo?user=root"
       driver = com.mysql.jdbc.Driver
       maxThreads = 5
    }
}

h2 {
  profile = "slick.jdbc.H2Profile$"
  db {
    url = "jdbc:h2:mem:todo;DB_CLOSE_DELAY=-1"
      	driver=org.h2.Driver
      	connectionPool = disabled
  }
}
```
 
 Then update the pointers in `/main/scala/db/DbConfiguration.scala` and `/test/scala/db/TestDbConfiguration.scala`

#### Testing

Since this is a simple CRUD api and nothing besides the routes are using the repositories I am considering
testing just the routes sufficient as unit tests. Aside from the routes, the custom directives are tested as
well.

```
sbt test
```

#### Run

```
sbt run 
```

#### Using the Api

---

**POST** /users

request body
```json
{
	"email": "tester@chester.com",
	"firstName": "tester",
	"lastName": "chester"
}
```

201 
```json
{
    "id": 4,
    "email": "tester@chester.com",
    "firstName": "tester",
    "lastName": "chester"
}
```

**PUT** /users/update?userId=[userId]

request body

```json
{
	
	"email": "tester@chester.com",
	"firstName": "tester",
	"lastName": "chesterDUDE"
}
```

200
```json
true
```

---

**POST** /todos?userId=[userId]

request body
```json
{
  	"title": "Go to the store",
  	"description": "get tomatoes"
}
```

201
```json
{
    "id": 3,
    "userId": 4,
    "title": "Go to the store",
    "description": "get tomatoes",
    "done": false
}
```

**GET** /todos?userId=[userId]

200
```json
[
    {
        "id": 3,
        "userId": 4,
        "title": "Go to the store",
        "description": "get tomatoes",
        "done": false
    }
]
```

**PUT** /todos/update?userId=[userId]&id=[todo id]

request body
```json
{
  "title": "Go to the MOON",
  "description": "get tomatoes",
  "done": true
}
```

200
```json
true
```

**GET** /todos?userId=[userId]&id=[id]

200
```json
{
    "id": 3,
    "userId": 4,
    "title": "Go to the MOON",
    "description": "get tomatoes",
    "done": true
}
```

**GET** /todos/pending?userId=[userId]

200
```json
[
    {
        "id": "2",
        "title": "go to the park",
        "description": "hide and seek game",
        "done": false
    },
    {
        "id": "036165de-5013-4846-ba85-cc5294fa1870",
        "title": "go to the park",
        "description": "it is important to smell the roses.",
        "done": false
    }
]
```

**GET** /todos/complete?userId=[userId]

```json
[
    {
        "id": 3,
        "userId": 4,
        "title": "Go to the MOON",
        "description": "get tomatoes",
        "done": true
    }
]
```

**DELETE** /todos/delete?userId=[userId]&id=[id]

200
```json
true
```
