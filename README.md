# Stylechecker Backend
## CO600 Group Project (jb695, jtk6, cbag2, ms693)

This is the Java backend of the code style checker for the 1st year module CO520

This project uses [Maven](https://maven.apache.org/) to manage its dependencies.

To build the project from source:

1. Clone the Git repository
2. With Maven installed on your machine and available on your PATH run: `mvn clean package` from the project root directory
3. This will put a `stylechecker-backend-1.0-SNAPSHOT.jar` in the `target` folder

To run the backend software:

`java -jar stylechecker-backend-1.0-SNAPSHOT.jar server src/main/resources/stylechecker-config.yml`

The `stylechecker-config.yml` is found in the source tree of the Git repository but can be copied
anywhere for deployment.

You should check the configuration file is suitable for your environment before deploying.

Once running the service will start a HTTP server listening on the port (8888) and path (/stylechecker) specified in the configuration file.