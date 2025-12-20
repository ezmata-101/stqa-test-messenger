# Messenger (For Test Purposes)
This is meant to be a test project for a simple messenger service built with Spring Boot.
Note: There might be some discrepancies between the code and this documentation. Refer to the code for the most accurate details.
Please report any issues or inconsistencies.
> Controllers and DTOs should give a better idea of the API structure.


## Project Structure / How the APIs should act

### High-level flow

1. **Auth**

    * `POST /auth/signup` creates a user (in memory).
    * `POST /auth/login` validates credentials and returns a **JWT token**.
    * Protected APIs require:
      ```
      Authorization: Bearer <JWT_TOKEN>
      ```
    * `GET /auth/test` is a quick way to confirm the token works.

2. **Users**
    * `GET /users/` and user lookup endpoints are protected.
    * `PATCH /users/{id}` can only update the **currently authenticated** user (self-update rule).
    * Blocking/unblocking (`/users/block/{id}`) affects messaging behavior (especially in DIRECT conversations).
      * A blocked/blocker cannot send messages to each other in DIRECT chats.
      * A user can still receive messages from a blocked user in GROUP chats.
      * But, a blocked/blocker should not be able to add each other to new conversations.

3. **Conversations**

    * Conversations are either:
        * `DIRECT` (1-to-1)
          * There should be only one DIRECT conversation between two users.
        * `GROUP` (multiple members)
          * Only the group creator should be able to add/remove members.
    * `POST /conversations/create`
        * For `DIRECT`: you provide the other member(s) in `memberIds`.
        * For `GROUP`: you provide other members, and the service ensures the **current user** is included.
    * You must be a **member** to:
        * view a conversation (`GET /conversations/{conversationId}`)
        * view/send messages

4. **Messages**
    * Messages belong to a specific `conversationId`.
    * You must be a **member** of the conversation to `GET` or `SEND`.
    * DIRECT chat rules regarding block:
        * If receiver blocked you → you can’t send.
        * If you blocked receiver → you can’t send.

### Data storage
* The project stores users/conversations/messages **in memory** (not a DB).
* Restarting the server will clear everything.
* `GET /reset` clears in-memory data manually.

---

## How to Run the Project
### 1. **IntelliJ IDEA**
* Open project in IntelliJ IDEA
* Run `MessengerApplication` main class
* Ensure you have the necessary dependencies (Spring Boot, Web, Security, JWT, etc.)
* Make sure Java 17+ is installed
* Run the application

### 2. **Using Gradle CLI**
* Navigate to project root directory
* Run the application using Gradle:
```bash
    ./gradlew build
    ./gradlew bootRun
```

### **Confirm it is running**

* Server should start at:
    * `http://localhost:8080`
* Quick check:

```bash
curl -X GET http://localhost:8080/health
```

### Notes

* If port `8080` is busy, change it in `application.properties` / `application.yml`:

    * `server.port=8081`

---





## API Documentation

### Base URL

* `http://localhost:8080`

### Authentication

* Uses **JWT**.
* After login, send token in every protected request:

**Header**

```
Authorization: Bearer <JWT_TOKEN>
```
---

### Common Response Wrapper (Generic Response)

Some endpoints respond with:

```json
{
  "message": "string",
  "data": "any"
}
```

---

## Health & Utilities
### 1) Health Check

**GET** `/health`

**Response (200)**

```json
{
  "message": "Service is up and running",
  "data": null
}
```

**curl**

```bash
curl -X GET http://localhost:8080/health
```

---

### 2) Reset In-Memory Data

**GET** `/reset`

Resets users, conversations, and messages stored in memory.

**Response (200)**

```text
Reset successful
```

**curl**

```bash
curl -X GET http://localhost:8080/reset
```

---

## Auth APIs

### 1) Signup

**POST** `/auth/signup`

**Request Body**

```json
{
  "username": "alice",
  "email": "alice@test.com",
  "password": "1234"
}
```

**Response (201)**

```json
{
  "username": "alice",
  "userId": 101
}
```

**Errors**

* **400** `Username already taken`

