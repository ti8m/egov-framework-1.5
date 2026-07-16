package ch.ti8m.egov.framework.documentation;

import ch.ti8m.egov.framework.exceptionhandling.context.ContextInterceptor;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenApiParameterCustomizer {

    private static final String QUERY = "query";
    private static final String INT_FORMAT = "int32";

    @Bean
    public GlobalOpenApiCustomizer getGlobalOpenApiParameterCustomizer() {
        return openApi -> openApi.getPaths().forEach(this::customize);
    }

    @Bean
    public OpenApiCustomizer getOpenApiParameterCustomizer() {
        return openApi -> openApi.getPaths().forEach(this::customize);
    }

    private void customize(final String path, final PathItem pathItem) {
        try {
            if (
                    pathItem.getGet() != null && pathItem.getGet().getResponses().get("200").getContent().get("*/*").getSchema().get$ref() != null &&
                            pathItem.getGet() != null && pathItem.getGet().getResponses().get("200").getContent().get("*/*").getSchema().get$ref().contains("PaginatedResult")
            ) {
                pathItem.getGet().addParametersItem(new Parameter()
                        .name(ContextInterceptor.SORT)
                        .description(String.format("Use for sorting responses. Sorting is respected for pagination.%n" +
                                "Usage as follows: fieldName:asc | fieldName:desc%n" +
                                "You may use multi-sorting. The field listed first has the highest sorting-priority: fieldName1:asc,fieldName2:asc,fieldName3:desc"))
                        .required(false)
                        .schema(new ArraySchema().items(new StringSchema()))
                        .explode(false)
                        .in(QUERY));
                pathItem.getGet().addParametersItem(new Parameter()
                        .name(ContextInterceptor.FILTER)
                        .description("Use for filtering responses. Filtering is respected for pagination.")
                        .required(false)
                        .schema(new StringSchema())
                        .in(QUERY));
                pathItem.getGet().addParametersItem(new Parameter()
                        .name(ContextInterceptor.PAGE)
                        .required(false)
                        .description("Use together with SIZE for page-based pagination. First page starts with index 0.")
                        .schema(new NumberSchema().format(INT_FORMAT))
                        .in(QUERY));
                pathItem.getGet().addParametersItem(new Parameter()
                        .name(ContextInterceptor.SIZE)
                        .description("Use together with PAGE for page-based pagination.")
                        .required(false)
                        .schema(new NumberSchema().format(INT_FORMAT))
                        .in(QUERY));
                pathItem.getGet().addParametersItem(new Parameter()
                        .name(ContextInterceptor.OFFSET)
                        .description("Use together with LIMIT for offset-based pagination.")
                        .required(false)
                        .schema(new NumberSchema().format(INT_FORMAT))
                        .in(QUERY));
                pathItem.getGet().addParametersItem(new Parameter()
                        .name(ContextInterceptor.LIMIT)
                        .description("Use together with OFFSET for offset-based pagination.")
                        .required(false)
                        .schema(new NumberSchema().format(INT_FORMAT))
                        .in(QUERY));
// functionality musst be evaluated first for suitability within the framework
/*
                pathItem.getGet().addParametersItem(new Parameter()
                        .name(ContextInterceptor.SEARCH)
                        .required(false)
                        .in(QUERY));
*/
            }
        } catch (final Exception e) {
            log.warn("Cannot add query parameters to API.", e);
        }
    }

}
