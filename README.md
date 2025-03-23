# Backend сервиса для планирования событий

Приложение состоит из двух самостоятельных модулей:

- **Сервис сбора статистики** позволяет регистрировать обращения к эндпойнтам и создавать отчет из собранной информации.
- **Сервис планирования событий** дает пользователям возможность регистрировать общественные мероприятия, 
приглашать на них людей и самим принимать участие, а также оставлять отзывы после.

## Использованные технологии

- Java 21
- SpringBoot
- Maven
- Lombok
- Docker
- PostgresSQL
- RestTemplate
- Hibernate

## Эндпойнты сервиса сбора статистики

- сохранение события *POST /hit*
```
{
    "app" : String
    "uri" : String
    "ip" : String
}
```
- получение статистики *GET /stats?String*
```
    startTime, endTime, urisList, unique
```

## Эндпойнты сервиса планирования событий

### Работа с пользователями

- добавление пользователя *POST /admin/users*
```
{
    "name" : String
    "email" : String
}
```
- получение списка пользователей *GET /admin/users?String*
```
    ids, from, size
```
- удаление пользователя *DELETE /admin/users/{userId}*

### Категории событий

- получение списка имеющихся категорий *GET /categories?String*
```
 from, size
```
- получение название категории по id *GET /categories/{categoryId}*
- добавление новой категории *POST /admin/categories*
```
{
    "name" : String
}
```
- изменение названия категории *PATCH /admin/categories/{categoryId}*
- удаление категории *DELETE /admin/categories/{categoryId}*

### События

- получение списка с краткой информацией о событиях *GET /events?String*
- - получение списка с полной информацией о событиях *GET /admin/events?String*
```
text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
```
- получение информации о событии по id *GET /event/{eventId}*
- получение информации о событиях, созданных пользователем *GET /users/{userId}/events?String*
```
from, size
```
- добавление нового события *POST /users/{userId}/events*
```
{
    "annotation" : String
    "category" : Long
    "description" : String
    "eventDate" : LocalDateTime
    "location" : Location
    "paid" : Boolean
    "participantLimit" : Long
    "requestModeration" : Boolean
    "title" : String
}
```
- получение полной информации о событии *GET /users/{userId}/events/{eventId}*
- обновление пользователем информации о событии *PATCH /users/{userId}/events/{eventId}*
- обновление администратором информации о событии *PATCH /admin/events/{eventId}*
```
{
    "annotation" : String
    "category" : Long
    "description" : String
    "eventDate" : LocalDateTime
    "location" : Location
    "paid" : Boolean
    "participantLimit" : Long
    "requestModeration" : Boolean
    "stateAction" : StateAction
    "title" : String
}
```
- получение списка запросов на участие в событии *GET /users/{userId}/events/{eventId}/requests*
- подтверждение/отклонение запросов на участие создателем события *PATCH /users/{userId}/events/{eventId}/requests*
```
{
    "requestIds" : List<Long>
    "status" : RequestState
}
```
- оценка события после участия в нем *POST /users/{userId}/events/{eventId}/rate*
```
{
    "rating" : int
}
```
- создание запроса на участие в событии *POST /users/{userId}/requests?String*
```
eventId
```
- получение списка запросов пользователя на участие в событиях *GET /users/{userId}/requests*
- удаление собственной заявки на участие *PATCH /users/{userId}/requests/{requestId}/cancel*

### Подборки событий

- создание новой подборки событий *POST /admin/compilations*
```
{
    "events" : List<Long>
    "pinned" : Boolean
    "title" : String
}
```
- обновление подборки событий *PATCH /admin/compilations/{compilationId}*
- удаление подборки событйи *DELETE /admin/compilations/{compilationId}*
- получение всех подборок *GET /compilations?String*
```
pinned, from, size
```
- получение информации по одной подборке *GET /compilations/{compilationId}*