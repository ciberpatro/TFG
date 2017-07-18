package semantic.tfg;
import java.util.ArrayList;
public class Value {

	public static Value VOID = new Value(new Object());

	private Object value;

	public Value(Object value) {
		this.value = value;
	}
	
	public Value(Value value){
		this.value=value.getValue();
		if (value.isList()) this.value = value.asList().clone();
	}

	private Object getValue(){
		return value;
	}

	public Boolean asBoolean() {
		return (Boolean) value;
	}

	public Integer asInteger() {
		return (Integer) value;
	}

	public Double asDouble() {
		return Double.valueOf(value.toString());
	}

	public String asString() {
		return String.valueOf(value.toString());
	}

	public ArrayList<Value> asList(){
		return (ArrayList<Value>) value;
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
		return value instanceof ArrayList || value instanceof Value;
	}

	public int hashCode() {

		if(value == null) {
			return 0;
		}

		return this.value.hashCode();
	}
	public boolean equals(Object a) {
		Value o = (Value) a;
		boolean eq=false;
		if(value.equals(o.getValue())) {
			eq = true;
		}else if(value == null || o == null || o.getValue().getClass() != value.getClass()) {
			eq = false;
		}
		return eq;
	}

	public String toString() {
		return String.valueOf(value);
	}
}
