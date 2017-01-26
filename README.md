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
  * [Demo link store for WCMS](https://github.com/Fedict/lod-link/blob/master/README.md)
  * [Fedict's Framework Contracts](doc/PROCUREMENT.md)
  * [Various code lists, themes...](https://github.com/Fedict/lod-vocab/blob/master/README.md)
