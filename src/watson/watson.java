package watson;

//Author: Justin Do
//Class: CSC483
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class watson {
	
	Directory index;
	StandardAnalyzer analyzer;
	IndexWriterConfig config;
	static File indexFile;
	static boolean indexExists=false;
	
	
	public watson() {
		
	}
	
	public void buildIndex() {
		final File folder = new File("resources");
		analyzer = new StandardAnalyzer();
        try {
			index = FSDirectory.open(indexFile.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (final File fileEntry : folder.listFiles()) {
			File tempFile = new File("resources/" + fileEntry.getName());
	        try {
	        	BufferedReader inputScanner = new BufferedReader(new FileReader(tempFile));
	        	config = new IndexWriterConfig(analyzer);
	        	IndexWriter w = new IndexWriter(index, config);
	        	
	        	StringBuilder line = new StringBuilder();
	        	StringBuilder docID = new StringBuilder();
	        	StringBuilder text = new StringBuilder();
	        	String helper = "";
	            while ((helper = inputScanner.readLine()) != null) {
	            	line.append(helper);
	            	
	            	if(line.length() != 0) {
	            		switch(line.charAt(0)) {
		            		case '[':
		            			if(line.length() >= 7) {
		            				if(line.subSequence(0, 7).equals("[[File:")) {
		            					break;
			            				
			            			}
		            			} 
		            			if(line.length() >= 8) {
		            				if(line.subSequence(0, 8).equals("[[Image:")) {
			            				break;
			            			}
		            			}
		            			if(line.length() <= 1) {
		            				break;
		            			}
		            			if(line.charAt(1) == '[') {
		            				Document doc = new Document();
			            			doc.add(new TextField("text", text.toString(),Field.Store.YES));
			                    	doc.add(new StringField("docid", docID.toString(), Field.Store.YES));
			                    	w.addDocument(doc);
			                    	text.delete(0, text.length());
			                    	docID.delete(0, docID.length());
			                    	docID.append(line.subSequence(2, line.length()-2));
			                    	System.out.println("New Doc ID: " + line.subSequence(2, line.length()-2));
		            			}
		            			
		            			
		            			
		                    	
		            		case '=':
		            			if(line.length() > 4) {
		            				text.append(line.subSequence(2, line.length() - 2));
		            			}
		            			

		            		default:
		            			text.append(line);
		            	}
	            		
	            	}
	            	line.delete(0, line.length());
	            	
	            }
	            Document doc = new Document();
				doc.add(new TextField("text", text.toString(),Field.Store.YES));
	        	doc.add(new StringField("docid", docID.toString(), Field.Store.YES));
	            inputScanner.close();
	            w.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	       
			//System.out.println(fileEntry.getName());
	    }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		indexFile = new File("resources/indexFile");
		indexExists = indexFile.exists();
		if(indexExists == false) {
			System.out.println("here");
			watson indexBuild = new watson();
			indexBuild.buildIndex();
		} else {
			Directory openIndex;
			IndexReader reader;
			IndexSearcher searcher;
			try {
				openIndex = FSDirectory.open(indexFile.toPath());
				reader = DirectoryReader.open(openIndex);
				searcher = new IndexSearcher(reader);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

}
