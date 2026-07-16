package ch.ti8m.egov.framework.validation.command;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.List;

public class IgnoreMultipartFileSerializerModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        // Remove all properties containing file data from serialization
        beanProperties.removeIf(writer ->
                writer.getType().getRawClass().equals(byte[].class) ||
                        Resource.class.isAssignableFrom(writer.getType().getRawClass()) ||
                        InputStream.class.isAssignableFrom(writer.getType().getRawClass())
        );
        return beanProperties;
    }
}
