# lodtools-triplepages

Simple front-end for RDF triplestore / Linked Open Data PoC's.

## Description

A typical setup consists of two dockerized services, the front-end and a triplestore.
Data and configuration is stored on a docker volume
(otherwise the data is lost when the docker containers get terminated / rebooted)


## Content-negotiation

An HTTP client can ask for various  RDF 1.1 serializations, by setting the HTTP `Accept` header

  * `application/ld+json`: JSON-LD
  * `text/turtle`: Turtle
  * `application/n-triples`: N-Triples

## Available datasets

These sets are not updated, there are no guarantees on availability and correctness.

  * Crossroad Banks Enterprises (see also [https://github.com/Fedict/lod-cbe/])
  * NACEbel 2008 codes (see also [https://github.com/Fedict/lod-skosifier])
  * Fedict's FSB services (based upon XML file)

CBE examples
```
http://org.belgif.be/cbe/org/0367_302_178#id  (Fedict)
http://org.belgif.be/cbe/_search?q=fed (Search for names starting with "Fed")
http://org.belgif.be/cbe/_filter?nace=nace2008/84119 (organizations per Nace2008 code)
```

NACEbel example
```
http://vocab.belgif.be/nace2008/84111#id (Federal government)
```

FSB examples
```
http://pubserv.belgif.be/fedict/fsb/catalog#id (List of families)
http://pubserv.belgif.be/fedict/fsb/family/S039-EnterpriseServices#id (Services of S039 family)
http://pubserv.belgif.be/fedict/fsb/_search?q=KBO (Service for KBO)
```

## Docker containers

### Front-end

A custom dropwizard application, the default dropwizard port (8080) is forwarded to port 80 (HTTP)

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

