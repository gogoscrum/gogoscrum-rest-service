# gogoscrum - Lightweight agile project management tool

[English](README.md) | [简体中文](README.zh-CN.md)

gogoscrum is a lightweight agile project management tool designed for efficient team collaboration. Manage tasks effortlessly on an intuitive sprint board, drag and drop tasks to update their status. Track and report bugs, monitor progress with a sprint burndown chart, and share files and documents seamlessly — everything an agile team needs in one place.

## Features

- **Sprint Board**: Align project teams with a visual task board, allowing members to assign tasks and update statuses via drag-and-drop.
- **Issue Tracking**: Efficiently manage user stories, tasks, and bugs using tags, attachments, links, and comments.
- **Product Backlog**: Prioritize and organize issues by importance and scope to focus on high-impact tasks first.
- **File Sharing**: Create folders and upload files for seamless team collaboration.
- **Documents**: Centralize critical information for easy access and reference by team members anytime.
- **Charts**: Visualize progress with Burndown charts (by story points), Velocity charts, and Cumulative Flow diagrams for each sprint.

## Screenshots

Classic board

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/white-board-classic.png" width="680">

Sticky-notes board

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/white-board-sticky-notes.png" width="680">

Dark mode board

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/dark-board.png" width="680">

Issue list

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/issue-list.png" width="680">

File list

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/file-list.png" width="680">

Burn-down chart

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/burn-down-chart.png" width="680">

Issue details

<img src="https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/materials/issue-edit.png" width="480">

## Technologies

**gogoscrum backend service built with**:

- Java 21
- Spring Boot 3.4
- Spring Security
- JPA + Hibernate
- MySQL 8.0

**gogoscrum frontend built with**:

- Vue 3.5
- Element Plus
- Echarts

## Demo site

- [https://demo.gogoscrum.com/]
- login user: `demo`
- password: `demo1234`

## Quick start

### Run gogoscrum with Docker

- Download [https://gogoscrum.oss-cn-hangzhou.aliyuncs.com/downloads/gogoscrum-docker.zip]
- Unzip into folder `gogoscrum-docker`
- Run command in the folder `docker compose up --build` 
- Access [http://localhost] to start

### Run gogoscrum from Source Code

#### Start Backend Service

- Clone `gsf-core-service` repository and install `shimi-gsf-core.jar` to your local Maven repository (refer to the README in the `gsf-core-service` project for detailed steps)
- Clone `gogoscrum-rest-service` repository and import it into your IDE
- Copy `dist.application-dev.properties` file and rename it to `application-dev.properties`
- Modify the database connection and other configuration details in `application-dev.properties`
  - Manually create the database in advance; all tables will be created automatically when the service starts
  - Ensure that Redis is installed and running on your local machine
  - Create an `lfs` directory for storing uploaded files, and update the `file.local.dir.base` setting to point to this directory
- Run `GogoscrumApplication.java` to start the backend service, and check the logs to confirm the service starts properly
- Visit [http://localhost:8080/health] to verify the service is running correctly

#### Start Frontend Application

- Clone the `gogoscrum-web-client` repository to your local directory and import it into your IDE
- Create a `.env.local` file in the root of the project and add the following content:
```
NODE_ENV=development
VITE_FILE_PATH = 'Path to the lfs directory configured in the backend Java service'
```
- Run `npm i` to install dependencies
- Run `npm run dev` to start the frontend project
- Visit [http://localhost:8081] to start using gogoscrum
- On your first visit, please register a new user account. You can log in after registration

## Report bugs

Join “gogoscrum community version” project to submit bugs: [https://www.gogoscrum.com/invitations/n6DDaxwqMndM]

## Community

Join gogoscrum developer group on Discord: [https://discord.gg/FBdf43BZ]

## License

This project is licensed under the **Apache License 2.0** with the following restriction.

You are free to:

- Use, modify, and distribute the software for **non-commercial** purposes (need to keep the logo and copyright info);
- Study the source code and contribute to the project.

However, you **may not sell** or use this software in **commercial** services or products without prior written consent from us.

If you want to use this software in a commercial context (e.g., as part of a SaaS offering, enterprise system, or paid solution), please contact us to obtain a **commercial license**.