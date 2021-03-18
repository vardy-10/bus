package com.zah.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * 项目日志类（整理打印项目日志）
 * 
 * @author fang
 * @version 1.0.2
 */
public class ProjectLog {
	private SimpleDateFormat df4name = new SimpleDateFormat("yyyyMMdd-HHmmssSS");
	private SimpleDateFormat df4content = new SimpleDateFormat("HH:mm:ss:SS");
	private SimpleDateFormat df4date = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
	private long multiple = 1024 * 1024;

	private String logDir;// 日志存放的目录路径
	private String suffix = ".log";// 日志文件的后缀名
	private String separator = "~";// 日志文件中信息间的分隔符
	private long limit = 30 * multiple;// 日志文件可写的最大字节数
	private long upperLimit = 1024 * multiple;// 日志目录可容纳日志的最大字节数
	private long lowerLimit = upperLimit / 2;// 日志目录清理时保留日志的最小字节数
	private boolean isDebug = false;// 是否进行调试

	private File file;
	private String saveDate;
	private long length = 0;
	private long total = 0;
	private long overtime = 3000;
	private boolean isOvertime = false;

	/**
	 * 构造函数
	 * 
	 * @param logDir 日志存放的目录路径（不存在，会自动创建）
	 */
	public ProjectLog(String logDir) {
		this.logDir = logDir;
		total = getDirLength();
	}

	/**
	 * 设置日志文件的后缀名（可以为空字符串，但不能为null，建议不要为空字符串，默认为“.log”）
	 * 
	 * @param suffix
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix.length() == 0 || suffix.charAt(0) == '.') ? suffix : "." + suffix;
	}

	/**
	 * 设置日志文件中信息间的分隔符（不能为null，建议不要为空字符串，默认为“~”）
	 * 
	 * @param separator
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * 设置日志目录及文件的大小限制
	 * 
	 * @param fileLimit  日志文件可写的最大字节数（单位为M，设置为0或负数表示不限制，建议不要设为不限制，默认为30，即30M）
	 * @param upperLimit 日志目录可存储的最大字节数（单位为M，设置为0或负数为无效，无效会自动使用默认值，默认为1024，即1G）
	 * @param lowerLimit 日志目录需保留的最小字节数（单位为M，必须小于upperLimit，否则会自动默认为upperLimit的一半，默认为upperLimit的一半，即512M）
	 */
	public void setLimit(int fileLimit, int upperLimit, int lowerLimit) {
		this.limit = fileLimit > 0 ? fileLimit * multiple : 0;
		if (upperLimit > 0) {
			this.upperLimit = upperLimit * multiple;
		}
		this.lowerLimit = lowerLimit < upperLimit ? lowerLimit * multiple : upperLimit / 2 * multiple;
	}

	/**
	 * 设置是否进行调试
	 * 
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	/**
	 * 清空日志目录中的日志文件（目录中的文件夹及其下属所有文件都会被清除）
	 */
	public void clear() {
		clearDir(new File(logDir));
	}

	/**
	 * 写入普通信息
	 * 
	 * @param content
	 */
	public void writeInfo(String content) {
		write("info", content);
	}

	/**
	 * 写入错误信息
	 * 
	 * @param e
	 */
	public void writeError(Exception e) {
		write("error", getStackTrace(e));
	}

	private void write(String type, String content) {
		Date date = new Date();
		String dateStr = df4date.format(date);
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(type);
		sb.append("]\t");
		sb.append(Thread.currentThread().getStackTrace()[3].getFileName());
		sb.append(separator);
		sb.append(Thread.currentThread().getStackTrace()[3].getLineNumber());
		sb.append(separator);
		sb.append(df4content.format(date));
		sb.append(separator);
		sb.append(content);
		sb.append("\n");
		if (isDebug) {
			System.out.println(sb);
			return;
		}
		synchronized (this) {
			if (!reduce()) {
				System.out.println(
						"[" + df.format(new Date()) + "] ProjectLog无法使日志目录的文件低于上限导致写入停止，可能由于参数设置错误或老的日志文件无法被删除");
				return;
			}
			if (file == null || !dateStr.equals(saveDate) || limit > 0 && length > limit) {
				file = new File(logDir + File.separator + df4name.format(date) + suffix);
				saveDate = dateStr;
				length = 0;
			}
			File fileParent = file.getParentFile();
			if (!fileParent.isDirectory()) {
				if (!fileParent.mkdirs()) {
					file = null;
					System.out.println("[" + df.format(new Date()) + "] ProjectLog无法创建日志目录，可能由于无权限或者错误的路径："
							+ fileParent.getAbsolutePath());
					return;
				}
			}
		}
		byte[] by = sb.toString().getBytes();
		BufferedOutputStream buff = null;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file, true);
			buff = new BufferedOutputStream(out);
			buff.write(by);
			length += by.length;
			total += by.length;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			file = null;
			e.printStackTrace();
			System.out.println(
					"[" + df.format(new Date()) + "] ProjectLog无法创建日志文件，可能由于无权限或者错误的路径：" + file.getAbsolutePath());
		} finally {
			if (buff != null) {
				try {
					buff.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		long diffTime = System.currentTimeMillis() - date.getTime();
		if (diffTime > diffTime) {
			if (!isOvertime) {
				isOvertime = true;
				System.out.println("[" + df.format(new Date()) + "] ProjectLog写入信息超过" + overtime + "毫秒为" + diffTime);
			}
		} else {
			if (isOvertime) {
				isOvertime = false;
				System.out.println("[" + df.format(new Date()) + "] ProjectLog写入信息超过" + overtime + "毫秒已恢复");
			}
		}
	}

	private boolean reduce() {
		if (total < upperLimit) {
			return true;
		}
		total = 0;
		File dir = new File(logDir);
		File[] files = dir.listFiles();
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o2.getName().compareTo(o1.getName());
			}
		});
		boolean isRetain = true;
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(suffix)) {
				if (isRetain) {
					long sum = total + file.length();
					if (sum < lowerLimit) {
						total = sum;
						continue;
					} else {
						isRetain = false;
					}
				}
				if (!file.delete()) {
					total += file.length();
					System.out.println(
							"[" + df.format(new Date()) + "] ProjectLog无法删除日志文件，可能由于正被占用的文件：" + file.getAbsolutePath());
				}
			}
		}
		return total < upperLimit;
	}

	private long getDirLength() {
		long length = 0;
		File dir = new File(logDir);
		if (!dir.isDirectory()) {
			return length;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return length;
		}
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(suffix)) {
				length += file.length();
			}
		}
		return length;
	}

	private void clearDir(File dir) {
		if (!dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				clearDir(file);
			}
			file.delete();
		}
	}

	private String getStackTrace(Exception e) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		example();
	}

	private static void example() {
		ProjectLog pl = new ProjectLog(System.getProperty("user.dir") + "/logs");
		System.out.println("aa");
		pl.writeInfo("aa");
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			pl.writeError(e);
			e.printStackTrace();
		}
		pl.writeInfo("bb");
		System.out.println("bb");
	}
}