**curl**

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@test.com","password":"1234"}'
```

---

### 2) Login

**POST** `/auth/login`

**Request Body**

```json
{
  "username": "alice",
  "password": "1234"
}
```

**Response (200)**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9....",
  "userId": 101
}
```

**Errors**

* **401** `Invalid password`
* **401** `User not found`

**curl**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"1234"}'
```

---

### 3) Logout

**POST** `/auth/logout?username={username}`

**Response (200)**

```text
User logged out successfully
```

**curl**

```bash
curl -X POST "http://localhost:8080/auth/logout?username=alice"
```

---

### 4) Auth Test

**GET** `/auth/test`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```text
Hello, alice! This is a protected API.
```

**Errors**

* **403** `Forbidden` (if no authentication)

**curl**

```bash
curl -X GET http://localhost:8080/auth/test \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## User APIs

### 1) Get All Users (Authorization Required)

**GET** `/users/`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```json
[
  { "userId": 101, "username": "alice", "email": null, "password": null },
  { "userId": 102, "username": "bob", "email": null, "password": null }
]
```

**Errors**

* **401** `You must be logged in to view users`
* **401** `Unauthorized`

**curl**

```bash
curl -X GET http://localhost:8080/users/ \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

### 2) Get User by ID (Authorization Required)

**GET** `/users/{id}`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```json
{ "userId": 101, "username": "alice", "email": "alice@test.com"}
```

**Errors**

* **404** `User not found`
* **401** Unauthorized / missing auth

**curl**

```bash
curl -X GET http://localhost:8080/users/101 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

### 3) Get User by Username (Authorization Required)

**GET** `/users/username/{username}`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```json
{ "userId": 101, "username": "alice", "email": "alice@test.com"}
```

**Errors**

* **404** `User not found`
* **401** Unauthorized / missing auth

**curl**

```bash
curl -X GET http://localhost:8080/users/username/alice \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

### 4) Update User (Authorization Required, only self)

**PATCH** `/users/{id}`

Rules:

* You **cannot** update `id` (if body includes `id`, request fails).
* You can only update your **own** user (token username must match the user being updated).

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body** (any of these fields)

```json
{
  "username": "alice_new",
  "email": "alice_new@test.com",
  "password": "newpass"
}
```

**Response (200)**

```json
{
  "message": "User updated successfully",
  "data": {
    "userId": 101,
    "username": "alice_new",
    "email": "alice_new@test.com",
    "password": "newpass"
  }
}
```

**Errors**

* **400** `User ID cannot be updated`
* **400** `User not found`
* **403** `You can only update your own user details`
* **401** missing/invalid auth

**curl**

```bash
curl -X PATCH http://localhost:8080/users/101 \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"email":"alice_new@test.com"}'
```

---

### 5) Block User (Authorization Required)

**PATCH** `/users/block/{id}`

Blocks user with id `{id}`.

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```json
{
  "message": "User blocked successfully",
  "data": null
}
```

**Errors**

* **400** `User not found`
* **400** `User to be blocked not found`
* **400** `Failed to block user`
* **401** missing/invalid auth

**curl**

```bash
curl -X PATCH http://localhost:8080/users/block/102 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

### 6) Unblock User (Authorization Required)

**DELETE** `/users/block/{id}`

Unblocks user with id `{id}`.

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```json
{
  "message": "User unblocked successfully",
  "data": null
}
```

**Errors**

* **400** `User not found`
* **400** `User to be unblocked not found`
* **400** `Failed to unblock user`
* **401** missing/invalid auth

**curl**

```bash
curl -X DELETE http://localhost:8080/users/block/102 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

## Conversation APIs

### 1) Get My Conversations (Authorization Required)

**GET** `/conversations/get`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```json
[
  { "conversationId": 201, "type": "DIRECT", "name": null },
  { "conversationId": 202, "type": "GROUP", "name": "CSE Group" }
]
```

**Errors**

* **400** `User not found`

**curl**

```bash
curl -X GET http://localhost:8080/conversations/get \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

### 2) Get Conversation By ID (Authorization Required, must be member)

