package statisticsKB;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import bean.StatisticalKB_Bean;
import bean.WikipediaTable;


public class Consumer extends Thread {

	private BlockingQueue<WikipediaTable> tablesBuffer; //buffer in cui vengono trasmessi e prelevati le stringhe di tipo json
	private BlockingQueue<String> outputBuffer; //buffer per comunicare al produttore la terminazione dei consumatori
	private SearcherForRelations2 searcher;
	private StatisticalTable statisticalTable;

	

	public Consumer(BlockingQueue<WikipediaTable> bufferTables, BlockingQueue<String> responseBufferConsumers,
			StatisticalTable statisticalTable2, SearcherForRelations2 searcherForRel) {
		this.tablesBuffer = bufferTables;
		this.outputBuffer = responseBufferConsumers;
		this.searcher = searcherForRel;
		this.statisticalTable = statisticalTable2;
	}


	@Override
	public void run() {
		super.run();

		while(true){
			try {
				WikipediaTable wikiTable = tablesBuffer.take();
				if(!wikiTable.getIdTable().equals(Message.FINISHED_PRODUCER)){
					StatisticalKB_Bean statisticalResultForThesePairs = this.searcher.findRelationsForThesePairs(wikiTable.getRows());
					this.statisticalTable.writeStatisticsAbout(wikiTable, statisticalResultForThesePairs);
				}
				else{
					tablesBuffer.put(wikiTable);
					outputBuffer.put(Message.FINISHED_CONSUMER);
					break;
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
