/* Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.extractors.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.bioclipse.core.domain.StringMatrix;
import net.bioclipse.icebear.business.Entry;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class SioExtractor extends AbstractExtractor implements IPropertyExtractor {

	private final static String[] SIO_HAS_ATTRIBUTE = {
		"http://semanticscience.org/resource/has-attribute",
		"http://semanticscience.org/resource/SIO_000008"
	};
	private final static String[] SIO_HAS_VALUE = {
		"http://semanticscience.org/resource/has-value",
		"http://semanticscience.org/resource/SIO_000300"
	};

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();

		List<String> attributes = new ArrayList<String>();
		for (String hasAttribute : SIO_HAS_ATTRIBUTE) {
			// get SIO properties
			String sparql =
				"SELECT ?attribute WHERE {" +
				"  <" + resource + "> <" + hasAttribute + "> ?attribute ." +
				"}";
			StringMatrix results = sparql(store, sparql);
			if (results.hasColumn("attribute")) attributes = results.getColumn("attribute");
		}
		for (String attribute : attributes) { // I have attributes to explore
			boolean gotValues = findProperties(store, resource, props, attribute);
			if (!gotValues) {
				System.out.println("Desc resource: " + attribute);
		    	// try to get new results, only if we do not already have results
		    	getAdditionalTriples(store, attribute);
		    	findProperties(store, resource, props, attribute);
		    	try {
		    		Thread.sleep(50); // be a bit friendly to PubChem
		    	} catch (InterruptedException e) {}
			}
		}
		return props;
	}

	private boolean findProperties(IRDFStore store, String resource,
			List<Entry> props, String attribute) {
		boolean gotValues = false;
		for (String hasValue : SIO_HAS_VALUE) {
		  	String sparql =
		        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
		        "SELECT ?type ?value ?label WHERE {" +
		  		"  <" + attribute + "> <" + hasValue + "> ?value ;" +
		  		"    a ?type . " +
		  		"  OPTIONAL { <" + attribute + "> rdfs:label ?label . }" +
		  		"}";
		  	StringMatrix descResults = sparql(store, sparql);
		  	for (int j=1; j<=descResults.getRowCount(); j++) {
		  		String type = descResults.get(j, "type");
		  		if (descResults.hasColumn("label")) {
		  			String label = descResults.get(j, "label");
		  			props.add(new Entry(resource, label, fullURI(type), descResults.get(j, "value")));
		  		} else {
		  			props.add(new Entry(resource, type, fullURI(type), descResults.get(j, "value")));
		  		}
		  		gotValues = true;
		  	}
		}
		return gotValues;
	}

	private String fullURI(String type) {
		if (type.startsWith("resource:")) {
			return "http://semanticscience.org/resource/" + type.substring(9);
		}
		return type;
	}

	Map<String,String> extraHeaders = new HashMap<String, String>() {
		private static final long serialVersionUID = 2825983879781792266L;
	{
	  put("Content-Type", "application/rdf+xml");
	  put("Accept", "application/rdf+xml"); // Both Accept and Content-Type are needed for PubChem 
	}};

	protected void getAdditionalTriples(IRDFStore store, String resource) {
		try {
			rdf.importURL(store, resource, extraHeaders, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
