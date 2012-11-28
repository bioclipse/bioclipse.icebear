package net.bioclipse.icebear.extractors;

import java.util.List;

import net.bioclipse.rdf.business.IRDFStore;

public interface INextURIExtractor {

	public List<String> extractURIs(IRDFStore store, String resource);

}
