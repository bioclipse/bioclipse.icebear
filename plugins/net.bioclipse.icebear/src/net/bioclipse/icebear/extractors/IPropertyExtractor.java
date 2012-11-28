package net.bioclipse.icebear.extractors;

import java.util.List;

import net.bioclipse.icebear.business.Entry;
import net.bioclipse.rdf.business.IRDFStore;

public interface IPropertyExtractor {

	public List<Entry> extractProperties(IRDFStore store, String resource);

}
