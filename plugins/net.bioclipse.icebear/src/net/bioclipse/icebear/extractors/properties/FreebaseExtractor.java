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

public class FreebaseExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();
		Map<String,String> resultMap = new HashMap<String, String>();
		addPredicateToMap(store, resultMap, "Average molar mass", resource, "http://rdf.freebase.com/ns/chemistry.chemical_compound.average_molar_mass");
		addPredicateToMap(store, resultMap, "Boiling point", resource, "http://rdf.freebase.com/ns/chemistry.chemical_compound.boiling_point");
		addPredicateToMap(store, resultMap, "Melting point", resource, "http://rdf.freebase.com/ns/chemistry.chemical_compound.melting_point");
		addPredicateToMap(store, resultMap, "Density", resource, "http://rdf.freebase.com/ns/chemistry.chemical_compound.density");
		for (String key : resultMap.keySet()) {
			String value = resultMap.get(key);
			props.add(new Entry(resource, key, value));
		}
		return props;
	}
}
