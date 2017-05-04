package io.swagger.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.models.Components;
import io.swagger.models.ExternalDocumentation;
import io.swagger.models.OpenAPI;
import io.swagger.models.Operation;
import io.swagger.models.PathItem;
import io.swagger.models.Paths;
import io.swagger.models.examples.Example;
import io.swagger.models.info.Contact;
import io.swagger.models.info.Info;
import io.swagger.models.links.Link;
import io.swagger.models.media.Content;
import io.swagger.models.media.IntegerSchema;
import io.swagger.models.media.MediaType;
import io.swagger.models.media.Schema;
import io.swagger.models.media.StringSchema;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.responses.Response;
import io.swagger.models.responses.Responses;
import io.swagger.models.tags.Tag;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimpleBuilderTest {
    @Test
    public void testBuilder() throws Exception {
        // basic metadata
        OpenAPI oai =
                new OpenAPI()
                        .info(new Info()
                                .contact(new Contact()
                                        .email("tony@eatbacon.org")
                                        .name("Tony the Tam")
                                        .url("https://foo.bar")))
                        .externalDocs(new ExternalDocumentation()
                                .description("read more here")
                                .url("http://swagger.io"))
                        .addTagsItem(new Tag()
                                .name("funky dunky")
                                .description("all about neat things"));

        Map<String, Schema> schemas = new HashMap<>();

        schemas
            .put("StringSchema", new StringSchema()
                .description("simple string schema")
                .minLength(3)
                .maxLength(100)
                .example(new Example())
        );

        schemas.put("IntegerSchema", new IntegerSchema()
                .description("simple integer schema")
                .multipleOf(new BigDecimal(3))
                .minimum(new BigDecimal(6))
        );

        oai.components(new Components()
                .schemas(schemas));

        schemas.put("Address", new Schema()
                .description("address object")
                .addProperties("street", new StringSchema()
                        .description("the street number"))
                .addProperties("city", new StringSchema()
                        .description("city"))
                .addProperties("state", new StringSchema()
                        .description("state")
                        .minLength(2)
                        .maxLength(2))
                .addProperties("zip", new StringSchema()
                        .description("zip code")
                        .pattern("^\\d{5}(?:[-\\s]\\d{4})?$")
                        .minLength(2)
                        .maxLength(2))
                .addProperties("country", new StringSchema()
                        .description("2-digit country code")
                        .minLength(2)
                        .maxLength(2)
                        ._enum(new ArrayList<Object>(){{
                            this.add("US");
                        }}))
        );


        oai.paths(new Paths()
                .addPathItem("/foo", new PathItem()
                        .description("the foo path")
                        .get(new Operation()
                                .addParametersItem(new QueryParameter()
                                        .description("Records to skip")
                                        .required(false)
                                        .schema(new IntegerSchema()
                                ))
                                .responses(new Responses()
                                        .addResponse("200", new Response()
                                                .description("it worked")
                                                .content(new Content()
                                                        .addMediaType("application/json",
                                                                new MediaType().schema(new Schema()
                                                                        .ref("#/components/schemas/Address")))
                                                )
                                                .links(new Link()
                                                        .operationId("getFunky")))
                                )
                        )
                )
        );


        System.out.println(writeJson(oai));
    }


    public static String writeJson(Object value) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.writer(new DefaultPrettyPrinter()).writeValueAsString(value);
    }
}
