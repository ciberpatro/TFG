package semantica;
import java.util.ArrayList;
public class Value {

	public static Value VOID = new Value(new Object());

	final Object value;

	public Value(Object value) {
		this.value = value;
	}
	private Object getObject(){
		return (value instanceof Value) ? ((Value) value).getObject() : value;
	}

	public Boolean asBoolean() {
		return (Boolean) value;
	}

	public Integer asInteger() {
		return (Integer) value;
	}

	public Double asDouble() {
		return (Double) value;
	}

	public String asString() {
		return String.valueOf(value.toString());
	}

	public ArrayList<Value> asList(){
		return (ArrayList<Value>) getObject();
	}
	public String getValClass(){
		return value.getClass().toString();
	}

	public boolean isString() {
		return value instanceof String;
	}

	public boolean isBoolean(){
		return value instanceof Boolean;
	}

	public boolean isNumber() {
		return value instanceof Number;
	}

	public boolean isDouble() {
		return value instanceof Double;
	}

	public boolean isInteger() {
		return value instanceof Integer;
	}
	public boolean isList(){
		return getObject() instanceof ArrayList;
	}

	public int hashCode() {

		if(value == null) {
			return 0;
		}

		return this.value.hashCode();
	}

	public boolean equals(Object o) {

		if(value == o) {
			return true;
		}

		if(value == null || o == null || o.getClass() != value.getClass()) {
			return false;
		}

		Value that = (Value)o;

		return this.value.equals(that.value);
	}

	public String toString() {
		return String.valueOf(value);
	}
}
