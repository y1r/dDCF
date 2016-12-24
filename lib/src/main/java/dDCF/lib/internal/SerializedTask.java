package dDCF.lib.internal;

import dDCF.lib.Task;

import java.io.*;

public class SerializedTask {
	// public SerializableFunction<Serializable, Serializable> function;
	public byte[] function;

	// public Serializable input;
	public byte[] input;

	// public Serializable result = null;
	public byte[] result;

	public boolean ended = false;

	public SerializedTask() {
	}

	public static SerializedTask serialize(Task task) {
		if (task == null) return null;

		SerializedTask serializedTask = new SerializedTask();

		serializedTask.function = toByteArray(task.function);
		serializedTask.input = toByteArray(task.input);
		serializedTask.result = toByteArray(task.result);
		serializedTask.ended = task.ended;

		return serializedTask;
	}

	@SuppressWarnings("unchecked")
	public static Task deserialize(SerializedTask serializedTask) {
		if (serializedTask == null) return null;

		Task task = new Task();

		task.function = (SerializableFunction<Serializable, Serializable>) toObject(serializedTask.function);
		task.input = toObject(serializedTask.input);
		task.result = toObject(serializedTask.result);
		task.ended = serializedTask.ended;

		return task;
	}

	private static byte[] toByteArray(Serializable object) {
		if (object == null) return null;

		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ObjectOutputStream oss = new ObjectOutputStream(byteArray);

			oss.writeObject(object);

			oss.close();
			return byteArray.toByteArray();
		} catch (IOException e) {
			Utils.debugPrint(e.toString());
			return null;
		}
	}

	private static Serializable toObject(byte[] array) {
		if (array == null) return null;

		try {
			ByteArrayInputStream byteArray = new ByteArrayInputStream(array);
			CustomObjectInputStream ois = new CustomObjectInputStream(JarByteClassLoader.getInstance());

			return (Serializable) ois.readObject();
		} catch (Exception e) {
			Utils.debugPrint(e.toString());
			return null;
		}
	}
}
