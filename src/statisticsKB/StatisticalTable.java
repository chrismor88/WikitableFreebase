package statisticsKB;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import bean.StatisticalKB_Bean;
import bean.WikipediaTable;




public class StatisticalTable{

	private final String PREFIX_URL = "en.wikipedia.org/wiki/";

	private BufferedWriter writerResultsFile;

	public StatisticalTable(BufferedWriter writerResultsFile) {
		this.writerResultsFile = writerResultsFile;

	}


	public synchronized void writeStatisticsAbout(WikipediaTable wikiTable, Map<String, Integer> relationToOccurrence) throws IOException {
		int significant_columns = wikiTable.getRows().get(0).split("\t").length;

		String record = wikiTable.getIdTable()+"\t"+wikiTable.getWikid()+"\t";

		String pairs = "";
		int counterPairs = 0;
		for(int i=0; i<wikiTable.getRows().size() && counterPairs < 3;i++,counterPairs++){
			String[] entities = wikiTable.getRows().get(i).split("\t");
			pairs = pairs.concat("<"+entities[0]+" , "+entities[1]+">  ");
		}

		record = record.concat(pairs+"\t"+PREFIX_URL+wikiTable.getWikid()+"\t"+
				wikiTable.getNrows()+"\t"+wikiTable.getNcols()+"\t"+wikiTable.getRows().size()+"\t"+
				significant_columns+"\t");



		int number_of_N_A_relations = relationToOccurrence.get("N/A");
		relationToOccurrence.remove("N/A");


		if(relationToOccurrence.size() == 0){
			int k = 0;
			while(k<3){
				record = record.concat("N/A"+"\t"+"-"+"\t");
				k++;
			}
		}

		else{
			int counterRelations = 0;
			for(String relation : relationToOccurrence.keySet()){
				record = record.concat(relation+"\t"+relationToOccurrence.get(relation)+"\t");
				counterRelations ++;
			}

			while(counterRelations < 3){
				record = record.concat("-"+"\t"+"-"+"\t");
				counterRelations ++;
			}
		}

		record = record.concat(number_of_N_A_relations+"\t");
		record = record.concat(wikiTable.getNrows_with_multiple_mentions()+"\n");

		writerResultsFile.write(record);

	}


	public void writeStatisticsAbout(WikipediaTable wikiTable, StatisticalKB_Bean statisticalResultForThesePairs) throws IOException {
		int significant_columns = wikiTable.getRows().get(0).split("\t").length;

		String record = wikiTable.getIdTable()+"\t"+wikiTable.getWikid()+"\t";

		int mid1_found_counter = statisticalResultForThesePairs.getMid1_found_counter();
		int mid2_found_counter = statisticalResultForThesePairs.getMid2_found_counter();
		
		String pairs = "";
		int counterPairs = 0;
		for(int i=0; i<wikiTable.getRows().size() && counterPairs < 3;i++,counterPairs++){
			String[] entities = wikiTable.getRows().get(i).split("\t");
			pairs = pairs.concat("<"+entities[0]+" , "+entities[1]+">  ");
		}

		record = record.concat(pairs+"\t"+PREFIX_URL+wikiTable.getWikid()+"\t"+
				wikiTable.getNrows()+"\t"+wikiTable.getNcols()+"\t"+wikiTable.getRows().size()+"\t"+
				significant_columns+"\t"+mid1_found_counter+"\t"+mid2_found_counter+"\t");


		Map<String,Integer> relationToOccurrence = statisticalResultForThesePairs.getRelationTypeToOccurrence();
		int number_of_N_A_relations = relationToOccurrence.get("N/A");
		relationToOccurrence.remove("N/A");


		if(relationToOccurrence.size() == 0){
			int k = 0;
			while(k<3){
				record = record.concat("N/A"+"\t"+"-"+"\t");
				k++;
			}
		}

		else{
			int counterRelations = 0;
			for(String relation : relationToOccurrence.keySet()){
				record = record.concat(relation+"\t"+relationToOccurrence.get(relation)+"\t");
				counterRelations ++;
			}

			while(counterRelations < 3){
				record = record.concat("-"+"\t"+"-"+"\t");
				counterRelations ++;
			}
		}

		record = record.concat(number_of_N_A_relations+"\t");
		record = record.concat(wikiTable.getNrows_with_multiple_mentions()+"\n");

		writerResultsFile.write(record);

		
		
		
	}
	
	
	
	
	

}
