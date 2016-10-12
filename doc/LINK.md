# Link store

## Content-negotiation

An HTTP client can use various  RDF 1.1 serializations.

For GET requests, the HTTP `Accept` header should be set

For PUT requests, the HTTP `Content-Type` header must be set, and UTF-8 encoding must be used.

  * `application/ld+json`: JSON-LD
  * `text/turtle`: Turtle
  * `application/n-triples`: N-Triples

## Retrieving all info about a link (GET)

The value of the `url` parameter must be URL-encoded.
```
http://pubserv.belgif.be/link?url=
```

## Filtering link(s) (GET)

Currently only the [Data Theme list](http://publications.europa.eu/mdr/resource/authority/data-theme/html/data-theme-eng.html)
published by EU Publication Office is supported.
```
http://pubserv.belgif.be/link/_filter?theme=TECH
```

## Adding information about a link (PUT)

Requires HTTP basic authentication.

## Removing a link (DELETE)

Requires HTTP basic authentication.



