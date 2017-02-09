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

import com.hp.hpl.jena.vocabulary.RDFS;

import net.bioclipse.icebear.business.Entry;
import net.bioclipse.icebear.business.Fields;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class RdfsExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();

		List<String> labels = new ArrayList<String>();
		labels.addAll(getPredicate(store, resource, RDFS.label.toString()));		
		// the first will do fine, but pick the first English one
		for (String label : labels) {
			if (label.endsWith("@en")) {
				label = label.substring(0, label.indexOf("@en")); // remove the lang indication
				props.add(new Entry(resource, Fields.LABEL, RDFS.label.toString(), label));
			} else if (!label.contains("@")) {
				props.add(new Entry(resource, Fields.LABEL, RDFS.label.toString(), label));
			}
		}

		// subclass info
    	List<String> types = new ArrayList<String>();
		types.addAll(getPredicate(store, resource, RDFS.subClassOf.toString()));
		for (String type : types) {
			props.add(new Entry(resource, Fields.TYPE, RDFS.subClassOf.toString(), type));
		}

		return props;
	}
}
