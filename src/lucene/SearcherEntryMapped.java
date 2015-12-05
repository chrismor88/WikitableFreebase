package lucene;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.ReaderFileConfig;


public class SearcherEntryMapped {

	private String indexPath;

	private final float SCORE_THRESHOLD = 0.5f;
	private final String Field = "title";

	private StandardAnalyzer analyzer;
	private IndexReader reader;
	private IndexSearcher searcher;
	private QueryParser parser;
	private ReaderFileConfig readerFileConfig;

	public SearcherEntryMapped(ReaderFileConfig readerFileConfig) throws IOException {
		indexPath = readerFileConfig.getValueFor("index_mapping");
		reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer(Version.LUCENE_47);
		parser = new QueryParser(Version.LUCENE_47, Field, analyzer);
	}


	public synchronized String findMidFor(String wikid) throws IOException, UnsupportedEncodingException {

		int maxHits = 3;
		Document d = null;
		String mid = null;


		try {
			Query query = parser.parse(wikid);

			TopDocs results = searcher.search(query, 5 * maxHits);
			ScoreDoc[] hits = results.scoreDocs;

			if(hits.length > 0){
				int docId = hits[0].doc;
				d = searcher.doc(docId);
				mid = d.get("mid");
			}

		} catch (ParseException e) {
			System.err.println("Incorrect Query");
		}

		return mid;
	}



}