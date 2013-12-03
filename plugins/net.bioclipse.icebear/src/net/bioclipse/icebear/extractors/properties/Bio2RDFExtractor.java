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
import net.bioclipse.icebear.business.Fields;
import net.bioclipse.icebear.extractors.AbstractExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class Bio2RDFExtractor extends AbstractExtractor implements IPropertyExtractor {

	@Override
	public List<Entry> extractProperties(IRDFStore store, String resource) {
		// labels
    	List<String> labels = new ArrayList<String>();
		labels.addAll(getPredicate(store, resource, "http://bio2rdf.org/obo_resource:synonym"));
		labels.addAll(getPredicate(store, resource, "http://bio2rdf.org/drugbank_ontology:synonym"));
		labels.addAll(getPredicate(store, resource, "http://bio2rdf.org/bio2rdf_resource:synonym"));
		List<Entry> props = new ArrayList<Entry>();
		for (String label : labels) {
			props.add(new Entry(resource, Fields.LABEL, "http://bio2rdf.org/drugbank_ontology:description", label));
		}
		// descriptions
    	List<String> types = new ArrayList<String>();
		types.addAll(getPredicate(store, resource, "http://bio2rdf.org/drugbank_ontology:description"));
		for (String type : types) {
			props.add(new Entry(resource, Fields.DESCRIPTION, "http://bio2rdf.org/drugbank_ontology:description", type));
		}
		// images
    	List<String> images = new ArrayList<String>();
		images.addAll(getPredicate(store, resource, "http://bio2rdf.org/bio2rdf_resource:image"));
		images.addAll(getPredicate(store, resource, "http://bio2rdf.org/bio2rdf_resource:urlImage"));
		for (String image : images) {
			props.add(new Entry(resource, Fields.IMAGE, "http://bio2rdf.org/bio2rdf_resource:image", image)); // FIXME
		}
		// other
		Map<String,String> labelMap = new HashMap<String,String>() {{
			put("Mass", "http://bio2rdf.org/bio2rdf_resource:mass");
			put("SMILES", "http://bio2rdf.org/bio2rdf_resource:smiles");
			put("Charge", "http://bio2rdf.org/bio2rdf_resource:charge");
			put("Formula", "http://bio2rdf.org/bio2rdf_resource:formula");
			put("IUPAC name", "http://bio2rdf.org/bio2rdf_resource:iupacName");
			put("CACO2 permeability", "http://bio2rdf.org/drugbank_ontology:experimentalCaco2Permeability");
			put("LogP", "http://bio2rdf.org/drugbank_ontology:experimentalLogpHydrophobicity");
			put("Water solubility", "http://bio2rdf.org/drugbank_ontology:experimentalWaterSolubility");
			put("Food interactions", "http://bio2rdf.org/drugbank_ontology:foodInteraction");
			put("Mechanism of action", "http://bio2rdf.org/drugbank_ontology:mechanismOfAction");
			put("Melting point", "http://bio2rdf.org/drugbank_ontology:meltingPoint");
			put("Monoisotopic mass", "http://bio2rdf.org/drugbank_ontology:molecularWeightMono");
			put("Isoelectric point", "http://bio2rdf.org/drugbank_ontology:pkaIsoelectricPoint");
			put("Toxicity", "http://bio2rdf.org/drugbank_ontology:toxicity");
			put("Protein binding", "http://bio2rdf.org/drugbank_ontology:primaryAccessionNo");
			put("Pharmacology", "http://bio2rdf.org/drugbank_ontology:pharmacology");
			put("Biotransformation", "http://bio2rdf.org/drugbank_ontology:biotransformation");
		}};
		for (String key : labelMap.keySet()) {
			String value = labelMap.get(key);
			for (Entry entry : extractEntries(store, key, resource, value))
				props.add(entry);
		}
		return props;
	}
}
