# CBE and NACEbel

## Content-negotiation

An HTTP client can ask for various  RDF 1.1 serializations, by setting the HTTP `Accept` header.
All requests are HTTP GET request.

  * `application/ld+json`: JSON-LD
  * `text/turtle`: Turtle
  * `application/n-triples`: N-Triples


## CBE examples

Based upon the [open data CSV files](https://kbopub.economie.fgov.be/kbo-open-data/login?lang=en) 
published by the FPS Economy.
See also the conversion tool [https://github.com/Fedict/lod-cbe/]

```
http://org.belgif.be/cbe/org/0367_302_178#id  (Fedict)
http://org.belgif.be/cbe/_search?q=fed (Search for names starting with "Fed")
http://org.belgif.be/cbe/_filter?nace=nace2008/84119 (organizations per Nace2008 code)
```

The following vocabularies are used https://www.w3.org/TR/vocab-regorg/

## NACEbel example

Based upon the [Excel file](http://statbel.fgov.be/nl/statistieken/gegevensinzameling/nomenclaturen/nacebel/) 
published by FPS Economy / Statbel.
See also the conversion tool [https://github.com/Fedict/lod-skosifier]

```
http://vocab.belgif.be/nace2008/84111#id (Federal government)
```

