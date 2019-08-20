package org.catalogus.professorum;

import java.net.URL;

import org.aksw.kbox.kibe.KBox;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class GNDListRecover {
	
	private String gndVariable;
	private String sparql;
	private URL[] graphs;
	
	public GNDListRecover(String sparql, String gndVariable, URL... graphs) {
		this.gndVariable = gndVariable;
		this.sparql = sparql;
		this.graphs = graphs;
	}
	
	public void accept(GNDListVisitor visitor) throws Exception {
		ResultSet result = KBox.query(sparql, true, graphs);
		while(result.hasNext()) {
			QuerySolution qs = result.next();
			String gnd = qs.get(gndVariable).toString();
			visitor.visit(gnd);
		}
	}
}
