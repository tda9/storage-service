# storage-service
Description: Provide files management support integrate with iam-service

## Table of Contents
- [About the Project](#about-the-project)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [API Endpoints](#api-endpoints)
- [Contact](#contact)

## About the Project
- This project focus on managing files storaging
## Features
- CRUD Files(Support search, filter and pagination)
- Provide feature download, upload files and store user activities when interact with files
- Integrate with Redis to store invalid tokens
- Services communication using client-credentials flow
## Technologies Used
- **Backend**: Spring boot
- **Database**: PostgreSQL
- **Others**: Keycloak 26.0.7, Docker, Redis
## Installation
### Prerequisites
- Ensure you have the following installed:
1. JDK 21
2. Intellij
3. Keycloak(latest)
4. Docker
### Steps
1. Clone project: git clone https://github.com/tda9/storage-service.git
2. Extract zip and open with Intellij
3. Open application.yml and change base on your configuration
## Api Endpoints
- See in swagger
## Contact
- Author: Tran Duc Anh
- Email: datran682023@gmail.com
