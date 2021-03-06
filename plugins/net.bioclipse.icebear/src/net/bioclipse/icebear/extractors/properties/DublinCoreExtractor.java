/* Copyright (c) 2012-2017  Egon Willighagen <egon.willighagen@gmail.com>
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

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DC_10;
import com.hp.hpl.jena.vocabulary.DC_11;

import net.bioclipse.icebear.business.Entry;
import net.bioclipse.icebear.business.Fields;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class DublinCoreExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();

		// extract labels
    	List<String> labels = new ArrayList<String>();
		labels.addAll(getPredicate(store, resource, DC.title.toString()));
		labels.addAll(getPredicate(store, resource, DC_10.title.toString()));
		labels.addAll(getPredicate(store, resource, DC_11.title.toString()));
		// the first will do fine, but pick the first English one
		for (String label : labels) {
			if (label.endsWith("@en")) {
				label = label.substring(0, label.indexOf("@en")); // remove the lang indication
				props.add(new Entry(resource, Fields.LABEL, DC_10.title.toString(), label)); // FIXME: or DC_11 !
			} else if (!label.contains("@")) {
				props.add(new Entry(resource, Fields.LABEL, DC_10.title.toString(), label)); // FIXME: or DC_11 !
			}
		}

		// extract identifiers
		List<String> identifiers = new ArrayList<String>();
		identifiers.addAll(getPredicate(store, resource.toString(), DC.identifier.toString()));
		identifiers.addAll(getPredicate(store, resource.toString(), DC_10.identifier.toString()));
		identifiers.addAll(getPredicate(store, resource.toString(), DC_11.identifier.toString()));
		for (String identifier : identifiers) {
			if (identifier.endsWith("@en")) {
				identifier = identifier.substring(0, identifier.indexOf("@en")); // remove the lang indication
				props.add(new Entry(resource, Fields.IDENTIFIER, DC_11.identifier.toString(), identifier)); // FIXME
			} else if (!identifier.contains("@")) {
				props.add(new Entry(resource, Fields.IDENTIFIER, DC_11.identifier.toString(), identifier)); // FIXME
			}
		}

		// extract descriptions
		List<String> descriptions = new ArrayList<String>();
		descriptions.addAll(getPredicate(store, resource, DC.description.toString()));
		descriptions.addAll(getPredicate(store, resource, DC_10.description.toString()));
		descriptions.addAll(getPredicate(store, resource, DC_11.description.toString()));
		for (String desc : descriptions) {
			props.add(new Entry(resource, Fields.DESCRIPTION, DC_10.description.toString(), desc));
		}
		return props;
	}
}
