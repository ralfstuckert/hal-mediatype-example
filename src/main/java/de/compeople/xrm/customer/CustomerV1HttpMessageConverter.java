package de.compeople.xrm.customer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Converts the current (V2) version of the customer - where the name is split
 * into different properties for first and last name - to the legacy V1 representation
 * with one single property "name":
 * <pre>
 * { "name": "Ralf Stuckert" }
 * </pre>
 */
public class CustomerV1HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public CustomerV1HttpMessageConverter() {
        super(createObjectMapper());
        setSupportedMediaTypes(Arrays.asList(new MediaType("application", "vnd.customer.v1+json")));
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Customer.class, new CustomerV2Serializer());
        module.addDeserializer(Customer.class, new CustomerV2Deserializer());
        mapper.registerModule(module);
        return mapper;
    }

    public static class CustomerV2Serializer extends StdSerializer<Customer> {

        public CustomerV2Serializer() {
            this(null);
        }

        public CustomerV2Serializer(Class<Customer> t) {
            super(t);
        }

        @Override
        public void serialize(
                Customer value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

            jgen.writeStartObject();
            jgen.writeStringField("name", String.format("%s %s", value.getFirstName(), value.getLastName()));
            jgen.writeEndObject();
        }
    }

    public static class CustomerV2Deserializer extends StdDeserializer<Customer> {

        public CustomerV2Deserializer() {
            this(null);
        }

        public CustomerV2Deserializer(Class<Customer> t) {
            super(t);
        }

        @Override
        public Customer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String name = node.get("name").asText();
            // stupid simple, just for demo purpose
            String[] parts = name.split(" ");
            String firstName = "";
            String lastName = "";
            if (parts.length > 0) {
                firstName = parts[0];
            }
            if (parts.length > 1) {
                lastName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            }
            return new Customer(firstName, lastName);

        }
    }
}

