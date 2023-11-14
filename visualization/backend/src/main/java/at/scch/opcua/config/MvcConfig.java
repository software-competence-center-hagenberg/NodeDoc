package at.scch.opcua.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private NodeDocConfiguration config;

//    @Value("${resources.directory}")
//    private String resourcesDirectory;

    /**
     * This method sets a handler so all calls to "http://host:port/documentation/..." get redirected to the file
     * structure, so a html file containing a documentation can be shown in the browser
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/documentation/**", "/nodeset/**")
                .addResourceLocations("file:///" + config.getDirectory().getNodesets() + "/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));

        registry
                .addResourceHandler("/diffs/**")
                .addResourceLocations("file:///" + config.getDirectory().getDiffs() + "/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));
        // could be used for referencing java script files
//        registry
//                .addResourceHandler("/scripts/**")
//                .addResourceLocations("file:///" + resourcesDirectory + "/");
    }
}