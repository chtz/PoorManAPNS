package ch.furthermore.pmapns;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Storage {
	private final static ObjectMapper om = new ObjectMapper();
	
	@Value(value="${storagePath:storage}")
	private String storagePath;
	private File storage;
	
	@PostConstruct
	public void init() {
		storage = new File(storagePath);
		storage.mkdirs();
	}

	public <T> T load(Class<T> clazz, String id) throws JsonParseException, JsonMappingException, IOException {
		File file = new File(storage, UUID.fromString(id).toString() + ".json");
		
		return om.readValue(file, clazz);
	}
	
	public String insert(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
		String id = id();
		File file = file(id, ".json");
		
		om.writeValue(file, obj);
		
		return id;
	}

	public String insertBinary(InputStream in, String suffix) throws IOException {
		String id = id();
		File file = file(id, suffix);
		
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		try {
			FileCopyUtils.copy(in,  out);
		}
		finally {
			out.close();
		}
		
		return id;
	}
	
	private String id() {
		return UUID.randomUUID().toString();
	}
	
	public File file(String id, String suffix) {
		return new File(storage, id + suffix);
	}
}
