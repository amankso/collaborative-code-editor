# 🚀 Collaborative Code Editor

> Real-time multi-user editing for HTML, CSS, and JavaScript — built on an event-driven microservices architecture.

![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![Socket.io](https://img.shields.io/badge/Socket.io-010101?style=for-the-badge&logo=socket.io&logoColor=white)

---

## ✨ Features

| | Feature | Description |
|---|---|---|
| 🧑‍💻 | **Real-time collaboration** | Like Google Docs — simultaneous multi-user editing |
| ⚡ | **Live code sync** | Instant updates across all connected clients |
| 💾 | **Smart autosave** | Every 2s, only triggers on actual changes |
| 🧠 | **Default project init** | HTML / CSS / JS boilerplate on first load |
| 🖥️ | **Live preview** | Iframe-based code emulator |
| 🧩 | **Microservices backend** | Separate collab and project services |

---

## 🏗️ Architecture

```
Frontend (React + Monaco Editor)
        │
        │  Socket.IO
        ▼
Collab Service (WebSocket Server)
        │
        │  Kafka — project-commands
        ▼
Project Service (Spring Boot + MongoDB)
        │
        │  Kafka — project-events
        ▼
Collab Service
        │
        │  Socket.IO
        ▼
Frontend (All clients updated)
```

---

## ⚙️ Tech Stack

### Frontend
- React.js
- Monaco Editor
- Socket.IO Client

### Backend
- Spring Boot (Microservices)
- Socket.IO (Netty-based server)
- Apache Kafka
- MongoDB
- Redis *(optional)*

### DevOps
- Docker & Docker Compose

---

## 🔄 Workflow

### Project Load — `project_get`
1. User opens editor
2. Request sent via WebSocket
3. Kafka → Project Service
4. Fetch or create default project
5. Response back to frontend

### Live Collaboration — `project_write`
1. User types
2. Event sent via WebSocket
3. Kafka broadcast
4. Other users receive instantly

### Autosave — `project_save`
1. Triggers every 2 seconds
2. Runs only if changes detected
3. Saved to MongoDB
4. Synced back to all clients

---

## 🧠 Key Design Decisions

- Event-driven architecture using Kafka
- Separation of real-time and persistence concerns
- Loop prevention using `source` field filtering
- Optimized autosave to avoid redundant writes

---

## ⚠️ Challenges Solved

| Challenge | Solution |
|---|---|
| Infinite save loop | Fixed via `source` field filtering |
| UI refresh issue | Fixed via state comparison |
| Initial load race condition | Fixed using load flag |

---

## 🧪 How to Run

### 1. Start Infrastructure

```bash
docker-compose up -d
```

> Starts: Kafka, Zookeeper, MongoDB, Redis

### 2. Start Backend

```bash
# Collab Service
cd collab-service
mvn spring-boot:run

# Project Service
cd project-service
mvn spring-boot:run
```

### 3. Start Frontend

```bash
cd frontend
npm install
npm start
```

---

## 📡 Kafka Topics

| Direction | Topic |
|---|---|
| Incoming | `project-commands` |
| Outgoing | `project-events` |

---

## 🚀 Future Improvements

- [ ] CRDT / Operational Transformation
- [ ] Authentication & user sessions
- [ ] Multi-language support
- [ ] File/project management
- [ ] WebRTC optimization

---

## 👨‍💻 Author

**Aman Singh**
- 📧 [devshivaman@gmail.com](mailto:devshivaman@gmail.com)
- 🔗 [linkedin.com/in/aman-kumar-singh-7a489a387](https://www.linkedin.com/in/aman-kumar-singh-7a489a387/)

---

## ⭐ Support

If you find this project useful, please give it a ⭐ and consider contributing!
