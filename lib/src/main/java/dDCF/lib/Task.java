package dDCF.lib;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dDCF.lib.internal.SerializableFunction;

import java.io.Serializable;

public class Task {
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
	public SerializableFunction<Serializable, Serializable> function;

	public Serializable input;
	public Serializable result = null;
	public boolean ended = false;

	public Task() {
	}

	public Task(SerializableFunction<Serializable, Serializable> func, Serializable in) {
		function = func;
		input = in;
	}

	boolean isEnded() {
		return ended;
	}

	public void execute() {
		result = function.apply(input);
		ended = true;
	}

	public Serializable getResult() {
		return result;
	}

	public void setResult(Serializable t) {
		result = t;
		ended = true;
	}
}
