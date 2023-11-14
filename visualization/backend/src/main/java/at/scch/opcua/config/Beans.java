package at.scch.opcua.config;

import at.scch.nodedoc.ModelFileSystemRepository;
import at.scch.nodedoc.ModelRepository;
import at.scch.nodedoc.NodeSetAnnotator;
import at.scch.nodedoc.XMLWriter;
import at.scch.nodedoc.db.repository.NodeDescriptionRepository;
import at.scch.nodedoc.documentation.DocumentationGenerator;
import at.scch.nodedoc.documentation.diff.generator.*;
import at.scch.nodedoc.documentation.diff.graph.DiffGraphDisplayDataBuilder;
import at.scch.nodedoc.documentation.single.generator.*;
import at.scch.nodedoc.documentation.single.graph.SingleGraphDisplayDataBuilder;
import at.scch.nodedoc.modelresolver.DependencyResolver;
import at.scch.nodedoc.modelresolver.ModelResolver;
import at.scch.nodedoc.modelresolver.NodeIdParser;
import at.scch.nodedoc.modelresolver.ReferenceResolver;
import at.scch.nodedoc.parser.NodeSetXMLParser;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class Beans {

    @Autowired
    private NodeDocConfiguration config;

    @Autowired
    private GenericApplicationContext context;
    @PostConstruct
    public void registerBeans() {
        context.registerBean(DocumentationGenerator.class);
        context.registerBean(NodeSetXMLParser.class);
        context.registerBean(NodeIdParser.class);
        context.registerBean(ReferenceResolver.class);
        context.registerBean(ModelResolver.class);
        context.registerBean(DependencyResolver.class);
        context.registerBean(NodeDescriptionRepository.class);
        context.registerBean(NodeSetAnnotator.class);
        context.registerBean(XMLWriter.class);
        context.registerBean(DiffDisplayNodeSetGenerator.class);
        context.registerBean(DiffDisplayTypeGenerator.class);
        context.registerBean(DiffDisplayReferencesTableSectionGenerator.class);
        context.registerBean(DiffDisplayInstanceGenerator.class);
        context.registerBean(DiffDisplayDataTypeTableSectionGenerator.class);
        context.registerBean(DiffDisplayMethodGenerator.class);
        context.registerBean(DiffGraphDisplayDataBuilder.class);
        context.registerBean(SingleDisplayNodeSetGenerator.class);
        context.registerBean(SingleDisplayTypeGenerator.class);
        context.registerBean(SingleDisplayInstanceGenerator.class);
        context.registerBean(SingleDisplayReferencesTableSectionGenerator.class);
        context.registerBean(SingleDisplayDataTypeTableSectionGenerator.class);
        context.registerBean(SingleDisplayMethodGenerator.class);
        context.registerBean(SingleGraphDisplayDataBuilder.class);
        context.registerBean(DocEntryEditorGenerator.class);
    }

    @Bean
    public ModelRepository modelRepository(NodeSetXMLParser nodeSetXmlParser) {
        return new ModelFileSystemRepository(nodeSetXmlParser, new File(config.getDirectory().getNodesets()));
    }

    @Bean
    public Database database() {
        try {
            CloudantClient client = ClientBuilder.url(new URL(config.getCouchDb().getUri()))
                    .username(config.getCouchDb().getUsername())
                    .password(config.getCouchDb().getPassword())
                    .build();
            return client.database(config.getCouchDb().getDatabaseName(), config.getCouchDb().shouldCreateDb());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
