# Docker containers

## Proxy

The idea is to create one Dropwizard application and subdomain per dataset type.
The [Nginx-proxy] (https://github.com/jwilder/nginx-proxy) docker is used to send the requests to the Dropwizard microservices. The container is set to automatically restart when it exits unexpectedly.

```
docker run --restart=unless-stopped -d -p 80:80 -v /var/run/docker.sock:/tmp/docker.sock:ro jwilder/nginx-proxy
```

## Vocab front-end with embedded RDF store

A custom [Dropwizard](http://www.dropwizard.io/) application, provided as a pre-built local image `barthanssens/lod-vocab`

See also [lod-vocab](https://github.com/Fedict/lod-vocab)

In this example the image uses an environment variable `DW_CFG` to set the location of the YAML config file. The default Dropwizard port (8080) and the name of the subdomain (`vocab.belgif.be`) are used by the nginx-proxy. 
Inside the container, the home directory `/home/dropwizard` is used to store persistent data as a docker volume, mapped/mounted to the host file system `/home/opendata/data/vocab`. This directory must be read/writable for the container.

```
docker run --name vocab -d  -e "DW_CFG=/home/dropwizard/config.yml" -e "VIRTUAL_HOST=vocab.belgif.be" -e "VIRTUAL_PORT=8080" -v /home/opendata/data/vocab:/home/dropwizard barthanssens/lod-vocab
```

## Link front-end with embedded RDF store

Similar tp vocab front-end, see also  [lod-link](https://github.com/Fedict/lod-link)

## Front-end for other datatypes

A custom [Dropwizard](http://www.dropwizard.io/) application, provided as a pre-built local image `barthanssens/lod-triplepages`

In this example the image uses an environment variable `DW_CFG` to set the location of the YAML config file. The default Dropwizard port (8080) and the name of the subdomains (`org.belgif.be`...) are used by the nginx-proxy. 
Inside the container, the home directory `/home/dropwizard` is used to store persistent data as a docker volume, mapped/mounted to the host file system `/home/opendata/data/pages`. This directory must be read/writable for the container.

```
docker run --restart=unless-stopped --name dw -d -e "VIRTUALHOST=org.belgif.be,form.belgif.be,pubserv.belgif.be,link.belgif.be" -e "DW_CFG=/home/dropwizard/config.yml" -v /home/opendata/data/pages:/home/dropwizard barthanssens/lod-triplepages
```

A YAML config file is required to configure the connection to the triple store and logging of HTTP requests and Java exceptions.

```
sparqlPoint: "http://172.17.0.2:7200"
username: myuser
password: verysecret

logging:
  level: INFO
  appenders:
  - type: file
    archive: false
    currentLogFilename: /home/dropwizard/dw.log
  
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

## RDF Triple Store

This can be a [GraphDB](doc/GRAPHDB.md) triple store, or another store supporting [RDF4j](http://rdf4j.org/).
In this example the default GrapHDB workbench port (7200) is mapped to the host port 8443

```
docker run --restart=unless-stopped --name graphdb7 -p 8443:7200 -d 
-v /home/opendata/data/graphdb:/home/graphdb/data barthanssens/graphdb
```