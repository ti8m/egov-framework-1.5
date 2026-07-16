package ch.ti8m.egov.framework.validation.command.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * MixIn class to ignore StreamingResponseBody during serialization.
 * This is necessary because StreamingResponseBody cannot be serialized by Jackson.
 */
@JsonIgnoreType
public class MixInStreamingResponseBody {

}