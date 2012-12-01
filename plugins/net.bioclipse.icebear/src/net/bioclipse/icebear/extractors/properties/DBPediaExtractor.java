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

import net.bioclipse.icebear.business.Entry;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class DBPediaExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();
		Map<String,String> resultMap = new HashMap<String, String>();
		addPredicateToMap(store, resultMap, "Administration", resource, "http://dbpedia.org/property/routesOfAdministration");
		addPredicateToMap(store, resultMap, "Bioavailability", resource, "http://dbpedia.org/property/bioavailability");
		addPredicateToMap(store, resultMap, "Boiling point", resource, "http://dbpedia.org/property/boilingPoint");
		addPredicateToMap(store, resultMap, "Melting point", resource, "http://dbpedia.org/property/meltingPoint");
//		addResourcePredicateToMap(store, resultMap, "Metabolism", resource, "http://dbpedia.org/property/metabolism");
//		addResourcePredicateToMap(
//			store, resultMap, "Excretion", ronURI.toString(), "http://dbpedia.org/property/excretion"
//		);
		for (String key : resultMap.keySet()) {
			String value = resultMap.get(key);
			props.add(new Entry(resource, key, value));
		}
		return props;
	}
}
