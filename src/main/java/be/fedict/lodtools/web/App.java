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
package be.fedict.lodtools.web;

import be.fedict.lodtools.web.health.RdfStoreHealthCheck;
import be.fedict.lodtools.web.helpers.RDFMessageBodyWriter;
import be.fedict.lodtools.web.resources.CpsvResource;
import be.fedict.lodtools.web.resources.GeoResource;
import be.fedict.lodtools.web.resources.OrgResource;
import be.fedict.lodtools.web.resources.RdfResource;
import be.fedict.lodtools.web.resources.VocabResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.rdf4j.repository.Repository;

import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;


/**
 * Main Dropwizard web application
 * 
 * @author Bart.Hanssens
 */
public class App extends Application<AppConfig> {
	@Override
	public String getName() {
		return "lod-triplepages";
	}
	
	@Override
    public void run(AppConfig config, Environment env) throws Exception {
		
		// RDF Serialization formats
		env.jersey().register(new RDFMessageBodyWriter());
		
		// Managed resource
		String endpoint = config.getSparqlPoint();
		RepositoryManager mgr = RepositoryProvider.getRepositoryManager(endpoint);
		
		// Monitoring
		RdfStoreHealthCheck check = new RdfStoreHealthCheck(mgr.getSystemRepository());
		env.healthChecks().register("triplestore", check);

		// Repositories
		Map<String,Class<RdfResource>> map = new HashMap() {{
			put("CBE", OrgResource.class);
			put("GEO", GeoResource.class);
			put("VOCAB", VocabResource.class);
			put("CPSV", CpsvResource.class);
		}};
		
		for(String name: map.keySet()) {
			Repository repo = mgr.getRepository(name);
			if (repo != null) {
				Constructor c = map.get(name).getConstructor(Repository.class);
				env.jersey().register(c.newInstance(repo));
			}
		}
	}
	
	/**
	 * Main 
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new App().run(args);
	}
}
