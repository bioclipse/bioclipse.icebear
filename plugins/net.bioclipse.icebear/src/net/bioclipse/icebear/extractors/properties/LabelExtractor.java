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

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DC_10;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LabelExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
    	List<String> labels = new ArrayList<String>();
		labels.addAll(getPredicate(store, resource, DC.title.toString()));
		labels.addAll(getPredicate(store, resource, DC_10.title.toString()));
		labels.addAll(getPredicate(store, resource, DC_11.title.toString()));
		labels.addAll(getPredicate(store, resource, RDFS.label.toString()));
		labels.addAll(getPredicate(store, resource, "http://www.w3.org/2004/02/skos/core#prefLabel"));
		labels.addAll(getPredicate(store, resource, "http://www.w3.org/2004/02/skos/core#altLabel"));
		
		List<Entry> props = new ArrayList<Entry>();
		// the first will do fine, but pick the first English one
		for (String label : labels) {
			if (label.endsWith("@en")) {
				label = label.substring(0, label.indexOf("@en")); // remove the lang indication
				props.add(new Entry(resource, "Label", label));
			} else if (!label.contains("@")) {
				props.add(new Entry(resource, "Label", label));
			}
		}
		return props;
	}
}