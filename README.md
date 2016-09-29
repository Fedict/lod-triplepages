# lodtools-triplepages

Simple front-end for RDF triplestore / Linked Open Data PoC's


## Content-negotiation

An HTTP client can ask for various  RDF 1.1 serializations, by setting the HTTP `Accept` header

  * `application/ld+json`: JSON-LD
  * `text/turtle`: Turtle
  * `application/n-triples`: N-Triples

