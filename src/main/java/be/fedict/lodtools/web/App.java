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

import be.fedict.lodtools.web.auth.DummyUser;
import be.fedict.lodtools.web.auth.UpdateAuth;
import be.fedict.lodtools.web.health.RdfStoreHealthCheck;
import be.fedict.lodtools.web.helpers.HTMLMessageBodyWriter;
import be.fedict.lodtools.web.helpers.RDFMessageBodyWriter;
import be.fedict.lodtools.web.resources.CpsvResource;
import be.fedict.lodtools.web.resources.GeoResource;
import be.fedict.lodtools.web.resources.OrgResource;
import be.fedict.lodtools.web.resources.RdfResource;
import be.fedict.lodtools.web.resources.VocabResource;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.WebApplicationException;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.glassfish.hk2.utilities.reflection.Logger;


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
	
	/**
	 * Initialize specific Resource class
	 * 
	 * @param cl
	 * @param repo repository
	 * @return resource class
	 */
	private Object getResource(Class<RdfResource> cl, Repository repo) {
		try {
			Constructor c = cl.getConstructor(Repository.class);
			return c.newInstance(repo);
		} catch (ReflectiveOperationException ex) {
			throw new WebApplicationException(ex);
		}
	}
	
	@Override
    public void run(AppConfig config, Environment env) {
		
		// RDF Serialization formats
		env.jersey().register(new RDFMessageBodyWriter());
		//env.jersey().register(new HTMLMessageBodyWriter());
		
		// Managed resource
		String endpoint = config.getSparqlPoint();
		RemoteRepositoryManager mgr = 
				(RemoteRepositoryManager) RepositoryProvider.getRepositoryManager(endpoint);
		if (config.getUsername() != null) {
			mgr.setUsernameAndPassword(config.getUsername(), config.getPassword());
		}
		
		// Monitoring
		RdfStoreHealthCheck check = new RdfStoreHealthCheck(mgr.getSystemRepository());
		env.healthChecks().register("triplestore", check);

		// Authentication
		env.jersey().register(new AuthDynamicFeature(
				new BasicCredentialAuthFilter.Builder<DummyUser>()
						.setAuthenticator(new UpdateAuth())
						.buildAuthFilter()));
		
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
				Object resource = getResource(map.get(name), repo);
				env.jersey().register(resource);
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
