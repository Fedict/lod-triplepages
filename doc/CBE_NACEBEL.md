# CBE, Company types and NACEbel

Based upon the [open data CSV files](https://kbopub.economie.fgov.be/kbo-open-data/login?lang=en) 
and the [Excel file](http://statbel.fgov.be/nl/statistieken/gegevensinzameling/nomenclaturen/nacebel/) 
published by the FPS Economy.

See also the conversion tools
(https://github.com/Fedict/lod-cbe/ and https://github.com/Fedict/lod-skosifier)


## Content-negotiation

An HTTP client can ask for various  RDF 1.1 serializations, by setting the HTTP `Accept` header.
All requests are HTTP GET request.

  * `application/ld+json`: JSON-LD
  * `text/turtle`: Turtle
  * `application/n-triples`: N-Triples


## CBE examples

```
http://org.belgif.be/cbe/org/0367_302_178#id  (Fedict)
http://org.belgif.be/cbe/_search?q=fed (Search for names starting with "Fed")
http://org.belgif.be/cbe/_filter?nace=nace2008/84119 (organizations per Nace2008 code)
```

## Company type example
```
http://vocab.belgif.be/orgtype/ (All company types)
http://vocab.belgif.be/orgtype/CBE301#id (Federal public service)
http://vocab.belgif.be/orgtype/_search?q=Dienst (Search for company type names starting with "Dienst")
```


## NACEbel example

```
http://vocab.belgif.be/nace2008/ (All NACEbel codes)
http://vocab.belgif.be/nace2008/84111#id (Federal government)
http://vocab.belgif.be/nace2008/_search?q=Dienst (Search for NACE names starting with "Dienst")
```

