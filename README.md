# SSAPServer
An implementation of the [Simple Spectral Access Protocol of the VO](http://www.ivoa.net/documents/SSA/20120210/REC-SSA-1.1-20120210.pdf)
The server is a Spring Boot application that implements the API defined in the standard.

The server can either redirect the requests to a TAP service or go directly to the DB.

SSAPServer is at a very early stage of development and currently supports the mandatory parameters `POS`, `SIZE`, `TIME`, `BAND` and `FORMAT`.
In additional the following recommended and optional parameters are supported: `COLLECTION`, `CREATORDID`.

## Using TAP
In this mode the server translates the incoming requests into ADQL and sends them to a TAP service.
The tool assumes a specific schema is available on the TAP server. Unfortunately it is not possible to query directly ObsCore, 
because the UCDs are different and some columns are missing. Therefore we defined a view that can be built on top of
ObsCore to enable SSA access. You can find the definition of this view for SQLServer under TBD

### Customization
SSA leaves some freedom in the implementation of specific parameters, and some of them can be specific to the data or to the
model that the server is exposing. It is therefore possible to customize how a given parameter is converted in a TAP query.
Every parameter defined in the SSA standard is handled by a class that implements the interface `ParameterHandler`. There are 
currently two implementations: `PosHandler` and `TimeHandler`. 
If, for example, you want to override the default TIME implememntation, it is sufficient to declare a class named `MyTimeHandler`,
 that implements ParameterHandler. You can find an example in the tests

## Direct DB queries
Not yet implemented

## Run the server
Execute:
```
./gradlew bootRun -Pargs="--ssap.tap.url=http://<host>:<port>/yourtap"
```
in the project root directory: the server will start at `http://localhost:9000/ssa`
## Build
Execute:
```
./gradlew build
```
in the project root directory, a standalone jar file will be created under `build/libs/SSAPServer-<version>.jar`. You can run it like this:
```
java -jar SSAPServer-<version>.jar --ssap.tap.url=http://<host>:<port>/yourtap
```

## Configuration
The available configuration options are in `src/main/resources/application.properties`, together with their default values.
They can be set in any way allowed by Spring Boot (see [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html))