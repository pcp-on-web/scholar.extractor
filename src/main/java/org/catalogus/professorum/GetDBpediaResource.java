package org.catalogus.professorum;

import java.io.PrintStream;
import java.net.URL;

public class GetDBpediaResource {

	public static void main(String[] args) throws Exception {
		
		java.lang.Class.forName("org.sqlite.JDBC");
		
		URL[] kbs = new URL[]{new URL("http://purl.org/pcp-on-web/ontology?version=0"), 
				new URL("http://purl.org/pcp-on-web/dataset?version=0"), 
				new URL("http://purl.org/pcp-on-web/dbpedia?version=latest")};
		
		PrintStream ps = new PrintStream("pcpDBpediaResources.txt");
		
		String pcpGNDSPARQL = "Select distinct ?dbpediaProfessor where { "
				+ "?pcpProfessor <http://purl.org/pcp-on-web/ontology#gnd> ?gnd."
				+ "?pcpProfessor <http://purl.org/pcp-on-web/ontology#hasPeriod> ?period. "
				+ "?period <http://purl.org/pcp-on-web/ontology#periodBody> ?facultyPeriodBody. "
				+ "?facultyPeriodBody <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/pcp-on-web/ontology#Faculty>. "
				+ "optional {?period <http://purl.org/pcp-on-web/ontology#from> ?date.} "
				+ "?dbpediaProfessor <http://www.w3.org/2002/07/owl#sameAs> ?gnd2."
				+ "FILTER ((IRI(?gnd) = ?gnd2) || (?gnd2 = IRI(CONCAT('http://d-nb.info/gnd/', str(?gnd)))))"
				+ "FILTER (1500<=year(?date) && year(?date)<=1810) "
				+ " }"; 
		GNDListRecover recover = new GNDListRecover(pcpGNDSPARQL, "?dbpediaProfessor", kbs);
	
		recover.accept(new GNDListVisitor() {
			@Override
			public void visit(String professor) throws Exception {
				ps.println(professor);
			}
		});
	}
}
