# GraphDB notes

GraphDB is a commercial RDF triple store, developed by OntoText.
The [free edition](http://ontotext.com/products/graphdb/editions/)
is fully functional but limited to running two queries in parallel.

## Security

For security reasons, it is recommended to
  * change the default admin password
  * enable the "Security" and disable "Free Access" setting in the web interface
  * create a new user with limited access (however, this user should have read-only 
access to the SYSTEM namespace)

## Full text search

As many other triple stores, full text search using SPARQL queries is not very efficient.
Therefore GraphDB adds a custom solution based upon Lucene.

Additional configuration is required to make it work,
as explained in the [detailed example](http://graphdb.ontotext.com/documentation/free/full-text-search.html#detailed-example).


