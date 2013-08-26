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
import java.util.List;

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

		for (String hasAttribute : SIO_HAS_ATTRIBUTE) {
			for (String hasValue : SIO_HAS_VALUE) {
				// get SIO properties
				String sparql =
					"PREFIX sio: <http://semanticscience.org/resource/>\n" +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
					"SELECT ?label ?desc ?value WHERE {" +
					"  <" + resource + "> <" + hasAttribute + "> ?property ." +
					"  ?property <" + hasValue + "> ?value ." +
					"  OPTIONAL { ?property a ?desc . } " +
					"  OPTIONAL { ?property rdfs:label ?label . }" +
					"}";
				StringMatrix results = sparql(store, sparql);
				for (int i=1; i<=results.getRowCount(); i++) {
					String type = "NA";
					if (results.get(i, "label") != null) {
						type = results.get(i, "label");
					} else if (results.get(i, "desc") != null) {
						type = results.get(i, "desc");
					}
					props.add(new Entry(resource, type, results.get(i, "value")));			
				}
			}
		}
		return props;
	}
}
