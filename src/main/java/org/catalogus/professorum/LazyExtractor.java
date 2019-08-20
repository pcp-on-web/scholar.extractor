package org.catalogus.professorum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;

public class LazyExtractor implements GNDListVisitor {
	
	private String sparql;
	private String endpoint;
	private String variableTemplate;
	private PrintStream ps;
	
	public LazyExtractor(String endpoint,
			String sparql,
			String variableTemplate,
			File destFile) throws FileNotFoundException {
		this.sparql = sparql;
		this.endpoint = endpoint;
		this.variableTemplate = variableTemplate;
		this.ps = new PrintStream(destFile);
	}

	@Override
	public void visit(String gnd) throws Exception {
		
		gnd = gnd.replace("\n", "").replace("\r", ""); // removing line breaks
		gnd = gnd.trim();
		gnd = gnd.replaceAll("\\s", ""); // removing whitespace
		
		if(gnd.equals("http://d-nb.info/gnd/")) {
			return; // Skipping generic/malformed GND
		}
		
		String finalSPARQL = sparql.replace(variableTemplate, gnd);
		System.out.println("Processing GND: " + gnd + " SPARQL: " + finalSPARQL);
		try(QueryExecution qe = QueryExecutionFactory.sparqlService(
				endpoint, finalSPARQL)) {
			ResultSet rs = qe.execSelect();
			Model model = ModelFactory.createDefaultModel();
			while(rs.hasNext()) {
				QuerySolution qs = rs.next();
				RDFNode subject = null,predicate = null,object = null;
				for(String var : rs.getResultVars()) {
					RDFNode node = qs.get(var);
					if(subject == null) {
						subject = node;
					} else if(predicate == null) {
						predicate = node;
					} else {
						object = node;
					}
				}
				model.add(subject.asResource(), ResourceFactory.createProperty(predicate.toString()), object);
			} 
			RDFDataMgr.write(ps, model, org.apache.jena.riot.Lang.NT);
			model.removeAll();
			model.close();
			ps.flush();
		} catch (Exception e) {
			System.out.println("Error processing GND: " + gnd);
		}
	}
}
