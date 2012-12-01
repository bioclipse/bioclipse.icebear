package net.bioclipse.icebear.extractors.links;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.icebear.extractors.AbstractPropertyExtractor;
import net.bioclipse.icebear.extractors.INextURIExtractor;
import net.bioclipse.rdf.business.IRDFStore;

public class OwlSameAsExtractor extends AbstractPropertyExtractor implements INextURIExtractor {

	@Override
	public List<String> extractURIs(IRDFStore store, String resource) {
		List<String> sameResources = allOwlSameAs(store, resource);
		List<String> properResources = new ArrayList<String>();
		for (String sameResource : sameResources) {
			if (!sameResource.contains("dbpedia.org")) properResources.add(sameResource);
		}
		return properResources;
	}


}
