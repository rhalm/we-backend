# Prerequisites

This project requires Java 15 and Maven

# How to run

First you need to get your credentials:

- Go to [Firebase console](https://console.firebase.google.com/) and click on "Create a project"
- Create a new bucket by going to Storage and clicking on "Get started"
- Copy the bucket's path (the part after `gs://`) and add it to `common/src/main/resources/config.properties`
- Go to Project settings / Service accounts to generate a new private key and place the JSON into `common/src/main/resources/config` as `credentials.json`

### Local-dev setup

**Using IntelliJ IDEA** 

After importing the project run each service's Application class

**Using command line**

For each service go to the project's root folder (for Feed, Messaging and Profile go to the \*-core folder) and run `mvn spring-boot:run`

### Production setup

Make sure your Docker is running and you have Docker Compose installed

Create images for backend components:

- Build project by running `mvn clean install`
- Run `jib:dockerBuild` for all services in their folder (for Feed, Messaging and Profile in the \*-core folder)

Create image for frontend:

- Go to the frontend repo and set up as described [here](https://github.com/rhalm/we-frontend)
- After installing the dependencies run `npm run build` and copy the created `dist` folder's content to this repo's `frontend/dist`
- Run `build-docker.sh` in the frontend folder

To start the application run `docker-compose up -d` in the project's root folder
and `docker-compose down` to stop
