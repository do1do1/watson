package watson;

//Author: Justin Do
//Class: CSC483
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;


public class watson {
	
	Directory index;
	static StandardAnalyzer analyzer;
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
	
	public void buildIndexStem() {
		
	}
	
	public void buildIndexLem() {
		final File folder = new File("resources");
		analyzer = new StandardAnalyzer();
        try {
			index = FSDirectory.open(indexFile.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document;
        
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
		            				document = pipeline.processToCoreDocument(text.toString());
		            	        	pipeline.annotate(document);
		            				Document doc = new Document();
		            				
			            			doc.add(new TextField("text", document.text(),Field.Store.YES));
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
		System.out.println("Welcome to watson. Please select your mode: Normal, Lem, Stem");
		Scanner readerIn = new Scanner(System.in);
		String typing = readerIn.nextLine();
		if(typing.toLowerCase().equals("normal")) {
			indexFile = new File("resources/indexFile");

		} else if(typing.toLowerCase().equals("lem")) {
			indexFile = new File("resources/indexFileLem");
			
		} else if(typing.toLowerCase().equals("stem")) {
			indexFile = new File("resources/indexFileStem");
			
		} else {
			System.out.println("Invalid typing, goodbye.");
			readerIn.close();
			System.exit(0);
		}
		
		indexExists = indexFile.exists();
		if(indexExists == false) {
			System.out.println("Building index.");
			watson indexBuild = new watson();
			if(typing.toLowerCase().equals("normal")) {
				indexBuild.buildIndex();
			} else if(typing.toLowerCase().equals("lem")) {
				indexBuild.buildIndexLem();
			} else if(typing.toLowerCase().equals("stem")) {
				indexBuild.buildIndexStem();
			}
		} else {
			Directory openIndex;
			IndexReader reader;
			IndexSearcher searcher = null;
			Query q = null;
			File testingFile = new File("testingMat/questions.txt");
			Scanner testFile = null;
			//open scanner
			try {
				testFile = new Scanner(testingFile);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//open index and initialize tools
			try {
				openIndex = FSDirectory.open(indexFile.toPath());
				reader = DirectoryReader.open(openIndex);
				searcher = new IndexSearcher(reader);
				
				analyzer = new StandardAnalyzer();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double score = 0;
			int precision1 = 0;
			while(testFile.hasNextLine()) {
				String line1 = testFile.nextLine(); //category, this will unfortunately go unused
				String line2 = testFile.nextLine(); //query
				String line3 = testFile.nextLine(); //answer
				//line2.replaceAll("[^a-zA-Z0-9]", "");
				//change to user query tomorrow.
				try {
					q = new QueryParser("text", analyzer).parse(QueryParser.escape(line1 + " " + line2));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int hitsPerPage = 10;
		        TopDocs docs = null;
				try {
					docs = searcher.search(q, hitsPerPage);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //it is naturally sorted in descending order
		        ScoreDoc[] hits = docs.scoreDocs; 
		        for(int i=0;i<hits.length;++i) {
		            int docId = hits[i].doc;
		           
		            Document d = null;
					try {
						d = searcher.doc(docId);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(d.get("docid").equals(line3)) {
						if(i == 0) {

							precision1 += 1;
						}

						score = score + (double) 1/(i+1);
					}
					
		            
		        }
				
				if(testFile.hasNextLine()) { //edge case for end of file zzz
					testFile.nextLine(); //blank line
				}
				
			}
			System.out.println("MRR: " + score/100 + " P@1: " + precision1);
			
		}
		readerIn.close();
		
		
	}

}
