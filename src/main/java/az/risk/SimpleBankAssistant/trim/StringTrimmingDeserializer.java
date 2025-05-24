package az.risk.SimpleBankAssistant.trim;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class StringTrimmingDeserializer extends StdDeserializer<String> {

	private static final long serialVersionUID = -6972065572263950443L;

	// konstruktor
	protected StringTrimmingDeserializer(Class<String> vc) {
		super(vc);
	}

	// bu metod trim emeliyyatini edir
	@Override
	public String deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException {

		// burada servere daxil olan String deyeri qebul olunur
		String data = parser.getText();

		// burada null olmayan setirler trim edilir
		String result = data == null ? null : data.trim();

		// burada geri qaytarilir
		return result;
	}
}