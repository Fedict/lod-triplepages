# lodtools-triplepages

Simple front-end for RDF triplestore / Linked Open Data PoC's.

## Description

A typical setup consists of [Docker-ized](DOCKERS.md) services including a front-end and a triplestore for large (meta)datasets, and all-in-one microservices with embedded data store.
Data and configuration files are stored on a docker volume
(otherwise the data is lost when the docker containers get terminated / rebooted)

## Available datasets

These sets are not updated, there are no guarantees on availability and correctness.

  * [Crossroad Banks Enterprises](doc/CBE_NACEBEL.md)
  * [Company types](doc/CBE_NACEBEL.md)
  * [NACEbel 2008 codes](doc/CBE_NACEBEL.md)
  * [Fedict's FSB services](doc/FSB.md) 
  * [Demo link store for WCMS](https://github.com/Fedict/lod-link/blob/master/README.md)
  * [Fedict's Framework Contracts](doc/PROCUREMENT.md)
  * [Various code lists, themes...](https://github.com/Fedict/lod-vocab/blob/master/README.md)


