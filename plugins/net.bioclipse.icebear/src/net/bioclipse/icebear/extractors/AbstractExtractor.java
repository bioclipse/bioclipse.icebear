package net.bioclipse.icebear.extractors;

import java.util.Collections;
import java.util.List;

import net.bioclipse.rdf.business.IRDFStore;
import net.bioclipse.rdf.business.RDFManager;

public class AbstractExtractor {

	private RDFManager rdf = new RDFManager();

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
	
}
