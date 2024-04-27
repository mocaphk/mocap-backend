# mocap-backend

## What is MOCAP

![MOCAP demo](./.github/assets/demo.gif)

Multipurpose Online Coding Assessment Platform (MOCAP) is a web-based platform that aims to eliminate the need for students to individually configure their coding environments when completing course coding assignments.

To complete course coding assignments, students need to set up a coding environment on their local machines. However, issues might arise when setting up the environment due to discrepancies in libraries, dependencies, operating systems, and hardware. These differences can lead to problems when running assignments in markers' environments, resulting in disputes between students and teachers.

To address this problem, MOCAP provides a solution by hosting a web platform that offers a customizable coding environment using Docker. Docker ensures environment consistency and replicability, thereby eliminating the problems arising from discrepancies in libraries, dependencies, and operating systems.

## What is mocap-backend

mocap-backend is the backend of our MOCAP system. It is written in Spring Boot and GraphQL.

## Getting Started

### Production

> [!INFO]  
> If you are using Windows, you need to use Docker for WSL2.

1. Create a copy of `.env.production` and rename it as `.env.production.local`.

2. Fill in all required environment variables in `.env.production.local`.

3. Build the docker image.

    ```bash
    docker-compose build
    ```

4. Create a docker network `mocap` if you haven't already.

    ```bash
    docker network create mocap
    ```

5. Start the container.

    ```bash
    docker-compose up
    ```

### Development

1. We suggest to use IntelliJ IDEA IDE. Reference `.env.production` to see all required environment variables and fill in the IDE.

2. Run the application using the IDE.

## Contributing

-   Please fork this repository and create a pull request if you want to contribute.

-   Please follow [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) when you commit!

-   Recommend IntelliJ IDEA Plugins: [GraphGL](https://plugins.jetbrains.com/plugin/8097-graphql), [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok)
