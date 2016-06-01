package zotmc.onlysilver.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Supplier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonHelper {
	
	private final JsonObject backing;
	
	public JsonHelper() {
		this(new JsonObject());
	}
	
	public JsonHelper(JsonObject backing) {
		this.backing = checkNotNull(backing);
	}
	
	public JsonObject asJsonObject() {
		return backing;
	}
	
	@Override public int hashCode() {
		return backing.hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		return obj == this || obj instanceof JsonHelper && backing.equals(((JsonHelper) obj).backing);
	}
	
	@Override public String toString() {
		return backing.toString();
	}
	
	public JsonHelper set(String key, JsonElement value) {
		backing.add(key, value);
		return this;
	}
	
	public JsonHelper set(String key, JsonHelper jsonHelper) {
		backing.add(key, jsonHelper.backing);
		return this;
	}
	
	public JsonHelper set(String key, String value) {
		backing.addProperty(key, value);
		return this;
	}
	
	public JsonHelper set(String key, Number value) {
		backing.addProperty(key, value);
		return this;
	}
	
	public JsonHelper set(String key, Boolean value) {
		backing.addProperty(key, value);
		return this;
	}
	
	public JsonHelper set(String key, Character value) {
		backing.addProperty(key, value);
		return this;
	}
	
	public static JsonArray array(JsonElement... elements) {
		JsonArray j = new JsonArray();
		for (JsonElement e : elements)
			j.add(e);
		return j;
	}
	
	public static JsonArray array(JsonHelper... elements) {
		JsonArray j = new JsonArray();
		for (JsonHelper e : elements)
			j.add(e.asJsonObject());
		return j;
	}
	
	public static JsonArray array(String... elements) {
		JsonArray j = new JsonArray();
		for (String e : elements)
			j.add(new JsonPrimitive(e));
		return j;
	}
	
	public static JsonArray array(Number... elements) {
		JsonArray j = new JsonArray();
		for (Number e : elements)
			j.add(new JsonPrimitive(e));
		return j;
	}
	
	public static JsonArray array(Boolean... elements) {
		JsonArray j = new JsonArray();
		for (Boolean e : elements)
			j.add(new JsonPrimitive(e));
		return j;
	}
	
	public static JsonArray array(Character... elements) {
		JsonArray j = new JsonArray();
		for (Character e : elements)
			j.add(new JsonPrimitive(e));
		return j;
	}
	
	public boolean hasKey(String key) {
		return backing.has(key);
	}
	
	public boolean hasKey(String key, Class<? extends JsonElement> classOfElement) {
		return classOfElement.isInstance(backing.get(key));
	}
	
	public JsonElement get(String key) {
		return backing.get(key);
	}
	
	public JsonElement remove(String key) {
		return backing.remove(key);
	}
	
	public Set<Map.Entry<String, JsonElement>> entrySet() {
		return backing.entrySet();
	}
	
	public boolean isEmpty() {
		return backing.entrySet().isEmpty();
	}
	
	private static boolean present(JsonElement element) {
		return element != null && !(element instanceof JsonNull);
	}
	
	public JsonHelper getAsHelper(String key) {
		try {
			JsonObject j = backing.getAsJsonObject(key);
			return new JsonHelper(present(j) ? j : new JsonObject());
		} catch (Exception ignored) { }
		return new JsonHelper();
	}
	
	public JsonHelper getAsHelper(String key, Supplier<JsonHelper> defaultValueFactory) {
		try {
			JsonObject j = backing.getAsJsonObject(key);
			return new JsonHelper(present(j) ? j : new JsonObject());
		} catch (Exception ignored) { }
		return defaultValueFactory.get();
	}
	
	public JsonArray getAsArray(String key) {
		try {
			JsonArray j = backing.getAsJsonArray(key);
			return present(j) ? j : new JsonArray();
		} catch (Exception ignored) { }
		return new JsonArray();
	}
	
	public JsonArray getAsArray(String key, Supplier<JsonArray> defaultValueFactory) {
		try {
			JsonArray j = backing.getAsJsonArray(key);
			return present(j) ? j : new JsonArray();
		} catch (Exception ignored) { }
		return defaultValueFactory.get();
	}

	public String getAsString(String key) {
		return getAsString(key, "");
	}
	
	public String getAsString(String key, String defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsString() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}

	public BigDecimal getAsBigDecimal(String key) {
		return getAsBigDecimal(key, BigDecimal.ZERO);
	}
	
	public BigDecimal getAsBigDecimal(String key, BigDecimal defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsBigDecimal() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}

	public double getAsDouble(String key) {
		return getAsDouble(key, 0);
	}
	
	public double getAsDouble(String key, double defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsDouble() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
	public float getAsFloat(String key) {
		return getAsFloat(key, 0);
	}
	
	public float getAsFloat(String key, float defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsFloat() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
	public BigInteger getAsBigInteger(String key) {
		return getAsBigInteger(key, BigInteger.ZERO);
	}
	
	public BigInteger getAsBigInteger(String key, BigInteger defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsBigInteger() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
	public long getAsLong(String key) {
		return getAsLong(key, 0);
	}
	
	public long getAsLong(String key, long defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsLong() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
	public int getAsInt(String key) {
		return getAsInt(key, 0);
	}
	
	public int getAsInt(String key, int defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsInt() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
	public short getAsShort(String key) {
		return getAsShort(key, (short) 0);
	}
	
	public short getAsShort(String key, short defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsShort() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
	public byte getAsByte(String key) {
		return getAsByte(key, (byte) 0);
	}
	
	public byte getAsByte(String key, byte defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsByte() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
	public boolean getAsBoolean(String key) {
		return getAsBoolean(key, false);
	}
	
	public boolean getAsBoolean(String key, boolean defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsBoolean() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
	public char getAsChar(String key) {
		return getAsChar(key, (char) 0);
	}
	
	public char getAsChar(String key, char defaultValue) {
		try {
			JsonElement j = backing.get(key);
			return present(j) ? j.getAsCharacter() : defaultValue;
		} catch (Exception ignored) { }
		return defaultValue;
	}
	
}
