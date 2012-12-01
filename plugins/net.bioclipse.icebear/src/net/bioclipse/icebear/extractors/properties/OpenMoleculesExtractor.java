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

public class OpenMoleculesExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		List<Entry> props = new ArrayList<Entry>();
		// PubChem
		List<String> cids = getPredicate(store, resource, "http://pubchem.ncbi.nlm.nih.gov/#cid");
		for (String cid : cids) {
			props.add(new Entry(resource, "PubChem CID", cid));
		}
		// ChEBI ID
		List<String> ids = getPredicate(store, resource, "http://rdf.openmolecules.net/#chebiid");
		for (String id : ids) {
			props.add(new Entry(resource, "ChEBI ID", id));
		}
		return props;
	}
}
