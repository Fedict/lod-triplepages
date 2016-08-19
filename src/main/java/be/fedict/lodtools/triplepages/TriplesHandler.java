/*
 * Copyright (c) 2016, Bart Hanssens <bart.hanssens@fedict.be>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package be.fedict.lodtools.triplepages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.eclipse.rdf4j.common.iteration.Iterations;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * Raw servlet handler for serving RDF triples, supports HTTP content negotiation
 * 
 * @author Bart.Hanssens
 */
public class TriplesHandler extends AbstractHandler {
	private final static ValueFactory fac = SimpleValueFactory.getInstance();
	private final Repository repo;
	
	/**
	 * Get triples from repository (if any)
	 * 
	 * @param req http request
	 * @param resp http response
	 * @param fmt RDF serialization format
	 * @throws IOException 
	 */
	private void triples(HttpServletRequest req, HttpServletResponse resp, RDFFormat fmt) 
															throws IOException {
		resp.setHeader(HttpHeader.CONTENT_TYPE.asString(), fmt.getName());
		
		IRI subj = fac.createIRI(req.getRequestURL().toString());
		
		Model m = new LinkedHashModel();
		m.setNamespace(DC.PREFIX, DC.NAMESPACE);
		m.setNamespace(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
		m.setNamespace(FOAF.PREFIX, FOAF.NAMESPACE);
		m.setNamespace(SKOS.PREFIX, SKOS.NAMESPACE);
		
		try(RepositoryConnection conn = repo.getConnection()) {
			Iterations.addAll(conn.getStatements(subj, null, null), m);
		}
		resp.setStatus(m.isEmpty() ? HttpServletResponse.SC_NOT_FOUND
									: HttpServletResponse.SC_OK);
		Rio.write(m, resp.getOutputStream(), fmt);
	}
	
	@Override
	public void handle(String target, Request base, HttpServletRequest req, 
			HttpServletResponse resp) throws IOException, ServletException {
		
		String accept = req.getHeader(HttpHeader.ACCEPT.asString());
		
		if (accept.contains(RDFFormat.JSONLD.getName())) {
			triples(req, resp, RDFFormat.JSONLD);
			return;
		}
		if (accept.contains(RDFFormat.NTRIPLES.getName())) {
			triples(req, resp, RDFFormat.NTRIPLES);
			return;
		}
		triples(req, resp, RDFFormat.TURTLE);
	}
	
	/**
	 * Constructor
	 * 
	 * @param repo RDF triple store 
	 */
	TriplesHandler(Repository repo) {
		this.repo = repo;
	}
}
