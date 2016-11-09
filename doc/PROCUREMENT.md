# Framework contracts

Based upon a manually created Excel file listing Fedict's framework contracts.

## Content-negotiation

An HTTP client can ask for various  RDF 1.1 serializations, by setting the HTTP `Accept` header.

All requests are HTTP GET request.

  * `application/ld+json`: JSON-LD
  * `text/turtle`: Turtle
  * `application/n-triples`: N-Triples

## Examples

```
http://form.belgif.be/proc/contract (List of all contracts)
http://form.belgif.be/proc/contract/FEDICT-2011-M803-1#id (Specific contract)
```
