# Stylechecker Backend
## CO600 Group Project (jb695, jtk6, cbag2, ms693)

This is the Java backend of the code style checker for the 1st year module CO520

This project uses [Maven](https://maven.apache.org/) to manage its dependencies.

To build the project from source:

1. Clone the Git repository
2. With Maven installed on your machine and available on your PATH run: `mvn clean package` from the project root directory
3. This will put a `stylechecker-backend-1.0-SNAPSHOT.jar` in the `target` folder

To run the backend software:

`java -jar stylechecker-backend-1.0-SNAPSHOT.jar server`

Once running the service will start a HTTP server listening on the port 8888 and path /stylechecker

Configuration can be overridden using system properties. 

For e.g. `java -Ddw.server.connector.port=9000 -jar stylechecker-backend-1.0-SNAPSHOT.jar server`