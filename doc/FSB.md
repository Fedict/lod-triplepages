# FSB services

Based upon a manually created file documenting the names of Fedict's FSB services and their "families".

## Content-negotiation

An HTTP client can ask for various  RDF 1.1 serializations, by setting the HTTP `Accept` header.

All requests are HTTP GET request.

  * `application/ld+json`: JSON-LD
  * `text/turtle`: Turtle
  * `application/n-triples`: N-Triples

## FSB examples

```
http://pubserv.belgif.be/fedict/fsb/catalog#id (List of families)
http://pubserv.belgif.be/fedict/fsb/family/S039-EnterpriseServices#id (Services of S039 family)
http://pubserv.belgif.be/fedict/fsb/_search?q=KBO (Service for KBO)
```
