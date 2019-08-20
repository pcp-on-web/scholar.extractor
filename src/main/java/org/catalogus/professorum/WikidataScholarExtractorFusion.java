package org.catalogus.professorum;

import java.io.File;
import java.net.URL;

public class WikidataScholarExtractorFusion {	
	public static void main(String[] args) throws Exception {
		URL[] kbs = new URL[]{new URL("http://purl.org/pcp-on-web/ontology?version=0"), 
				new URL("http://purl.org/pcp-on-web/dataset?version=0")};
		String pcpGNDSPARQL = "Select ?gnd where { "
				+ "?pcpProfessor <http://purl.org/pcp-on-web/ontology#gnd> ?gnd.}";
		GNDListRecover recover = new GNDListRecover(pcpGNDSPARQL, "?gnd", kbs);
		String wikidataSPARQL = "Select ?wikidataProfessor ?wp ?wo where {"
				+ " ?wikidataProfessor <http://www.wikidata.org/prop/direct-normalized/P227> ?gndWiki. " 
				+ " ?wikidataProfessor ?wp ?wo. " 
				+ " FILTER((?gndWiki = <http://d-nb.info/gnd/?gndValue>) || (?gndWiki = <?gndValue>)) }";		

		LazyExtractor lazyExtractor = new LazyExtractor("http://query.wikidata.org/sparql", 
				wikidataSPARQL, "?gndValue", new File("wikidataExtraction.2.rdf"));

		recover.accept(lazyExtractor);
	}
}