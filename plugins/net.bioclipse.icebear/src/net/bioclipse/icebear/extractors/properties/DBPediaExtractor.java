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
		Map<String,String> labelMap = new HashMap<String,String>() {{
			put("Administration", "http://dbpedia.org/property/routesOfAdministration");
			put("Bioavailability", "http://dbpedia.org/property/bioavailability");
			put("Boiling point", "http://dbpedia.org/property/boilingPoint");
			put("Melting point", "http://dbpedia.org/property/meltingPoint");
		}};
		for (String key : labelMap.keySet()) {
			String value = labelMap.get(key);
			for (Entry entry : extractEntries(store, key, resource, value))
				props.add(entry);
		}
		return props;
	}
}
