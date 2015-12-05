package statisticsKB;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bean.StatisticalKB_Bean;
import lucene.SearcherEntryKB;
import lucene.SearcherEntryMapped;
import lucene.SearcherRedirect;
import util.ReaderFileConfig;

public class SearcherForRelations {
/*
	public static void main(String[] args) {
		String pair1 = "Valencia,_Trinidad_and_Tobago	Sangre_Grande_Regional_Corporation";
		String pair2 = "Valsayn	Tunapuna-Piarco_Regional_Corporation";
		String pair3 = "Vega_de_Oropouche	Sangre_Grande_Regional_Corporation";
		String pair4 = "Vessigny	Siparia_Regional_Corporation";
		String pair5 = "Vistabella_River	San_Fernando,_Trinidad_and_Tobago";
		
		
		SearcherForRelations searcher = new SearcherForRelations();
		List<String> pairs = new ArrayList<String>();
		pairs.add(pair1);
		pairs.add(pair2);
		pairs.add(pair3);
		pairs.add(pair4);
		pairs.add(pair5);
		System.out.println(searcher.findRelationsForThesePairs(pairs));



	}
*/
	private SearcherEntryMapped searcherEntryMapped;
	private SearcherRedirect searcherRedirect;
	private SearcherEntryKB searcherKB;



	public SearcherForRelations(ReaderFileConfig readerFileConfig) {
		try {
			this.searcherEntryMapped = new SearcherEntryMapped(readerFileConfig);
			this.searcherKB = new SearcherEntryKB(readerFileConfig);
			this.searcherRedirect = new SearcherRedirect(readerFileConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SearcherForRelations() {
		// TODO Auto-generated constructor stub
	}

	//restituisce la lista di relazioni trovate per le coppie analizzate con il relativo numero di occorrenza
	public synchronized StatisticalKB_Bean findRelationsForThesePairs(List<String> pairs){

		Map<String,Integer> relationToOccurrence = new HashMap<String,Integer>();
		int not_assigned_counter = 0;
		int mid1_found_counter = 0;
		int mid2_found_counter = 0;

		for(String pair : pairs){
//			System.out.println(pair);
			String[] entities = pair.split("\t");
			String subject = entities[0];
			String object = entities[1];

			String mid1 = findMidForThisEntity(subject);
			if(mid1!=null)
				mid1_found_counter++;
			
			String mid2 = findMidForThisEntity(object);
			if(mid2!=null)
				mid2_found_counter++;
			
			try {
				if(mid1!=null && mid2!=null){
					mid1 = "m."+mid1;
					mid2 = "m."+mid2;

					List<String> relations = this.searcherKB.findRelationsBetween(mid1, mid2);


					if(relations.size() > 0){
						for(String rel : relations){
							if(relationToOccurrence.containsKey(rel)){
								int counterRelation = relationToOccurrence.get(rel);
								counterRelation++;
								relationToOccurrence.put(rel,counterRelation);
							}
							else{
								relationToOccurrence.put(rel,1);
							}
						}
					}
					else{
						not_assigned_counter++;
					}
				}
				else{
					not_assigned_counter++;
				}


			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		relationToOccurrence.put("N/A", not_assigned_counter);

		Map<String,Integer> sortedRelations = firstThreeRelations(relationToOccurrence);
		StatisticalKB_Bean result = new StatisticalKB_Bean();
		result.setMid1_found_counter(mid1_found_counter);
		result.setMid2_found_counter(mid2_found_counter);
		result.setRelationTypeToOccurrence(sortedRelations);
		
		return result;
	

	}
	

	private String findMidForThisEntity(String entity){
		String mid = null;
		try {
			mid = searcherEntryMapped.findMidFor(entity);
			if(mid==null){
				String wikid = searcherRedirect.findWikidFor(entity);
				if(wikid!=null)
					mid = searcherEntryMapped.findMidFor(wikid);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mid;

	}


	//restituisce una mappa ordinata in cui compaiono le prima tre relazioni con più occorrenze
	//aggiungendo anche le relazioni N/A non trovate su Freebase
	public static Map<String,Integer> firstThreeRelations(Map<String,Integer> map){
		ValueComparator valueComparator = new ValueComparator(map);
		TreeMap<String,Integer> sortedMap = new TreeMap<>(valueComparator); 
		sortedMap.putAll(map);

		Map<String,Integer> result = new TreeMap<>(valueComparator);
		int i = 0;
		for(String key : sortedMap.keySet()){
			result.put(key, sortedMap.get(key));
			i++;
			if(i==3)
				break;
		}

		result.put("N/A", map.get("N/A"));

		return result;
	}
}



//class ValueComparator implements Comparator{
//
//	private Map<String,Integer> map;
//
//	public ValueComparator(Map<String,Integer> map2) {
//		this.map = map2;
//	}
//
//	public int compare(Object first, Object second) {
//		Comparable valueA = (Comparable) map.get(first);
//		Comparable valueB = (Comparable) map.get(second);
//		return valueB.compareTo(valueA); 
//	}
//
//}


