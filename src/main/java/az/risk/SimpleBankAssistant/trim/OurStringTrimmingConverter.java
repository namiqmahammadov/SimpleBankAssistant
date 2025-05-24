package az.risk.SimpleBankAssistant.trim;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class OurStringTrimmingConverter implements WebMvcConfigurer {

	// bu metod override edilib ve yeni converter elave edir
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingJackson2HttpMessageConverter());
	}

	// bu metod yeni converteri yaradir
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

		// burada converter yaradilir
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

		// burada mapper yaradilir
		ObjectMapper mapper = new ObjectMapper();

		// burada bilinmeyen deyisenler ucun icra olunmamaq ayari edilir
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// burada modul yaradilir
		SimpleModule module = new SimpleModule();

		// burada modula Deserializer elave edilir
		module.addDeserializer(String.class, new StringTrimmingDeserializer(String.class));

		// mapper ucun modul elave olunur
		mapper.registerModule(module);

		// converter ucun mapper qeyd olunur
		converter.setObjectMapper(mapper);

		// converter qaytarilir
		return converter;
	}
}
