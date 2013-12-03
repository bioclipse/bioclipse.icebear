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

public class PubChemRDFExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		System.out.println("Getting PubChem RDF properties");
		List<Entry> props = new ArrayList<Entry>();
		// SPARQL PC RDF
		String sparql =
			"PREFIX sio:  <http://semanticscience.org/resource/>\n" +
			"SELECT ?desc WHERE {" +
			"  <" + resource + "> sio:has-attribute ?desc ." +
			"}";
		StringMatrix results = sparql(store, sparql);
		for (int i=1; i<=results.getRowCount(); i++) {
			String descResource = results.get(i, "desc");
			System.out.println("Desc resource: " + descResource);
  	        getAdditionalTriples(store, descResource);
  		    sparql =
  		  	  "PREFIX resource:  <http://semanticscience.org/resource/>\n" +
  	 		  "SELECT ?type ?value WHERE {" +
  		  	  "  <" + descResource + "> resource:has-value ?value ;" +
  			  "    a ?type . " +
  			  "}";
  		    StringMatrix descResults = sparql(store, sparql);
  		    for (int j=1; j<=descResults.getRowCount(); j++) {
  			    props.add(new Entry(descResource, descResults.get(j, "type"), "http://semanticscience.org/resource/has-value", descResults.get(j, "value")));			
  	        }
		}

		return props;
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
