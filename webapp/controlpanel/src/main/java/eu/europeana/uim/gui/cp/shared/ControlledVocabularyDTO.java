package eu.europeana.uim.gui.cp.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A DTO representation of a Controlled Vocabulary
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public class ControlledVocabularyDTO implements IsSerializable {

	//The name of the vocabulary
	private String name;
	//The location from which it was retrieved
	private String uri;
	//The suffix of the controlled vocabulary
	private String suffix;
	//The mappings of a controlled vocabualry
	private List<MappingDTO> mapping;
	
	private String location;
	
	private String[] rules;
	
	private int iterations;

	// GETTERS AND SETTERS
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public List<MappingDTO> getMapping() {
		return mapping;
	}

	public void setMapping(List<MappingDTO> mapping) {
		this.mapping = mapping;
	}

	public String[] getRules() {
		return rules;
	}

	public void setRules(String[] rules) {
		this.rules = rules;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	
	
}
