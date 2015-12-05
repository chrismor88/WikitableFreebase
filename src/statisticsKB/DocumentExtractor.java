package statisticsKB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import bean.WikipediaTable;
import statisticsKB.Message;

public class DocumentExtractor extends Thread{

	private String pathContentFile;

	private BlockingQueue<WikipediaTable> bufferTables;
	private BlockingQueue<String> responseBufferConsumers;


	public DocumentExtractor(String pathInputFile, BlockingQueue<WikipediaTable> bufferTables, BlockingQueue<String> responseBufferConsumers) {
		this.pathContentFile = pathInputFile;
		this.bufferTables = bufferTables;
		this.responseBufferConsumers = responseBufferConsumers;
	}

	@Override
	public void run() {
		super.run();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(pathContentFile)));
			String currentLine = "";
			WikipediaTable wikiTable = null;
			while((currentLine = reader.readLine())!=null){
				if(currentLine.startsWith("<doc")){
					wikiTable = new WikipediaTable();
					String[] fieldsTag = currentLine.split("\t");
					wikiTable.setIdTable(fieldsTag[1].replace("idPage=","")+"-"+fieldsTag[2].replace("idTable=",""));
					wikiTable.setWikid(fieldsTag[3].replaceAll("wikid=", ""));
					wikiTable.setNrows(Integer.parseInt(fieldsTag[4].replaceAll("nrow=","")));
					wikiTable.setNcols(Integer.parseInt(fieldsTag[5].replaceAll("ncol=","")));
					wikiTable.setNrowsWithMultipleMentions(Integer.parseInt(fieldsTag[6].replaceAll("nrows_with_multiple_mentions=", "")));
				}
				else{
					while(!currentLine.equals("</doc>") && currentLine!=null){
						wikiTable.addRow(currentLine);
						currentLine = reader.readLine();
					}
					bufferTables.put(wikiTable);
				}
			}
			wikiTable = new WikipediaTable();
			wikiTable.setIdTable(Message.FINISHED_PRODUCER);
			bufferTables.put(wikiTable);

			int counterCompletedConsumers = 0;
			int number_of_threads = Runtime.getRuntime().availableProcessors();
			while(counterCompletedConsumers <  number_of_threads){
				String message = responseBufferConsumers.take();
				if(message.equals(Message.FINISHED_CONSUMER))
					counterCompletedConsumers++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
