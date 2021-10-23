import java.io.IOException;
import java.util.ListIterator;
import java.util.function.Consumer;

public class DocumentParser {

    public void parse(ListIterator<String> iterator, Consumer<Document> consumer) throws IOException {
        Document document = null;
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(".I")) {
                if (document!=null)consumer.accept(document);
                document = new Document();
                document.setId(parseId(line));
            } else {
                Type type = parseType(line);
                StringBuilder builder = new StringBuilder();
                while (iterator.hasNext()) {
                    line = iterator.next();
                    Type nextType = parseType(line);
                    if (nextType!=null) {
                        iterator.previous();
                        break;
                    }
                    builder.append(line);
                }
                document.setTypeText(type, builder.toString());
            }
        }
        if (document!=null)consumer.accept(document);
    }

    private Type parseType(String line) {
        char type = line.charAt(1);
        switch (type) {
            case 'I':return Type.id;
            case 'B':return Type.bibliography;
            case 'A':return Type.author;
            case 'W':return Type.abstractText;
            case 'T':return Type.title;
            default: return null;
        }
    }

    private String parseId(String line) {
        return line.substring(3);
    }

    static class Document {
        private String id, title, author, bibliography, abstractText;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getBibliography() {
            return bibliography;
        }

        public void setBibliography(String bibliography) {
            this.bibliography = bibliography;
        }

        public String getAbstractText() {
            return abstractText;
        }

        public void setAbstractText(String abstractText) {
            this.abstractText = abstractText;
        }

        public void setTypeText(Type type, String str) {
            if (type==Type.id)id=str;
            if (type==Type.abstractText)abstractText=str;
            if (type==Type.author)author=str;
            if (type==Type.bibliography)bibliography=str;
            if (type==Type.title)title=str;
        }

        @Override
        public String toString() {
            return "Document{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", bibliography='" + bibliography + '\'' +
                    ", abstractText='" + abstractText + '\'' +
                    '}';
        }
    }

    enum Type {
        id, title, author, bibliography, abstractText
    }
}
