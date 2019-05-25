# Akka Http QuickStart

Fully featured, easy to understand akka http seed project. This a perfect seed for
constructing a micro service.

##### Dependencies
- [Java -v0.8](https://java.com/en/download/) 
- [Scala -v 2.12.2](https://www.scala-lang.org/download/)

#### Using the Api

##### POST /todos

request body
```json
{
	"title": "go to the park",
	"description": "it is important to smell the roses."
}
```

201 
```json
{
    "id": "a2ab38f3-84c9-4ee0-9a54-f44d2eeea87d",
    "title": "go to the park",
    "description": "it is important to smell the roses.",
    "done": false
}
```

##### GET /todos

200
```json
[
    {
        "id": "1",
        "title": "Buy eggs",
        "description": "Ran out of eggs, buy a dozen",
        "done": false
    },
    {
        "id": "2",
        "title": "Buy milk",
        "description": "The cat is thirsty",
        "done": true
    },
    {
        "id": "036165de-5013-4846-ba85-cc5294fa1870",
        "title": "go to the park",
        "description": "it is important to smell the roses.",
        "done": true
    }
]
```

##### PUT /todos/[todo id]

request body **all fields are optional**
```json
{
	"title": "go to the park",
	"description": "hide and seek game",
	"done": true
}
```

200
```json
{
    "id": "2",
    "title": "go to the park",
    "description": "hide and seek game",
    "done": true
}
```

##### GET /todos/pending

200
```json
[
    {
        "id": "1",
        "title": "Buy eggs",
        "description": "Ran out of eggs, buy a dozen",
        "done": false
    }
]
```

##### GET /todos/complete

200
```json
[
    {
        "id": "2",
        "title": "go to the park",
        "description": "hide and seek game",
        "done": true
    },
    {
        "id": "036165de-5013-4846-ba85-cc5294fa1870",
        "title": "go to the park",
        "description": "it is important to smell the roses.",
        "done": true
    }
]
```