/* Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.extractors;

import java.util.Collections;
import java.util.List;

import net.bioclipse.core.domain.StringMatrix;
import net.bioclipse.rdf.business.IRDFStore;
import net.bioclipse.rdf.business.RDFManager;

public class AbstractExtractor {

	protected RDFManager rdf = new RDFManager();

	protected List<String> getPredicate(IRDFStore store, String resource, String predicate) {
		try {
			return rdf.getForPredicate(store, resource, predicate);
		} catch (Throwable exception) {
			exception.printStackTrace();
		};
		return Collections.emptyList();
	}

	protected List<String> allOwlSameAs(IRDFStore store, String resource) {
		try {
			return rdf.allOwlSameAs(store, resource);
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		return Collections.emptyList();
	}

	protected List<String> allOwlEquivalentClass(IRDFStore store, String resource) {
		try {
			return rdf.allOwlEquivalentClass(store, resource);
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		return Collections.emptyList();
	}

	protected List<String> allSkosExactmatch(IRDFStore store, String resource) {
		try {
			return rdf.getForPredicate(
				store, resource, "http://www.w3.org/2004/02/skos/core#exactMatch"
			);
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		return Collections.emptyList();
	}

	protected StringMatrix sparql(IRDFStore store, String query) {
		try {
			return rdf.sparql(store, query);
		} catch (Throwable exception) {
			exception.printStackTrace();
		};
		return new StringMatrix();
	}

}
