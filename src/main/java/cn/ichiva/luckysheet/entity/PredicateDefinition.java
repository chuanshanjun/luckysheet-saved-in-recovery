package cn.ichiva.luckysheet.entity;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

//@ApiModel("gw 断言")
public class PredicateDefinition {

	public static final String GENERATED_NAME_PREFIX = "_genkey_";

	//@ApiModelProperty("断言名称,如:path/header/...")
	private String name;

	//@ApiModelProperty("断言参数")
	private Map<String, String> args = new LinkedHashMap<>();

	public PredicateDefinition() {
	}

	public PredicateDefinition(String text) {
		int eqIdx = text.indexOf('=');
		if (eqIdx <= 0) {
			throw new RuntimeException("Unable to parse PredicateDefinition text '"
					+ text + "'" + ", must be of the form name=value");
		}
		setName(text.substring(0, eqIdx));

		String[] args = text.substring(eqIdx + 1).split(",");

		for (int i = 0; i < args.length; i++) {
			this.args.put(GENERATED_NAME_PREFIX + i, args[i]);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getArgs() {
		return args;
	}

	public void setArgs(Map<String, String> args) {
		this.args = args;
	}

	public void addArg(String key, String value) {
		this.args.put(key, value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PredicateDefinition that = (PredicateDefinition) o;
		return Objects.equals(name, that.name) && Objects.equals(args, that.args);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, args);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PredicateDefinition{");
		sb.append("name='").append(name).append('\'');
		sb.append(", args=").append(args);
		sb.append('}');
		return sb.toString();
	}

}
