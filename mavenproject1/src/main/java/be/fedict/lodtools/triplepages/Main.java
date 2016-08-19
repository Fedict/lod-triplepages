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

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.rdf4j.IsolationLevel;
import org.eclipse.rdf4j.IsolationLevels;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetty front-end and embedded triple store
 * 
 * @author Bart.Hanssens
 */
public class Main {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	private static final Options OPTS = new Options().
			addOption("h", "help", false, "Display help").
			addOption("r", "repository", true, "Path to RDF repository").
			addOption("f", "file", true, "RDF file to load into repository").
			addOption("p", "port", true, "Server port").
			addOption("v", "vhosts", true, "Vhost(s), comma-separated");
	
	/**
	 * Print help to console
	 */
	private static void printHelp() {
		HelpFormatter help = new HelpFormatter();
		help.printHelp("SimpleTriple", "Simple front-end for RDF triple store\n\n", 
					OPTS, "\nSee also http://github.org/fedict", true);
	}
	
	/**
	 * Get and initialize a local RDF repository
	 * 
	 * @param repodir
	 * @return initialized repository 
	 */
	private static Repository getRepository(String repodir) {
		Repository repo = new SailRepository(new NativeStore(new File(repodir)));
		repo.initialize();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				repo.shutDown();
				LOG.info("Shutdown complete");
			}
		});
		return repo;
	}

	
	/**
	 * Load RDF triples into repository
	 * 
	 * @param repo RDF repository
	 * @param f file containing triples
	 * @return true upon success
	 */
	private static boolean loadTriples(Repository repo, File f) {
		boolean status = false;
		Optional<RDFFormat> fmt = Rio.getParserFormatForFileName(f.getName());
		if (!fmt.isPresent()) {
			LOG.error("Could not find parser for file");
		}
		try {
			RepositoryConnection conn = repo.getConnection();
			conn.begin(IsolationLevels.NONE);
			conn.add(f, null, fmt.get());
			conn.commit();
			status = true;
		} catch (IOException ex) {
			LOG.error("Could not load file", ex);
		}
		return status;
	}
	
	/**
	 * Main
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CommandLineParser cli = new DefaultParser();
		CommandLine line = cli.parse(OPTS, args);
		
		if ((line.getOptions().length == 0) || line.hasOption('h')) {
			printHelp();
		}
		
		if (!line.hasOption('r') || (line.getOptionValue('r') == null)) {
			LOG.error("No repository specified");
			System.exit(-2);
		}
		
		Repository repo = getRepository(line.getOptionValue('r'));
		
		if (line.hasOption('f')) {
			File f = new File(line.getOptionValue('f'));
			LOG.info("Loading {} into repository ...", f);
			
			/* This may several minutes, a million triples per minute */		
			if (!loadTriples(repo, f)) {
				repo.shutDown();
				System.exit(-3);
			}
			LOG.info("... done");
		}
		
		String port = line.getOptionValue('p', "80");
		
		LOG.info("Starting embedded server on port {}", port);
		
		Server server = new Server();
		
		ServerConnector http = new ServerConnector(server);
		http.setPort(Integer.valueOf(port));
		server.addConnector(http);
		
		ContextHandler context = new ContextHandler();		
		if (line.hasOption('v')) {
			String[] vhosts = line.getOptionValue('v', "").split(",");
			context.setVirtualHosts(vhosts);
		}
		context.setHandler(new TriplesHandler(repo));
		server.setHandler(context);
		
		server.start();
		server.join();
		
		repo.shutDown();
	}
}
