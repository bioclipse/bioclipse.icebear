/*******************************************************************************
 * Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.icebear.business;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.bioclipse.business.BioclipsePlatformManager;
import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.domain.IMolecule.Property;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.rdf.business.IJenaStore;
import net.bioclipse.rdf.business.IRDFStore;
import net.bioclipse.rdf.business.RDFManager;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DC_10;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class IcebearManager implements IBioclipseManager {

	private static final Logger logger = Logger.getLogger(IcebearManager.class);

	private CDKManager cdk = new CDKManager();
	private RDFManager rdf = new RDFManager();
	private BioclipsePlatformManager bioclipse = new BioclipsePlatformManager();

    public String getManagerName() {
        return "isbjørn";
    }

    public IFile findInfo( IMolecule mol, IFile target, IProgressMonitor monitor) throws BioclipseException, CoreException {
    	if (!bioclipse.isOnline())
    		throw new BioclipseException("Searching information on the web requires an active internet connection.");

    	if (monitor == null) monitor = new NullProgressMonitor();
    	monitor.beginTask("Downloading RDF resources", 100);
    	monitor.worked(1);

    	StringWriter writer = new StringWriter();
    	PrintWriter pWriter = new PrintWriter(writer);
    	
    	pWriter.println("<html>");
    	pWriter.println("  <head>");
    	pWriter.println("  <title>Isbjørn Report</title>");
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
    	// now, the next should of course use an extension point, but this will have to do for now...
    	useIcebearPowers(mol, pWriter, null, monitor);
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

	private void useIcebearPowers(IMolecule mol, PrintWriter pWriter, List<String> alreadyDone, IProgressMonitor monitor)
	throws BioclipseException, CoreException {
		if (alreadyDone == null) alreadyDone = new ArrayList<String>();
		// so, what are the isbjørn powers then?
		// 1. use the InChI to get URIs
		ICDKMolecule cdkMol = cdk.asCDKMolecule(mol);
		String inchi = cdkMol.getInChI(Property.USE_CACHED_OR_CALCULATED);
		try {
			URI ronURI = new URI("http://rdf.openmolecules.net/?" + inchi);
			useUniveRsalIcebearPowers(pWriter, ronURI, alreadyDone, monitor);
		} catch (URISyntaxException exception) {
			throw new BioclipseException("Something wrong with the URI: " + exception.getMessage(), exception);
		}
		// 2. also do the non-standard InChI
		inchi = inchi.replace("=1S/", "=1/");
		try {
			URI ronURI = new URI("http://rdf.openmolecules.net/?" + inchi);
			useUniveRsalIcebearPowers(pWriter, ronURI, alreadyDone, monitor);
		} catch (URISyntaxException exception) {
			throw new BioclipseException("Something wrong with the URI: " + exception.getMessage(), exception);
		}
	}

	private void useUniveRsalIcebearPowers(PrintWriter pWriter, URI uri, List<String> alreadyDone, IProgressMonitor monitor) {
		alreadyDone.add(uri.toString());
		monitor.setTaskName("Downloading " + uri.toString());
		IRDFStore store = rdf.createInMemoryStore();
		pWriter.println("<h2>" + uri.getHost() +"</h2>");
		pWriter.println("<p><a href=\""+ uri.toString() + "\">" + uri.toString() + "</a></p>");
		pWriter.println("<ul>");
		try {
			rdf.importURL(store, uri.toString(), monitor);
			System.out.println(rdf.asRDFN3(store)); // so that I can check what is there...
			printFoundInformation(pWriter, store, uri);
			// and recurse: owl:sameAs
			List<String> sameResources = rdf.allOwlSameAs(store, uri.toString());
			for (String sameResource : sameResources) {
				if (!alreadyDone.contains(sameResource)) {
					try {
						URI sameURI = new URI(sameResource);
						useUniveRsalIcebearPowers(pWriter, sameURI, alreadyDone, monitor);	
					} catch (URISyntaxException exception) {
						// ignore resource
					}
				}
			}
		} catch (Throwable exception) {
			logger.warn("Something wrong during IO for " + uri.toString() + ": " + exception.getMessage());
		}
		pWriter.println("</ul>");
		monitor.worked(1);
	}

	private void printFoundInformation(PrintWriter pWriter, IRDFStore store, URI ronURI) {
		// get the rdf:type's
		try {
			List<String> types = rdf.getForPredicate(store, ronURI.toString(), RDF.type.toString());
			types.addAll(rdf.getForPredicate(store, ronURI.toString(), RDFS.subClassOf.toString()));
			if (types.size() > 0) {
				pWriter.append("<p>");
				pWriter.println("<b>Types</b> ");
				StringBuffer buffer = new StringBuffer();
				for (String type : types) {
					Resource resource = ((IJenaStore)store).getModel().createResource(type);
					buffer.append("<a href=\"").append(type).append("\">").
					    append(resource.getLocalName()).append("</a>, ");
				}
				String bufferStr = buffer.toString();
				pWriter.println(bufferStr.substring(0,bufferStr.length()-2));
				pWriter.append("</p>");
			}
		} catch (Exception exception) {
			logger.warn("Error while quering for labels for " + ronURI, exception);
		}
		// get a description
		try {
			List<String> descriptions = rdf.getForPredicate(store, ronURI.toString(), DC.description.toString());
			descriptions.addAll(rdf.getForPredicate(store, ronURI.toString(), DC_10.description.toString()));
			descriptions.addAll(rdf.getForPredicate(store, ronURI.toString(), DC_11.description.toString()));
			if (descriptions.size() > 0) {
				pWriter.println("<b>Descriptions</b><br />");
				for (String desc : descriptions) {
					pWriter.append("<p>");
					pWriter.println(desc);
					pWriter.append("</p>");
				}
			}
		} catch (Exception exception) {
			logger.warn("Error while quering for labels for " + ronURI, exception);
		}
		// get visualizations
		try {
			List<String> depictions = rdf.getForPredicate(store, ronURI.toString(), FOAF.depiction.toString());
			if (depictions.size() > 0) {
				pWriter.append("<p>");
				for (String depiction : depictions) {
					pWriter.println("<img height=\"80\" src=\"" + depiction + "\" />");
				}
				pWriter.append("</p>");
			}
		} catch (Exception exception) {
			logger.warn("Error while quering for labels for " + ronURI, exception);
		}
		// get the labels
		try {
			List<String> labels = rdf.getForPredicate(store, ronURI.toString(), RDFS.label.toString());
			if (labels.size() > 0) {
				pWriter.println("<p>");
				pWriter.println("<b>Synonyms</b> ");
				StringBuffer labelString = new StringBuffer();
				for (String label : labels) {
					labelString.append(label).append(", ");
				}
				String fullString = labelString.toString();
				pWriter.println(fullString.substring(0, fullString.length()-2));
				pWriter.println("</p>");
			}
		} catch (Exception exception) {
			logger.warn("Error while quering for labels for " + ronURI, exception);
		}
		// get the (home)pages
		try {
			List<String> homepages = rdf.getForPredicate(store, ronURI.toString(), FOAF.homepage.toString());
			if (homepages.size() > 0) {
				pWriter.println("<b><a href=\"" + homepages.get(0) + "\">Homepage</a></b><br />");
			}
		} catch (Exception exception) {
			logger.warn("Error while quering for labels for " + ronURI, exception);
		}
		try {
			List<String> homepages = rdf.getForPredicate(store, ronURI.toString(), FOAF.page.toString());
			if (homepages.size() > 0) {
				pWriter.println("<b><a href=\"" + homepages.get(0) + "\">Homepage</a></b><br />");
			}
		} catch (Exception exception) {
			logger.warn("Error while quering for labels for " + ronURI, exception);
		}
	}
}
