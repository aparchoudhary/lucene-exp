import com.oracle.tools.packager.IOUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main {
    private final static StandardAnalyzer analyzer = new StandardAnalyzer();
    private final static Directory index = new RAMDirectory();

    private final static IndexWriterConfig config = new IndexWriterConfig(analyzer);
    private static IndexWriter w;



    public static void main(String[] args) throws Exception {
        w = new IndexWriter(index, config);
        DocumentParser documentParser = new DocumentParser();
        BufferedReader  bufferedReader = new BufferedReader(new FileReader("cran.all.1400"));

        documentParser.parse(bufferedReader.lines().collect(Collectors.toList()).listIterator(), Main::addDoc);
        w.close();

        String querystr = args.length > 0 ? args[0] : "lucene";
        Query q = new QueryParser("id", analyzer).parse("365");
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        System.out.println(searcher.doc(hits[0].doc).get("text"));
    }
    static void addDoc(DocumentParser.Document doc1) {
        Document doc = new Document();
        doc.add(new TextField("title", doc1.getTitle(), Field.Store.YES));
        doc.add(new StringField("bibliography", doc1.getBibliography(), Field.Store.YES));
        doc.add(new StringField("id", doc1.getId(), Field.Store.YES));
        doc.add(new StringField("text", doc1.getAbstractText(), Field.Store.YES));
        try {
            w.addDocument(doc);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