**GET** `/conversations/{conversationId}`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```json
{ "conversationId": 201, "type": "DIRECT", "name": null }
```

**Errors**

* **400** `User not found`
* **400** `Conversation not found`
* **400** `User is not a member of this conversation`

**curl**

```bash
curl -X GET http://localhost:8080/conversations/201 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

### 3) Create Conversation (DIRECT or GROUP)

**POST** `/conversations/create`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

#### A) Create DIRECT conversation

**Request Body**

```json
{
  "type": "DIRECT",
  "name": null,
  "memberIds": [102]
}
```

**Response (200)**

```json
{ "conversationId": 201, "type": "DIRECT", "name": null }
```

#### B) Create GROUP conversation

**Request Body**

```json
{
  "type": "GROUP",
  "name": "CSE Group",
  "memberIds": [102, 103]
}
```

> The service also ensures **current user** is included in group members automatically.

**Response (200)**

```json
{ "conversationId": 202, "type": "GROUP", "name": "CSE Group" }
```

**Errors**

* **400** `User not found`
* **400** `Failed to create direct conversation`
* **400** `Failed to create group conversation`

**curl**

```bash
curl -X POST http://localhost:8080/conversations/create \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"type":"GROUP","name":"CSE Group","memberIds":[102,103]}'
```

---

### 4) Add Members (Authorization Required, must be member)

**POST** `/conversations/addMember`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body**

```json
{
  "conversationId": 202,
  "members": [104, 105]
}
```

**Response (200)**

```json
{
  "message": "Members added successfully",
  "conversationId": 202,
  "memberIds": [101,102,103,104,105]
}
```

**Errors**

* **400** `User is not a member of this conversation`
* **400** `Conversation not found`
* **400** `Failed to add member to conversation`

**curl**

```bash
curl -X POST http://localhost:8080/conversations/addMember \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"conversationId":202,"members":[104]}'
```

---

### 5) Remove Members (Authorization Required, must be member)

**POST** `/conversations/removeMember`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body**

```json
{
  "conversationId": 202,
  "members": [104]
}
```

**Response (200)**

```json
{
  "message": "Member removed successfully",
  "conversationId": 202,
  "memberIds": [101,102,103,105]
}
```

**Errors**

* **400** `User is not a member of this conversation`
* **400** `Conversation not found`
* **400** `Failed to remove member from conversation`

**curl**

```bash
curl -X POST http://localhost:8080/conversations/removeMember \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"conversationId":202,"members":[104]}'
```

---

## Message APIs

### 1) Get Messages of a Conversation (Authorization Required, must be member)

**GET** `/messages/{conversationId}/get?page=0&size=20`

> `page` and `size` exist, but current service returns the full list from repository (no pagination logic yet).

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200)**

```json
{
  "message": "Messages retrieved successfully",
  "data": [
    { "id": 1001, "conversationId": 201, "senderId": 101, "content": "hi", "timestamp": 1730000000000 }
  ]
}
```

**Errors (400)**

```json
{
  "message": "Cannot access messages for this conversation: User is not a member of this conversation",
  "data": null
}
```

**curl**

```bash
curl -X GET "http://localhost:8080/messages/201/get?page=0&size=20" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

---

### 2) Send Message (Authorization Required, must be member)

**POST** `/messages/{conversationId}/send`

**Headers**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: text/plain
```

**Request Body**

```text
Hello there
```

**Response (200)**

```json
{
  "message": "Message sent successfully",
  "data": [
    { "id": 1001, "conversationId": 201, "senderId": 101, "content": "Hello there", "timestamp": 1730000000000 }
  ]
}
```

**Important behavior (DIRECT chats)**

* If the receiver has blocked you → error: `You must unblock the user to send messages`
* If you have blocked the receiver → error: `You cannot send message to this conversation`

**Error (400)**

```json
{
  "message": "You cannot send message to this conversation",
  "data": null
}
```

**curl**

```bash
curl -X POST http://localhost:8080/messages/201/send \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: text/plain" \
  -d "Hello there"
```

---