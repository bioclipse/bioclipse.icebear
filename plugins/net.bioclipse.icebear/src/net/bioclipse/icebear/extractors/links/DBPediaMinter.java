/* Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.extractors.links;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.INextURIExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class DBPediaMinter extends AbstractExtractor implements INextURIExtractor {

	@Override
	public List<String> extractURIs(IRDFStore store, String uri) {
		List<String> extractedURIs = new ArrayList<String>();
		if (uri.toString().startsWith("http://dbpedia.org/resource")) {
			try {
				List<String> casNumbers = rdf.getForPredicate(store, uri.toString(), "http://dbpedia.org/ontology/casNumber");
				for (String cas : casNumbers) {
					System.out.println("CAS reg number: " + cas);
					// recurse
					extractedURIs.add("http://bio2rdf.org/cas:" + cas);
				}
			} catch (BioclipseException exeption) {} // just ignore
			try {
				List<String> drugBankIDs = rdf.getForPredicate(store, uri.toString(), "http://dbpedia.org/ontology/drugbank");
				for (String drugbank : drugBankIDs) {
					System.out.println("Drugbank code: " + drugbank);
					extractedURIs.add("http://bio2rdf.org/drugbank_drugs:" + drugbank);
				}
			} catch (BioclipseException exeption) {} // just ignore
			try {
				List<String> ids = rdf.getForPredicate(store, uri.toString(), "http://dbpedia.org/ontology/pubchem");
				for (String id : ids) {
					System.out.println("PubChem: " + id);
					extractedURIs.add("http://rdf.ncbi.nlm.nih.gov/pubchem/compound/CID" + id);
				}
			} catch (BioclipseException exeption) {} // just ignore
			try {
				List<String> ids = rdf.getForPredicate(store, uri.toString(), "http://dbpedia.org/property/chembl");
				for (String id : ids) {
					id = stripDataType(id);
					System.out.println("CHEMBL: " + id);
					extractedURIs.add("http://linkedchemistry.info/chembl/chemblid/CHEMBL" + id);
				}
			} catch (BioclipseException exeption) {} // just ignore
			try {
				List<String> ids = rdf.getForPredicate(store, uri.toString(), "http://dbpedia.org/property/chebi");
				for (String id : ids) {
					id = stripDataType(id);
					System.out.println("ChEBI: " + id);
					extractedURIs.add("http://bio2rdf.org/chebi:" + id);
				}
			} catch (BioclipseException exeption) {} // just ignore
			try {
				List<String> ids = rdf.getForPredicate(store, uri.toString(), "http://dbpedia.org/property/kegg");
				for (String id : ids) {
					System.out.println("KEGG: " + id);
					// recurse
					if (id.startsWith("C")) {
						extractedURIs.add("http://bio2rdf.org/cpd:" + id);
					} else if (id.startsWith("D")) {
						extractedURIs.add("http://bio2rdf.org/dr:" + id);
					}
				}
			} catch (BioclipseException exeption) {} // just ignore
		}
		return extractedURIs;
	}


}
