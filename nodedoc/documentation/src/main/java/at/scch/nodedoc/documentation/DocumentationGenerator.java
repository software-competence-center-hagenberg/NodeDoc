package at.scch.nodedoc.documentation;

import at.scch.nodedoc.documentation.displaymodel.DisplayNodeSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class DocumentationGenerator {

    @Getter
    public static class Config {
        private final String couchDbUrl;

        public Config(String couchDbUrl) {
            this.couchDbUrl = couchDbUrl;
        }
    }

    public void generateDocumentation(DisplayNodeSet displayNodeSet, Config config, OutputStream output, InputStream htmlTemplate) throws IOException {
        log.info("Generate documentation for NodeSet");
        Document htmlDoc = Jsoup.parse(htmlTemplate, null, "");

        Element tableOfContents = htmlDoc.body().getElementById("table-of-contents");

        if (tableOfContents == null) {
            tableOfContents = new Element("div").attr("id", "table-of-contents");
            htmlDoc.body().prependChild(tableOfContents);
        }

        tableOfContents.empty();

        Element content = htmlDoc.body().getElementById("content");

        if (content == null) {
            content = new Element("div").attr("id", "content");
            htmlDoc.body().appendChild(content);
        }

        htmlDoc.head().appendElement("style").html(readFileFromResources("/index.css"));

        var generatedContent = displayNodeSet.getContent().template().render().toString();

        content.appendElement("script").html("window.COUCHDB_URI = " + config.couchDbUrl + ";");

        content.appendElement("script").html(readFileFromResources("/docscript.js"));
        content.append(generatedContent);

        var generatedTableOfContents = displayNodeSet.getTableOfContents().template().render().toString();

        tableOfContents.append(generatedTableOfContents);

        Element mdi = new Element("link");
        mdi.attr("rel", "stylesheet");
        mdi.attr("href", "https://fonts.googleapis.com/icon?family=Material+Icons");
        htmlDoc.head().appendChild(mdi);

        output.write(htmlDoc.toString().getBytes(StandardCharsets.UTF_8));
        output.close();
    }

    private static String readFileFromResources(String path) {
        return new BufferedReader(
                new InputStreamReader(DocumentationGenerator.class.getResourceAsStream(path)))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
