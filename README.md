# lodtools-triplepages

Simple front-end for RDF triplestore / Linked Open Data PoC's.

## Description

A typical setup consists of two Docker-ized services: the front-end and a triplestore.
Data and configuration is stored on a docker volume
(otherwise the data is lost when the docker containers get terminated / rebooted)

## Available datasets

These sets are not updated, there are no guarantees on availability and correctness.

  * [Crossroad Banks Enterprises](doc/CBE_NACEBEL.md)
  * [Company types](doc/CBE_NACEBEL.md)
  * [NACEbel 2008 codes](doc/CBE_NACEBEL.md)
  * [Fedict's FSB services](doc/FSB.md) 
  * [Demo link store for WCMS](doc/LINK.md)
  * [Fedict's Framework Contracts](doc/PROCUREMENT.md)



## Docker containers

### Front-end

A custom [Dropwizard](http://www.dropwizard.io/) application, provided as a pre-built local image `barthanssens/lod-triplepages`

In this example the image uses an environment variable `DW_CFG` to set the location of the YAML config file, the default Dropwizard port (8080) is mapped to port 80 (HTTP), and the container `dw` is automatically restarted when it exits unexpectedly (e.g. server reboot).

Inside the container, the home folder is used to store persistent data as a docker volume, mapped/mounted to the host file system `/home/opendata/data/pages`

```
docker run --restart=unless-stopped --name dw -p 80:8080 -d -e "DW_CFG=/home/dropwizard/config.yml" 
-v /home/opendata/data/pages:/home/dropwizard barthanssens/lod-triplepages
```

A YAML config file is required to configure the connection to the triple store and logging of HTTP requests and Java exceptions.

```
sparqlPoint: "http://172.17.0.2:7200"
username: myuser
password: verysecret

server:
  requestLog:
    appenders:
    - type: file
      archivedFileCount: 5
      archivedLogFilenamePattern: /home/dropwizard/logs/dw-%d-request.log.gz
      currentLogFilename: /home/dropwizard/logs/dw-request.log

logging:
  level: INFO
  appenders:
    - type: file
      archivedFileCount: 5
      archivedLogFilenamePattern: /home/dropwizard/logs/dw-%d.log.gz
      currentLogFilename: /home/dropwizard/logs/dw.log
```

### RDF Triple Store

This can be a [GraphDB](doc/GRAPHDB.md) triple store, or another store supporting [RDF4j](http://rdf4j.org/).
In this example the default GrapHDB workbench port (7200) is mapped to the host port 8443

```
docker run --restart=unless-stopped --name graphdb7 -p 8443:7200 -d 
-v /home/opendata/data/graphdb:/home/graphdb/data barthanssens/graphdb
```

