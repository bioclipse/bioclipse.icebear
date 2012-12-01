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

public class ChemInfExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();

		// get CHEMINF properties
		String sparql =
			"PREFIX resource:  <http://semanticscience.org/resource/>\n" +
			"SELECT ?type ?value WHERE {" +
			"  <" + resource + "> resource:CHEMINF_000200 ?desc ." +
			"  ?desc resource:SIO_000300 ?value ;" +
			"    a ?type . " +
			"}";
		StringMatrix results = sparql(store, sparql);
		for (int i=1; i<=results.getRowCount(); i++) {
			props.add(new Entry(resource, results.get(i, "type"), results.get(i, "value")));			
		}
		return props;
	}
}
