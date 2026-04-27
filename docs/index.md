---
title: Stagefinder Documentation
---

# Official Documentation — Stagefinder

*By Martin Ließ*

---

Stagefinder is a Spring Boot + React web application that proxies artist search and setlist data from [setlist.fm](https://www.setlist.fm), lets authenticated users save artists as favourites with personal notes, and surfaces a personalised feed of recent shows on the landing page.

Source code: [github.com/Just-Martin-Really/stagefinder](https://github.com/Just-Martin-Really/stagefinder)

## Documentation sections

| Section | Contents |
|---------|----------|
| [Setup](setup.md) | Prerequisites, environment variables, and running the application with Docker Compose |
| [Architecture](architecture.md) | System components, request flow, and data model |
| [API](api.md) | All REST endpoints, request/response shapes, and authentication requirements |
| [Frontend](frontend.md) | React routes, component structure, and build configuration |
| [Security](security.md) | Session handling, CSRF protection, and password storage |
| [Testing](testing.md) | Test layout, coverage by layer, and how to run the suite |
| [Docker](docker.md) | Image configuration, Compose setup, and build targets |
| [Environment](environment.md) | Full reference for every environment variable |
