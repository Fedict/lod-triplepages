# lodtools-triplepages

Simple front-end for RDF triplestore / Linked Open Data PoC's.

## Description

A typical setup consists of two Docker-ized services: the front-end and a triplestore.
Data and configuration is stored on a docker volume
(otherwise the data is lost when the docker containers get terminated / rebooted)



## Available datasets

These sets are not updated, there are no guarantees on availability and correctness.

  * [Crossroad Banks Enterprises](doc/CBE_NACEBEL.md)
  * [NACEbel 2008 codes](doc/CBE_NACEBEL.md) 
  * [Fedict's FSB services](doc/FSB.md) 
  * [Demo link store for WCMS](doc/LINK.md)



## Docker containers

### Front-end

A custom Dropwizard application, the default Dropwizard port (8080) is forwarded to port 80 (HTTP)

```
docker run --name dw -p 80:8080 -d 
-e "DW_CFG=/home/dropwizard/config.yml" 
-v /home/opendata/data/pages:/home/dropwizard barthanssens/lod-triplepages
```

A YAML config file is required to configure the connection to the triple store and logging.
```
sparqlPoint: "http://172.17.0.2:7200"
username: myuser
password: verysecret

logging:
  level: INFO
  appenders:
    - type: file
      archivedFileCount: 5
      archivedLogFilenamePattern: /home/dropwizard/logs/dw-%d.log.gz
      currentLogFilename: /home/dropwizard/logs/dw.log
```

### RDF Triple Store

This can be a GraphDB triple store, or another store supporting Sesame / RDF4j.
Optionally, the default workbench port (7200) can be forwarded to port 443

```
docker run --name graphdb7 -p 443:7200 -d 
-v /home/opendata/data/graphdb:/home/graphdb/data barthanssens/graphdb
```

