# mocap-backend

## How to start

> [!INFO]  
> If you are using Windows, you need to use Docker for WSL2.

1. Build the docker image.

    ```bash
    docker-compose build
    ```

2. Create a docker network `mocap` if you haven't already.

    ```bash
    docker network create mocap
    ```

3. Create a copy of `.env.production` and rename it as `.env.production.local`.

4. Fill in all required environment variables.

5. Start the container.

    ```bash
    docker-compose up
    ```


## Recommend IntelliJ IDEA Plugins

-   GraphGL
-   Lombok
