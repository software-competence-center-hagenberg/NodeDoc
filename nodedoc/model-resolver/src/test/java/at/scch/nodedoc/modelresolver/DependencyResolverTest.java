package at.scch.nodedoc.modelresolver;

import at.scch.nodedoc.ModelMetaData;
import at.scch.nodedoc.testkit.ClasspathModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DependencyResolverTest {
    private DependencyResolver dependencyResolver;
    private ClasspathModelRepository classpathModelRepository;

    @BeforeEach
    void setUp() {
        classpathModelRepository = new ClasspathModelRepository();
        dependencyResolver = new DependencyResolver(classpathModelRepository);
    }

    @Test
    void getNewestModelMetaData() {
        var modelMetaDataCollection = List.of(
                new ModelMetaData("http://opcfoundation.com/UA", "1.0", OffsetDateTime.parse("2020-02-02T10:15:30+01:00")),
                new ModelMetaData("http://opcfoundation.com/UA", "2.0", OffsetDateTime.parse("2021-02-02T10:15:30+01:00")),
                new ModelMetaData("http://opcfoundation.com/UA/DI", "2.0", OffsetDateTime.parse("2020-08-02T10:15:30+01:00")),
                new ModelMetaData("http://opcfoundation.com/UA/DI", "1.0", OffsetDateTime.parse("2019-08-02T10:15:30+01:00")),
                new ModelMetaData("http://euromap.com", "1.0", OffsetDateTime.parse("2020-08-02T10:15:30+01:00"))
        );

        var result = dependencyResolver.getNewestModelMetaData(modelMetaDataCollection.stream());

        assertThat(result).containsExactlyInAnyOrder(
                new ModelMetaData("http://opcfoundation.com/UA", "2.0", OffsetDateTime.parse("2021-02-02T10:15:30+01:00")),
                new ModelMetaData("http://opcfoundation.com/UA/DI", "2.0", OffsetDateTime.parse("2020-08-02T10:15:30+01:00")),
                new ModelMetaData("http://euromap.com", "1.0", OffsetDateTime.parse("2020-08-02T10:15:30+01:00"))
        );

    }

    @Test
    void collectDependenciesAllEqual() {
        var dependencies = dependencyResolver.collectDependencies(ClasspathModelRepository.Models.A);
        assertThat(dependencies).containsExactlyInAnyOrder(
                ClasspathModelRepository.Models.B,
                ClasspathModelRepository.Models.C
        );
    }

    @Test
    void collectDependencies() {
        var dependencies = dependencyResolver.collectDependencies(ClasspathModelRepository.Models.D);
        assertThat(dependencies).containsExactlyInAnyOrder(
                ClasspathModelRepository.Models.B2,
                ClasspathModelRepository.Models.C2
        );
    }

    @Test
    void collectDependenciesMissingNodeSet() {
        assertThatThrownBy(() -> {
            dependencyResolver.collectDependencies(ClasspathModelRepository.Models.E);
        }).isInstanceOf(RuntimeException.class);
    }

}