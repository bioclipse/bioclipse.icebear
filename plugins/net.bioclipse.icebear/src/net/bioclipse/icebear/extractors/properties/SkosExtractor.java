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
import net.bioclipse.icebear.business.Fields;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class SkosExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();

		// labels
    	List<String> labels = new ArrayList<String>();
		labels.addAll(getPredicate(store, resource, "http://www.w3.org/2004/02/skos/core#prefLabel"));
		labels.addAll(getPredicate(store, resource, "http://www.w3.org/2004/02/skos/core#altLabel"));
		// the first will do fine, but pick the first English one
		for (String label : labels) {
			if (label.endsWith("@en")) {
				label = label.substring(0, label.indexOf("@en")); // remove the lang indication
				props.add(new Entry(resource, Fields.LABEL, "http://www.w3.org/2004/02/skos/core#prefLabel", label)); // FIXME
			} else if (!label.contains("@")) {
				props.add(new Entry(resource, Fields.LABEL, "http://www.w3.org/2004/02/skos/core#prefLabel", label));
			}
		}

		// get a description
		List<String> descriptions = new ArrayList<String>();
		descriptions.addAll(getPredicate(store, resource, "http://www.w3.org/2004/02/skos/core#definition"));
		for (String desc : descriptions) {
			if (desc.endsWith("@en")) {
				desc = desc.substring(0, desc.indexOf("@en")); // remove the lang indication
				props.add(new Entry(resource, Fields.DESCRIPTION, "http://www.w3.org/2004/02/skos/core#definition", desc));
			} else if (!desc.contains("@")) {
				props.add(new Entry(resource, Fields.DESCRIPTION, "http://www.w3.org/2004/02/skos/core#definition", desc));
			}
		}

		return props;
	}
}
