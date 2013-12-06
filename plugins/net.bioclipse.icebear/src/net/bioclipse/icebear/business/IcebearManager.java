/* Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.business;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bioclipse.business.BioclipsePlatformManager;
import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.domain.IMolecule.Property;
import net.bioclipse.icebear.extractors.INextURIExtractor;
import net.bioclipse.icebear.extractors.IPropertyExtractor;
import net.bioclipse.icebear.extractors.links.DBPediaMinter;
import net.bioclipse.icebear.extractors.links.OwlEquivalentClassExtractor;
import net.bioclipse.icebear.extractors.links.OwlSameAsExtractor;
import net.bioclipse.icebear.extractors.links.SkosExactMatchExtractor;
import net.bioclipse.icebear.extractors.properties.ChemAxiomExtractor;
import net.bioclipse.icebear.extractors.properties.DBPediaExtractor;
import net.bioclipse.icebear.extractors.properties.DublinCoreExtractor;
import net.bioclipse.icebear.extractors.properties.FoafExtractor;
import net.bioclipse.icebear.extractors.properties.FreebaseExtractor;
import net.bioclipse.icebear.extractors.properties.OpenMoleculesExtractor;
import net.bioclipse.icebear.extractors.properties.RdfsExtractor;
import net.bioclipse.icebear.extractors.properties.SioExtractor;
import net.bioclipse.icebear.extractors.properties.SkosExtractor;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.rdf.business.IRDFStore;
import net.bioclipse.rdf.business.RDFManager;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DC_10;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDFS;

public class IcebearManager implements IBioclipseManager {

	private static final String ICON = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAFZJREFUeF59z4EJADEIQ1F36k7u5E7ZKXeUQPACJ3wK7UNokVxVk9kHnQH7bY9hbDyDhNXgjpRLqFlo4M2GgfyJHhjq8V4agfrgPQX3JtJQGbofmCHgA/nAKks+JAjFAAAAAElFTkSuQmCC";
	
	private static final Logger logger = Logger.getLogger(IcebearManager.class);

	private CDKManager cdk = new CDKManager();
	private RDFManager rdf = new RDFManager();
	private BioclipsePlatformManager bioclipse = new BioclipsePlatformManager();

	private Map<String,String> resourceMap = new HashMap<String, String>() {
		private static final long serialVersionUID = -7354694153097755405L;
	{
		// prepopulate it with things I already know about so that we do not have to look that up
		put("http://semanticscience.org/resource/CHEMINF_000000", "chemical entity");
		put("http://bio2rdf.org/ns/chebi#Compound", "compound");
		put("http://bio2rdf.org/chebi_resource:Compound", "compound");
		put("http://www.polymerinformatics.com/ChemAxiom/ChemDomain.owl#NamedChemicalSpecies", "named chemical species");
		put("http://umbel.org/umbel/rc/DrugProduct", "drug product");
		put("http://umbel.org/umbel/rc/Drug", "drug");
		put("http://dbpedia.org/ontology/Drug", "drug");
		put("http://rdf.freebase.com/ns/medicine.medical_treatment", "medical treatment");
		put("http://rdf.freebase.com/ns/medicine.risk_factor", "risk factor");
		put("http://rdf.freebase.com/ns/chemistry.chemical_compound", "chemical compound");
		put("http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugs", "drug");
		put("http://www4.wiwiss.fu-berlin.de/sider/resource/sider/drugs", "drug");
		put("http://bio2rdf.org/kegg_resource:Drug", "drug");
		put("http://bio2rdf.org/drugbank_ontology:drugs", "drug");
		put("http://xmlns.com/foaf/0.1/Document", "document");
		put("http://bio2rdf.org/drugbank_drugtype:approved", "approved drug");
		put("http://bio2rdf.org/drugbank_drugtype:smallMolecule", "small molecule");
		put("http://purl.obolibrary.org/obo/CHEBI_23367", "molecular entity");
		put("http://www.opentox.org/api/1.1#Compound", "compound");
		put("http://semanticscience.org/resource/CHEMINF_000113", "InChI");
		put("http://semanticscience.org/resource/CHEMINF_000140", "PubChem CID");
		put("http://semanticscience.org/resource/CHEMINF_000334", "molecular weight");
		put("http://semanticscience.org/resource/CHEMINF_000335", "molecular formula");
		put("http://semanticscience.org/resource/CHEMINF_000336", "total formal charge");
		put("http://semanticscience.org/resource/CHEMINF_000337", "monoisotopic mass");
		put("http://semanticscience.org/resource/CHEMINF_000338", "exact mass");
		put("http://semanticscience.org/resource/CHEMINF_000369", "covalent unit count");
		put("http://semanticscience.org/resource/CHEMINF_000370", "defined atom stereocenter count");
		put("http://semanticscience.org/resource/CHEMINF_000371", "defined bond stereocenter count");
		put("http://semanticscience.org/resource/CHEMINF_000372", "isotope atom count");
		put("http://semanticscience.org/resource/CHEMINF_000373", "heavy atom count");
		put("http://semanticscience.org/resource/CHEMINF_000374", "undefined atom stereocenter count");
		put("http://semanticscience.org/resource/CHEMINF_000375", "undefined bond stereocenter count");
		put("http://semanticscience.org/resource/CHEMINF_000376", "canonical smiles");
		put("http://semanticscience.org/resource/CHEMINF_000379", "isomeric SMILES");
		put("http://semanticscience.org/resource/CHEMINF_000382", "IUPAC Name");
		put("http://semanticscience.org/resource/CHEMINF_000387", "hydrogen bond donor count");
		put("http://semanticscience.org/resource/CHEMINF_000388", "hydrogen bond acceptor count");
		put("http://semanticscience.org/resource/CHEMINF_000389", "rotatable bond count");
		put("http://semanticscience.org/resource/CHEMINF_000390", "structure complexity");
		put("http://semanticscience.org/resource/CHEMINF_000391", "tautomer count");
		put("http://semanticscience.org/resource/CHEMINF_000392", "TPSA");
		put("http://semanticscience.org/resource/CHEMINF_000395", "XlogP3");
		put("http://semanticscience.org/resource/CHEMINF_000396", "InChI (1.0.4)");
		put("http://semanticscience.org/resource/CHEMINF_000399", "InChIKey (1.0.4)");

		// and also ignore often used things we like to ignore
		ignore("http://bio2rdf.org/obo_resource:term");
		ignore("http://bio2rdf.org/obo_resource:Term-chebi");
		ignore("http://www.w3.org/2002/07/owl#Thing");
		ignore("http://www.w3.org/2002/07/owl#Class");
		ignore("http://www.w3.org/2002/07/owl#NamedIndividual");
		ignore("http://umbel.org/umbel/rc/ChemicalCompoundTypeByChemicalSpecies");
		ignore("http://umbel.org/umbel#RefConcept");
		ignore("http://www4.wiwiss.fu-berlin.de/drugbank/vocab/resource/class/Offer");
		ignore("http://www.w3.org/2000/01/rdf-schema#Class");
		ignore("http://www.w3.org/2000/01/rdf-schema#Resource");
		ignore("http://www.opentox.org/api/1.1#Dataset");
	}
		public void ignore(String resource) {
			put(resource, null);
		}
	};

	private List<IPropertyExtractor> extractors = new ArrayList<IPropertyExtractor>() {
		private static final long serialVersionUID = 2825983879781792266L; {
		add(new RdfsExtractor());
		add(new DublinCoreExtractor());
		add(new FoafExtractor());
		add(new SkosExtractor());
		add(new ChemAxiomExtractor());
		add(new SioExtractor());
		add(new OpenMoleculesExtractor());
		add(new DBPediaExtractor());
		add(new FreebaseExtractor());
	}};
	private List<INextURIExtractor> spiders = new ArrayList<INextURIExtractor>() {
		private static final long serialVersionUID = 7089854109617759948L; {
		add(new OwlSameAsExtractor());
		add(new OwlEquivalentClassExtractor());
		add(new SkosExactMatchExtractor());
		add(new DBPediaMinter());
//		add(new OpenMoleculesMinter());
	}};
	
	Map<String,String> extraHeaders = new HashMap<String, String>() {
		private static final long serialVersionUID = 2825983879781792266L;
	{
	  put("Content-Type", "application/rdf+xml");
	  put("Accept", "application/rdf+xml"); // Both Accept and Content-Type are needed for PubChem 
	}};

    public String getManagerName() {
        return "isbjørn";
    }

    public void findInfo(IMolecule mol, IReturner<IRDFStore> returner, IProgressMonitor monitor)
    throws BioclipseException {
    	monitor.beginTask("Downloading RDF resources", 100);
    	ICDKMolecule cdkMol = cdk.asCDKMolecule(mol);
		String inchi = cdkMol.getInChI(Property.USE_CACHED_OR_CALCULATED);
		IcebearWorkload workload = new IcebearWorkload();
		workload.addNewURI("http://rdf.openmolecules.net/?" + inchi);
		inchi = inchi.replace("=1S/", "=1/");
		workload.addNewURI("http://rdf.openmolecules.net/?" + inchi);
		while (workload.hasMoreWork() && !monitor.isCanceled()) {
			findInfoForOneURI(workload, returner, monitor);
	    	monitor.worked(1);
		}
    }
    
    public void findInfo(String uri, IReturner<IRDFStore> returner, IProgressMonitor monitor)
    throws BioclipseException {
    	monitor.beginTask("Downloading RDF resources", 100);
		IcebearWorkload workload = new IcebearWorkload();
		workload.addNewURI(uri);
		while (workload.hasMoreWork() && !monitor.isCanceled()) {
			findInfoForOneURI(workload, returner, monitor);
	    	monitor.worked(1);
		}
    }
    
    public List<Entry> getProperties(IRDFStore store) throws BioclipseException, CoreException {
    	String resource = rdf.getForPredicate(store,
    		"http://www.bioclipse.org/PrimaryObject",
			"http://www.bioclipse.org/hasURI").get(0);
    	
		List<Entry> props = new ArrayList<Entry>();
		for (IPropertyExtractor extractor : extractors) {
			props.addAll(extractor.extractProperties(store, resource));
		}
		return props;
    }

    private void findInfoForOneURI(IcebearWorkload workload, IReturner<IRDFStore> returner, IProgressMonitor monitor) {
    	IRDFStore store = rdf.createInMemoryStore();
    	URI nextURI = workload.getNextURI();
		String nextURIString = nextURI.toString();
		monitor.subTask("Downloading " + nextURIString);
    	try {
			rdf.addObjectProperty(store,
				"http://www.bioclipse.org/PrimaryObject", "http://www.bioclipse.org/hasURI",
				nextURI.toString()
			);
			rdf.importURL(store, nextURIString, extraHeaders, monitor);
			System.out.println(rdf.asTurtle(store));
			for (INextURIExtractor spider : spiders) {
				for (String uri : spider.extractURIs(store, nextURI.toString())) {
					workload.addNewURI(uri);
				}
			}
		} catch (Exception exception) {
			logger.debug("Error while downloading " + nextURIString + ": " + exception.getMessage());
		}
    	returner.partialReturn(store);
    }
    
    public IFile saveAsHTML(List<IRDFStore> stores, IFile target, IProgressMonitor monitor) throws BioclipseException, CoreException {
    	if (monitor == null) monitor = new NullProgressMonitor();
    	monitor.beginTask("Saving stores as HTML", 100);
    	monitor.worked(1);

    	StringWriter writer = new StringWriter();
    	PrintWriter pWriter = new PrintWriter(writer);
    	
    	pWriter.println("<html>");
    	pWriter.println("  <head>");
    	pWriter.println("  <title>Isbjørn Report</title>");
    	pWriter.println("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
    	pWriter.println("  <style type=\"text/css\">");
    	pWriter.println("    body {");
    	pWriter.println("      font-family: Arial, Verdana, Sans-serif;");
    	pWriter.println("      a:link {color:black;} ");
    	pWriter.println("      a:hover {color:black; text-decoration:underline;} ");
    	pWriter.println("      a:visited {color:black;} ");
    	pWriter.println("    }");
    	pWriter.println("  </style>");
    	pWriter.println("  </head>");
    	pWriter.println("<body>");
    	pWriter.println("<h1>Isbjørn Report</h1>");
    	for (IRDFStore store : stores) {
    		List<String> objects = rdf.getForPredicate(
    			store, "http://www.bioclipse.org/PrimaryObject", "http://www.bioclipse.org/hasURI"
    		);
    		for (String primObject : objects) {
				try {
	    			URI uri = new URI(primObject);
	    			printFoundInformation(pWriter, store, uri);
				} catch (URISyntaxException e) {
					logger.debug("Unexpected primary object URIL " + e.getMessage());
				}
    		}
    	}
    	pWriter.println("<html>");
    	pWriter.println("</body>");
    	pWriter.println("</html>");
    	pWriter.flush();

    	try {
    		if (target.exists()) {
    			target.setContents(
                    new ByteArrayInputStream(writer.toString()
                        .getBytes("UTF-8")),
                        false,
                        true, // overwrite
                        monitor
                );
            } else {
            	target.create(
            		new ByteArrayInputStream(writer.toString()
            			.getBytes("UTF-8")),
           			false,
 					monitor
            	);
            }
    	} catch (Exception encodingExeption) {
    		throw new BioclipseException("Error encoding problem: " + encodingExeption.getMessage(), encodingExeption);
    	}
    	
    	return target;
    }

	private String stripDataType(String id) {
		if (id.contains("^^"))
			return id.substring(0, id.indexOf("^^"));
		return id;
	}

	private void printFoundInformation(PrintWriter pWriter, IRDFStore store, URI uri) {
		if (uri.getHost() == null) return; // ignore
		pWriter.println(
		  "<h2>" + uri.getHost() + " <a href=\""+ uri.toString() + "\">" +
		  "<img border=0 src=\"" + ICON + "\" /></a></h2>");
	    pWriter.println("<ul>");

	    List<Entry> properties;
		try {
			properties = getProperties(store);
		} catch (Exception e) {
			pWriter.println("</ul>");
			return;
		}
		// get the rdf:type's
		try {
			List<Entry> approvedTypes = new ArrayList<Entry>();
			for (Entry prop : properties)
				if (Fields.TYPE.equals(prop.predicateLabel)) approvedTypes.add(prop);
			// now output what we have left
			if (approvedTypes.size() > 0) {
				pWriter.append("<p>");
				pWriter.println("<b>Is a</b> ");
				StringBuffer buffer = new StringBuffer();
				for (Entry prop : approvedTypes) {
					String type = prop.predicate;
					String label = getLabelForResource(store, type, null);
					buffer.append(label).append(" <a href=\"").append(type)
						.append("\"><img src=\"").append(ICON ).append("\" /></a>, ");
				}
				String bufferStr = buffer.toString();
				pWriter.println(bufferStr.substring(0,bufferStr.length()-2));
				pWriter.append("</p>");
			}
		} catch (Throwable exception) {
			logger.warn("Error while quering for labels for " + uri, exception);
		}
		// get a description
		try {
			List<Entry> descriptions = new ArrayList<Entry>();
			for (Entry prop : properties)
				if (Fields.DESCRIPTION.equals(prop.predicateLabel)) descriptions.add(prop);
			if (descriptions.size() > 0) {
				pWriter.println("<b>Descriptions</b><br />");
				for (Entry prop : descriptions) {
					pWriter.append("<p>");
					pWriter.println(prop.predicateLabel);
					pWriter.append("</p>");
				}
			}
		} catch (Throwable exception) {
			logger.warn("Error while quering for descriptions for " + uri, exception);
		}
		// get visualizations
		try {
			List<Entry> depictions = new ArrayList<Entry>();
			for (Entry prop : properties)
				if (Fields.IMAGE.equals(prop.predicateLabel)) depictions.add(prop);
			if (depictions.size() > 0) {
				pWriter.append("<p>");
				for (Entry depiction : depictions) {
					pWriter.println("<img height=\"80\" src=\"" + depiction.object + "\" />");
				}
				pWriter.append("</p>");
			}
		} catch (Throwable exception) {
			logger.warn("Error while quering for images for " + uri, exception);
		}
		// get the identifiers
		try {
			List<Entry> identifiers = new ArrayList<Entry>();
			for (Entry prop : properties)
				if (Fields.IDENTIFIER.equals(prop.predicateLabel)) identifiers.add(prop);
			if (identifiers.size() > 0) {
				pWriter.println("<p>");
				pWriter.println("<b>Identifiers</b> ");
				StringBuffer idString = new StringBuffer();
				List<String> processedIdentifiers = new ArrayList<String>();
				for (Entry identifier : identifiers) {
					if (!processedIdentifiers.contains(identifier.object)) {
						idString.append(identifier).append(", ");
						processedIdentifiers.add(identifier.object);
					}
				}
				String fullString = idString.toString();
				pWriter.println(fullString.substring(0, fullString.length()-2));
				pWriter.println("</p>");
			}
		} catch (Throwable exception) {
			logger.warn("Error while quering for identifiers for " + uri, exception);
		}
		// get the labels
		try {
			List<Entry> labels = new ArrayList<Entry>();
			for (Entry prop : properties)
				if (Fields.LABEL.equals(prop.predicateLabel)) labels.add(prop);
			if (labels.size() > 0) {
				pWriter.println("<p>");
				pWriter.println("<b>Synonyms</b> ");
				StringBuffer labelString = new StringBuffer();
				for (Entry label : labels) {
					labelString.append(label.object).append(", ");
				}
				String fullString = labelString.toString();
				pWriter.println(fullString.substring(0, fullString.length()-2));
				pWriter.println("</p>");
			}
		} catch (Throwable exception) {
			logger.warn("Error while quering for labels for " + uri, exception);
		}
		// get the (home)pages
		try {
			List<Entry> homepages = new ArrayList<Entry>();
			for (Entry prop : properties)
				if (Fields.HOMEPAGE.equals(prop.predicateLabel)) homepages.add(prop);
			if (homepages.size() > 0) {
				pWriter.println("<b><a href=\"" + homepages.get(0) + "\">Homepage</a></b><br />");
			}
		} catch (Throwable exception) {
			logger.warn("Error while quering for web pages for " + uri, exception);
		}
		// all other properties
		List<Entry> theRest = new ArrayList<Entry>();
		for (Entry prop : properties) {
			if (!Fields.HOMEPAGE.equals(prop.predicateLabel) &&
				!Fields.LABEL.equals(prop.predicateLabel) &&
				!Fields.DESCRIPTION.equals(prop.predicateLabel) &&
				!Fields.IDENTIFIER.equals(prop.predicateLabel) &&
				!Fields.IMAGE.equals(prop.predicateLabel) &&
				!Fields.TYPE.equals(prop.predicateLabel))
			    theRest.add(prop);
		}
		pWriter.println("<table border='0'>");
		for (Entry key : theRest) {
			pWriter.println("  <tr>");
			String label = key.predicateLabel;
			System.out.println("predicateLabel: " + key.predicateLabel);
			System.out.println("predicate: " + key.predicate);
			if (resourceMap.containsKey(key.predicate)) {
				label = resourceMap.get(key.predicate);
			} else {
				logger.debug("No label for: " + key.predicate);
			}
			pWriter.println("    <td valign=\"top\"><b>" + label + "</b></td>");
			String property = stripDataType(key.object);
			pWriter.println("    <td valign=\"top\">" + property + "</td>");
			pWriter.println("  </tr>");
		}
		pWriter.println("</table>");

	    pWriter.println("</ul>");
	}

	private String getLabelForResource(IRDFStore currentStore, String resource, IProgressMonitor monitor) {
		if (resourceMap.containsKey(resource)) return resourceMap.get(resource);
		if (resource.startsWith("http://rdf.freebase.com/ns/")) return null; // ignore all of them which we did not accept specifically
		if (resource.startsWith("http://sw.opencyc.org")) return null;
		System.out.println("Needing a label for resource: " + resource);

		// try the current store first
		String label = getLabelFromStore(resource, currentStore);
		if (label != null) return label;
		
		try {
			URI uri = new URI(resource);
			IRDFStore store = rdf.createInMemoryStore();
			System.out.println("Getting a label online for resource: " + resource);
			rdf.importURL(store, uri.toString(), extraHeaders, monitor);
			System.out.println(rdf.asRDFN3(store)); // so that I can check what is there...
			return getLabelFromStore(resource, store);
		} catch (Throwable e) {
			logger.debug("Something went wrong with getting a label: " + e.getMessage(), e);
			resourceMap.put(resource, null); // I don't want to try again
			return null;
		}
	}

	private String getLabelFromStore(String resource, IRDFStore store) {
		List<String> labels = new ArrayList<String>();
		labels.addAll(getPredicate(store, resource, DC.title.toString()));
		labels.addAll(getPredicate(store, resource, DC_10.title.toString()));
		labels.addAll(getPredicate(store, resource, DC_11.title.toString()));
		labels.addAll(getPredicate(store, resource, RDFS.label.toString()));
		labels.addAll(getPredicate(store, resource, "http://www.w3.org/2004/02/skos/core#prefLabel"));
		labels.addAll(getPredicate(store, resource, "http://www.w3.org/2004/02/skos/core#altLabel"));
		
		if (labels.size() == 0) {
			resourceMap.put(resource, null); // don't try again
			return null; // OK, did not find anything suitable
		}
		// the first will do fine, but pick the first English one
		for (String label : labels) {
			logger.debug("Is this english? -> " + label);
			if (label.endsWith("@en")) {
				label = label.substring(0, label.indexOf("@en")); // remove the lang indication
				resourceMap.put(resource, label); // store it for later use
				return label;
			} else if (!label.contains("@")) {
				resourceMap.put(resource, label); // store it for later use
				return label;
			}
		}
		logger.debug("Did not find an English label :(");
		return labels.get(0); // no labels marked @en, so pick the first
	}

	private List<String> getPredicate(IRDFStore store, String resource, String predicate) {
		try {
			return rdf.getForPredicate(store, resource, predicate);
		} catch (Throwable e) {
			logger.debug("Error while getting value for " + predicate + ": " + e.getMessage(), e);
		};
		return Collections.emptyList();
	}
	
	class IcebearWorkload {
		
		Set<URI> todo = new HashSet<URI>();
		Set<URI> done = new HashSet<URI>();

		public boolean hasMoreWork() {
			System.out.println("work left todo: " + todo.size());
			return todo.size() != 0;
		}

		public URI getNextURI() {
			URI nextURI = todo.iterator().next();
			System.out.println("next URI: " + nextURI);
			todo.remove(nextURI);
			done.add(nextURI);
			return nextURI;
		}

		/**
		 * Returns false when the URI was already processed or is already scheduled.
		 */
		public boolean addNewURI(String newURI) {
			System.out.println("Adding URI: " + newURI);
			try {
				URI uri = new URI(newURI);
				if (done.contains(uri) || todo.contains(uri)) {
					System.out.println("Already got it...");
					return false;
				}

				todo.add(uri);
				return true;
			} catch (URISyntaxException e) {
				System.out.println("Failed to add the new URI: " + e.getMessage());
				return false;
			}
		}
	}
}
