package org.catalogus.professorum;

import java.io.File;
import java.net.URL;

public class DBpediaScholarExtractorFusion {
	
	
	public static void main(String[] args) throws Exception {
		
		URL[] kbs = new URL[]{new URL("http://purl.org/pcp-on-web/ontology?version=0"), 
				new URL("http://purl.org/pcp-on-web/dataset?version=0")};
		
		String pcpGNDSPARQL = "Select ?gnd where { "
				+ "?pcpProfessor <http://purl.org/pcp-on-web/ontology#gnd> ?gnd.}";
		
		GNDListRecover recover = new GNDListRecover(pcpGNDSPARQL, "?gnd", kbs);
		
		String dbpediaSPARQL = "Select ?dbpediaProfessor ?wp ?wo where {"
				+ " ?dbpediaProfessor <http://www.w3.org/2002/07/owl#sameAs> ?gndWiki. " 
				+ " ?dbpediaProfessor ?wp ?wo. " 
				+ " FILTER((?gndWiki = <http://d-nb.info/gnd/?gndValue>) || (?gndWiki = <?gndValue>)) }";

		LazyExtractor lazyExtractor = new LazyExtractor("http://dbpedia.org/sparql", 
				dbpediaSPARQL, "?gndValue", new File("dbpediaExtraction.rdf"));
		
		recover.accept(lazyExtractor);
		
	}
}
