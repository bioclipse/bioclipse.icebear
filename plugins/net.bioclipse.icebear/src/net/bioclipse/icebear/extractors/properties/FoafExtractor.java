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

import com.hp.hpl.jena.sparql.vocabulary.FOAF;

import net.bioclipse.icebear.business.Entry;
import net.bioclipse.icebear.business.Fields;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class FoafExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();
		List<String> homepages = getPredicate(store, resource, FOAF.homepage.toString());
		for (String homepage : homepages) {
			props.add(new Entry(resource, Fields.HOMEPAGE, FOAF.homepage.toString(), homepage));
		}
		List<String> pages = getPredicate(store, resource, FOAF.page.toString());
		for (String page : pages) {
			props.add(new Entry(resource, Fields.HOMEPAGE, FOAF.page.toString(), page));
		}
		List<String> images = getPredicate(store, resource, FOAF.depiction.toString());
		for (String image : images) {
			props.add(new Entry(resource, Fields.IMAGE, FOAF.depiction.toString(), image));
		}
		return props;
	}
}
