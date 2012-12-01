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

import net.bioclipse.icebear.business.Entry;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.rdf.business.IRDFStore;

import com.hp.hpl.jena.vocabulary.DC_10;
import com.hp.hpl.jena.vocabulary.DC_11;

public class DublinCoreExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();

		// extract labels
    	List<String> labels = new ArrayList<String>();
		labels.addAll(getPredicate(store, resource, DC_10.title.toString()));
		labels.addAll(getPredicate(store, resource, DC_11.title.toString()));
		// the first will do fine, but pick the first English one
		for (String label : labels) {
			if (label.endsWith("@en")) {
				label = label.substring(0, label.indexOf("@en")); // remove the lang indication
				props.add(new Entry(resource, "Label", label));
			} else if (!label.contains("@")) {
				props.add(new Entry(resource, "Label", label));
			}
		}

		// extract identifiers
		List<String> identifiers = new ArrayList<String>();
		identifiers.addAll(getPredicate(store, resource.toString(), DC_10.identifier.toString()));
		identifiers.addAll(getPredicate(store, resource.toString(), DC_11.identifier.toString()));
		for (String identifier : identifiers) {
			if (identifier.endsWith("@en")) {
				identifier = identifier.substring(0, identifier.indexOf("@en")); // remove the lang indication
				props.add(new Entry(resource, "Identifier", identifier));
			} else if (!identifier.contains("@")) {
				props.add(new Entry(resource, "Identifier", identifier));
			}
		}

		// extract descriptions
		List<String> descriptions = new ArrayList<String>();
		descriptions.addAll(getPredicate(store, resource, DC_10.description.toString()));
		descriptions.addAll(getPredicate(store, resource, DC_11.description.toString()));
		for (String desc : descriptions) {
			props.add(new Entry(resource, "Description", desc));
		}
		return props;
	}
}
