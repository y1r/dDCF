package dDCF.lib;

import java.util.function.Function;

public class Task<InputType, ResultType> {
	private Function<InputType, ResultType> function;
	private InputType input;
	private ResultType result = null;
	private boolean ended = false;
	private Tasks parentTasks;

	public Task(Tasks tasks, Function<InputType, ResultType> func, InputType in) {
		function = func;
		input = in;
		parentTasks = tasks;
	}

	boolean isEnded() {
		return ended;
	}

	public void execute() {
		result = function.apply(input);
		ended = true;
	}

	public ResultType getResult() {
		return result;
	}
}
