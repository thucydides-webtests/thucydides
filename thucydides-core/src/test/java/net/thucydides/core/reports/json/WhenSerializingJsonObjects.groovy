package net.thucydides.core.reports.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import spock.lang.Specification


class WhenSerializingJsonObjects extends Specification {

    def serializationContext = Mock(JsonSerializationContext)
    def deserializationContext = Mock(JsonDeserializationContext)
    def type = Mock(java.lang.reflect.Type)

    def "should serialize Throwables to JSON"() {
        given:
            def adaptor = new ThrowableClassAdapter()
            def anException = new AssertionError("Oh bother")
        when:
            JsonElement element = adaptor.serialize(anException, type, serializationContext);
        then:
        ((JsonObject) element).get("class").toString() == "\"java.lang.AssertionError\""
        ((JsonObject) element).get("message").toString() == "\"Oh bother\""
    }

    def "should deserialise Throwables from JSON"() {
        given:
            def adaptor = new ThrowableClassAdapter()
            def element = new JsonObject();
            element.addProperty("class","java.lang.AssertionError")
            element.addProperty("message","Oh bother")
        when:
            def throwable = adaptor.deserialize(element, type, deserializationContext)
        then:
            throwable
    }
}