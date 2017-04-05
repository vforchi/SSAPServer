# SSAPServer
An implementation of the [Simple Spectral Access Protocol of the VO](http://www.ivoa.net/documents/SSA/20120210/REC-SSA-1.1-20120210.pdf)
The server is a Spring Boot application that implements the API defined in the standard.

The server can either redirect the requests to a TAP service or go directly to the DB.

## Using TAP
In this mode the server translates the incoming requests into ADQL and sends them to a TAP service.
The tool assumes a specific schema is available on the TAP server. Unfortunately it is not possible to query directly ObsCore, 
because the UCDs are different and some columns are missing. Therefore we defined a view that can be build on top of
ObsCore to enable SSA access. You can find the definition of this view for SQLServer under TBD

### Configuration 
These are all the configuration options specific to the TAP access, with their default values
```
ssap.use.tap = true // mandatory
ssap.tap.url = // the URL of the TAP server, no default
ssap.tap.timeout = 10 // timeout in seconds
```

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
in the project root directory, a standalone jar file will be created under `build/libs/SSAPServer.jar`. You can run it like this:
```
java -jar SSAPServer.jar --ssap.tap.url=http://<host>:<port>/yourtap
```

## More configuration options
server.port = 9000 // the port where the server runs. This property comes from Spring Boot

