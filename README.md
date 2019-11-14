![](https://github.com/vforchi/SSAPServer/workflows/Run%20tests/badge.svg)

# SSAPServer
An implementation of the [Simple Spectral Access Protocol](http://www.ivoa.net/documents/SSA/20120210/REC-SSA-1.1-20120210.pdf) and of the [Simple Image Access](http://www.ivoa.net/documents/SIA/20151223/REC-SIA-2.0-20151223.html) of the IVOA.
The server is a Spring Boot application that implements the APIs defined in the standard.
SSA and SIA can be enabled independently.

## SSA
SSA requests can either be redirected to a TAP service or go directly to the DB.

### Using TAP
In this mode the server translates the incoming SSA requests into ADQL and sends them to a TAP service.
The tool assumes a specific schema is available on the TAP server. Unfortunately it is not possible to query directly ObsCore, 
because the UCDs are different and some columns are missing. Therefore we defined a view that can be built on top of
ObsCore to enable SSA access. You can find an example definition of this view for SQLServer [here](https://github.com/vforchi/SSAPServer/blob/master/sql/create_view_ssa.sql).

### Customization
SSA leaves some freedom in the implementation of specific parameters, and some of them can be specific to the data or to the
model that the server is exposing. It is therefore possible to customize how a given parameter is converted in a TAP query.
Every parameter defined in the SSA standard is handled by a class that implements the interface `ParameterHandler`.
If, for example, you want to override the default TIME implementation, it is sufficient to declare a class named `MyTimeHandler`, that implements ParameterHandler. You can find an example in the tests.

### Direct DB queries
Not implemented.

## SIA
SIA requests are converted to ObsTAP queries.

## Run the server
Execute:
```
./gradlew bootRun -Pargs="--ssap.tap.url=http://<host>:<port>/yourtap --sia.tap.url=http://<host>:<port>/yourtap"
```
in the project root directory: the server will start at `http://localhost:9000`
## Build
Execute:
```
./gradlew build
```
in the project root directory, a standalone jar file will be created under `build/libs/SSAPServer-<version>.jar`. You can run it like this:
```
java -jar SSAPServer-<version>.jar --ssap.tap.url=http://<host>:<port>/yourtap --sia.tap.url=http://<host>:<port>/yourtap
```
Note: the first build might take a few minutes, because the script is going to download gradle and all dependencies. The 
next builds will be faster

## Configuration
The available configuration options are [here](src/main/resources/application.properties), together with their default values.
They can be set in any way allowed by Spring Boot (see [here](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html))
