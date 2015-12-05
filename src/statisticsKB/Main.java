package statisticsKB;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import util.ReaderAccessFile;
import util.ReaderFileConfig;
import bean.WikipediaTable;


public class Main {


	public static void main(String[] args) throws FileNotFoundException {
		
		
		ReaderFileConfig readerFileConfig = new ReaderFileConfig();
		
		BlockingQueue<WikipediaTable> bufferTables = new LinkedBlockingQueue<>(1000);
		BlockingQueue<String> responseBufferConsumers = new LinkedBlockingQueue<>(4);

		try {
			String pathFileStatistics = readerFileConfig.getPathStatisticsFile();
			String contentFilePath = readerFileConfig.getContentFile();
			File timestamp = new File(readerFileConfig.getValueFor("file_timestamp"));
			FileWriter writerTimestamp = new FileWriter(timestamp);
			
			
			BufferedWriter fileWriterStatistics = new BufferedWriter(new FileWriter(new File(pathFileStatistics),true));
			String headerFileStatistics = "ID\tWIKID\tPAIRS (fist four)\tURL\tORIGINAL ROWS\tORIGINAL COLUMNS\tEFFECTIVE ROWS\tSIGNIGFICANT COLUMNS\tMID1 FOUND\tMID2 FOUND\t1ST RELATION\tOCCURENCE 1ST RELATION\t"+
					"2ND RELATION\tOCCURRENCE 2ND RELATION\t3RD RELATION\tOCCURRENCE 3RD RELATION\tN/A\tNUM. ROWS WITH MULTIPLE MENTIONS\n";
			fileWriterStatistics.write(headerFileStatistics);
			DocumentExtractor docExtractor = new DocumentExtractor(contentFilePath, bufferTables,responseBufferConsumers);
			
			
			SearcherForRelations2 searcherForRel = new SearcherForRelations2(readerFileConfig);
			StatisticalTable statisticalTable = new StatisticalTable(fileWriterStatistics);

			int cores = Runtime.getRuntime().availableProcessors();
			
			Consumer[] consumers = new Consumer[cores];
			docExtractor.start();
//			Thread.sleep(2000);
			
			Date start = new Date();

			for(Consumer consumer : consumers){
				consumer = new Consumer(bufferTables, responseBufferConsumers, statisticalTable, searcherForRel);
				consumer.start();
			}


			docExtractor.join();
			Date end = new Date();
			double totalTimeInMilliSecs = ((double)end.getTime() - (double)end.getTime());
			double totalTime = totalTimeInMilliSecs / 1000;
			
			writerTimestamp.write("Start at: "+start.toString()+"\n");
			writerTimestamp.write("End at: "+end.toString()+"\n");
			writerTimestamp.write("Total in milli secs: "+totalTimeInMilliSecs);
			writerTimestamp.write("Total in secs: "+totalTime);
			writerTimestamp.close();
			fileWriterStatistics.close();
			
			System.out.println("Start at: "+start.toString());
			System.out.println("End at: "+end.toString());
			System.out.println("Total in milli secs: "+totalTimeInMilliSecs);
			System.out.println("Total in secs: "+totalTime);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}

}
