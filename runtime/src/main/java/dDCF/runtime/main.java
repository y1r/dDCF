package dDCF.runtime;

import dDCF.lib.Work;
import dDCF.lib.internal.Config;
import dDCF.lib.internal.InterConnects.InterConnects;
import dDCF.lib.internal.JarByteClassLoader;
import dDCF.lib.internal.Utils;
import dDCF.lib.internal.Worker;
import dDCF.runtime.Utils.CmdLineParser;
import dDCF.runtime.Utils.Reflection;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class main {
	public static void main(String[] args) {
		Config cfg = null;
		CmdLineParser parser = new CmdLineParser();

		try {
			cfg = parser.parse(args);
		} catch (ParseException e) {
			System.out.println("Parse Error: " + e.getMessage());
			parser.showUsage();
			return;
		}

		// cache jar file (master)
		if (cfg.isMaster) {
			try {
				cfg.jarByteCode = Utils.readFile(cfg.jarName);
			} catch (IOException e) {
				System.out.println("Jar File Reading Error: " + e.getMessage());
			}
		}


		InterConnects cons;
		try {
			cons = new InterConnects(cfg);
		} catch (IOException e) {
			Utils.debugPrint(() -> "InterConnects IOException." + e.getMessage());
			return;
		}

		// master's work
		if (cfg.isMaster) {
			try {
				Object[] works = Reflection.getWork(cfg.jarByteCode).toArray();

				if (works.length != 0) {
					for (Object obj : works) {
						Work work = (Work) obj;
						work.starter();
						Worker.startWorkers(cons);
						work.ender();
					}
				} else {
					System.out.println("Couldn't find valid work.");
					System.exit(-1);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// worker's work
		if (!cfg.isMaster) {
			byte[] jarCode = cons.getJarCode();
			cfg.jarByteCode = jarCode;

			if (jarCode == null) System.out.println("failed getJarCode");
			else System.out.println("jarCode:" + jarCode.length);
			try {
				JarByteClassLoader.loadJarFile(jarCode);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}

			Worker.startWorkers(cons);

			/* ***** Socket Benchmark ***** */
			/*
			int count;
			Scanner scanner = new Scanner(System.in);
			count = scanner.nextInt();
			long start = System.currentTimeMillis();

			for (int i = 0; i < count; i++) {
				cons.stealTask();
			}

			System.out.println("time:" + Long.toString(System.currentTimeMillis() - start));
			*/

			/* ***** Serialization Benchmark ***** */
			/*
			Message msg = MessageFactory.newMessage(MESSAGE_TYPE.JOB_REQUEST);
			ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
			MessageSerializer messageSerializer = new MessageSerializer();
			final int count = 1000000;
			byte[] bytes = null;

			for (int i = 0; i < count; i++) {
				try {
					bytes = objectMapper.writeValueAsBytes(msg);
					objectMapper.readValue(bytes, Message.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (int i = 0; i < count; i++) {
				try {
					bytes = messageSerializer.serialize(msg);
					messageSerializer.deserialize(bytes);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			long start = System.currentTimeMillis();

			for (int i = 0; i < count; i++) {
				try {
					bytes = objectMapper.writeValueAsBytes(msg);
					objectMapper.readValue(bytes, Message.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("time:" + Long.toString(System.currentTimeMillis() - start));
			System.out.println("len:" + Integer.toString(bytes.length));
			start = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				try {
					bytes = messageSerializer.serialize(msg);
					messageSerializer.deserialize(bytes);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("time:" + Long.toString(System.currentTimeMillis() - start));
			System.out.println("len:" + Integer.toString(bytes.length));
			*/
		}

		return;
	}
}
