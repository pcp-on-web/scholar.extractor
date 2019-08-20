package org.catalogus.professorum;

import java.io.PrintStream;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

public class DNBExtractor {
	private static PrintStream errorPS;

	public static void main(String[] args) throws Exception {		
		URL[] kbs = new URL[]{new URL("http://purl.org/pcp-on-web/ontology?version=0"), 
				new URL("http://purl.org/pcp-on-web/dataset?version=0")};
		
		PrintStream ps = new PrintStream("deutscheNationalBibliothekExtraction.rdf");
		errorPS = new PrintStream("deutscheNationalBibliothekExtraction.error");
		
		String pcpGNDSPARQL = "Select ?gnd where { "
				+ "?pcpProfessor <http://purl.org/pcp-on-web/ontology#gnd> ?gnd.}";
		
		GNDListRecover recover = new GNDListRecover(pcpGNDSPARQL, "?gnd", kbs);
		
		Model model = ModelFactory.createDefaultModel();
		
		
		recover.accept(new GNDListVisitor() {
			
			@Override
			public void visit(String gnd) throws Exception {
				
				gnd = gnd.replace("\n", "").replace("\r", ""); // removing line breaks
				gnd = gnd.trim();
				gnd = gnd.replaceAll("\\s", ""); // removing whitespace
				
				if(!gnd.contains("http://")) { // if it is only the number
					gnd = "http://d-nb.info/gnd/" + gnd;
				}
				
				gnd = gnd + "/about/lds";
				System.out.println("Extracting GND:" + gnd);
				try {
					RDFDataMgr.read(model, gnd) ;
				} catch (Exception e) {
					errorPS.println(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		RDFDataMgr.write(ps, model, org.apache.jena.riot.Lang.NT);
		model.close();
	}
}
