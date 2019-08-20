package org.catalogus.professorum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dbtrends.Entity;
import org.dbtrends.Knowledgebase;

public class EntityRankLink {
	public static void main(String[] args) throws Exception {
		java.lang.Class.forName("org.sqlite.JDBC");
		Set<String> urls = new HashSet<String>();
		List<Entity> entities = new ArrayList<Entity>();
		try (BufferedReader br = new BufferedReader(
				new FileReader("pcpDBpediaResources.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	URI url = new URI(line);
		    	if(urls.contains(url.toASCIIString()) || line.contains("niversity")) {
		    		continue;
		    	}
			    urls.add(url.toASCIIString());
		    	Entity e = Knowledgebase.DBpedia39.getEntity(url.toASCIIString());
		    	if(e != null) {
		    		entities.add(e);
		    		System.out.println(
		    				url.toASCIIString() + " | pageRank:" + e.getDatabaseRank()
			    		+ " pageIn:" + e.getPageInDegree()
			    		+ " pageOut:" + e.getPageOutDegree()
			    		+ " rIn:" + e.getResourceInDegree()
			    		+ " rOut:" + e.getResourceOutDegree());
		    	} else {
		    		System.out.println("Not found:" + url.toASCIIString());
		    	}
		    }
		}
		
		Knowledgebase.sortEntities(entities, Entity.Comparator.DBIN);
		File dbin = new File("dbin.txt");
		try(PrintStream ps = new PrintStream(dbin)) {
			for(Entity e : entities) {
				ps.println(e.getURI() + " " + e.getResourceInDegree());
			}
		}
		
		Knowledgebase.sortEntities(entities, Entity.Comparator.DBOUT);
		File dbout = new File("dbout.txt");
		try(PrintStream ps = new PrintStream(dbout)) {
			for(Entity e : entities) {
				ps.println(e.getURI() + " " + e.getResourceOutDegree());
			}
		}
		
		Knowledgebase.sortEntities(entities, Entity.Comparator.PAGEIN);
		File wikiIn = new File("wikiIn.txt");
		try(PrintStream ps = new PrintStream(wikiIn)) {
			for(Entity e : entities) {
				ps.println(e.getURI() + " " + e.getPageInDegree());
			}
		}
		
		Knowledgebase.sortEntities(entities, Entity.Comparator.PAGEOUT);
		File wikiOut = new File("wikiOut.txt");
		try(PrintStream ps = new PrintStream(wikiOut)) {
			for(Entity e : entities) {
				ps.println(e.getURI() + " " + e.getPageOutDegree());
			}
		}
		
		Knowledgebase.sortEntities(entities, Entity.Comparator.DBRANK);
		File pageRank = new File("pageRank.txt");
		try(PrintStream ps = new PrintStream(pageRank)) {
			for(Entity e : entities) {
				ps.println(e.getURI() + " " + e.getDatabaseRank());
			}
		}
	}
}