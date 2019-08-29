/**
 * 
 */
package com.dinapin.orderdish.sqliteComJdbc.common;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.dinapin.orderdish.sqliteComJdbc.enums.RuntimeBitEnum;
import com.dinapin.orderdish.sqliteComJdbc.utils.PropertiesUtil;
import com.dinapin.orderdish.sqliteComJdbc.utils.SystemUtils;

/**
 * 环境初始化
 * <li>检查c#运行环境</li>
 * <li>将dll等拷贝到java运行环境下</li>
 * <li>将DLL注册成COM组件</li>
 * 
 * @author dingwenbin
 *
 */
public class EnvInitialization {

	private static Logger logger = Logger.getLogger(EnvInitialization.class);
	
	public static String JdkBinPath = null;
	
	/**
	 * 初始化方法
	 * 
	 */
	public static void init(String jdkPath, RuntimeBitEnum bits) {
		
		try {
			logger.info("Start to environment initialization");
			
			//1. 注册dll为COM并且加入缓存，需要执行regasm和gacutil命令，需要安装.net framework
			checkNetFramework();
			
			//2. 未注册, 将注册COM所需胡dll从jar或者文件夹中拷贝到jdk的bin目录
			prepareComDll(jdkPath, bits);
			
			//3. 检查COM组件是否已经注册
			boolean isRegistered = checkCOMRegistered();
			
			if(isRegistered) {
				logger.info("检查到COM组件已经注册, 不在注册");
				return;
			}
			
			//4. 生成COM注册的命令
			List<String> registerCmdList = genRegisterCmdList(bits);
			
			try {
				
				//5. 执行COM注册命令
				execRegisterCmdList(registerCmdList);
				
				//6. 检查注册结果
				isRegistered = checkCOMRegistered();
				if(!isRegistered) {
					
					throw new Exception("在注册表中未检查到COM组件");
				}
				
				logger.info("Success to environment initialization");
			} catch(Exception e) {
				
				// 程序注册COM组件失败, 将命令写入bat文件,并提醒用户手动执行
				String batFile = genRegisterBat(registerCmdList);
				UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.ITALIC, 15)));
				UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.ITALIC, 18)));
				JOptionPane.showMessageDialog(null, String.format("注册COM组件失败，请找到文件 \"%s\", 右键单击该文件并选择【以管理员身份运行】, 进行手动注册.", batFile));
			}
		} catch (Exception e) {
			
			logger.error("Failed to init environment", e);
		}
	}
	
	/**
	 * 检查.net framework是否安装
	 * 
	 * @throws Exception
	 */
	private static void checkNetFramework() throws Exception {
		
		logger.info("Start to check .net framework");
		
		try {
			
			//检查是否存在%windir%\System32\MSCorEE.dll文件
			File file = new File(PropertiesUtil.DOTNET_CORE_DLL_PATH);
			if(!file.exists()) {
				
				logger.warn("没找到.net framwork");
				installNetFramework();
			}
			
			//检查HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\.NETFramework
			Process ps = Runtime.getRuntime().exec("reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\.NETFramework\\Policy");
			ps.getOutputStream().close();
			InputStreamReader i = new InputStreamReader(ps.getInputStream());
			String line; 
            BufferedReader ir = new BufferedReader(i);    
            boolean isInstalled = false;
            while ((line = ir.readLine()) != null) {    
                if(StringUtils.contains(line, "v4")) {
                	isInstalled = true;
                	break;
                }
            }
            
            if(!isInstalled) {
            	installNetFramework();
            }
            
		} catch (Exception e) {
			
			logger.error("Failed to check .Net framework installed state", e);
			throw new Exception("Failed to check .Net Framework");
		}
	}
	
	/**
	 * 执行注册COM组件的命令
	 * 
	 * @param registerCmdList
	 */
	private static void execRegisterCmdList(List<String> registerCmdList) throws Exception {
		
		Runtime rt = Runtime.getRuntime();
		for(String registerCmd : registerCmdList) {
				
			logger.info("Start to run command:  " + registerCmd);
			Process p = rt.exec(registerCmd);
			logger.info("register result: " + p.toString());
			if(!printAndCheck(p)) {
					
				throw new Exception("Failed to run command : " + registerCmd);
			}
		}
	}

	/**
	 * 生成COM组件注册和全局缓存添加的命令
	 * @param bits
	 * @return
	 * @throws Exception
	 */
	private static List<String> genRegisterCmdList(RuntimeBitEnum bits) throws Exception {
		
		List<String> commndList = new ArrayList<>();
		String dllPath = String.format("%s\\%s.dll", JdkBinPath, PropertiesUtil.DLL_COM_NAME);
		String tlbPath = String.format("%s\\%s.tlb", JdkBinPath, PropertiesUtil.DLL_COM_NAME);
		String sqliteDllPath = String.format("%s\\%s", JdkBinPath, PropertiesUtil.SQLITE_DLL_NAME);
		String newtonsoftDllPath = String.format("%s\\%s", JdkBinPath, PropertiesUtil.NEWTONSOFT_JSON_DLL);
		
		//执行regasm.exe将dll注册为COM
		List<String> regAsmCmdList = genRegAsmCmd(bits, dllPath, tlbPath);
		commndList.addAll(regAsmCmdList);
		
		//执行gacutil.exe将程序集加入缓存
		List<String> gacutilCmdList = genGacutilCmd(dllPath, sqliteDllPath, newtonsoftDllPath);
		commndList.addAll(gacutilCmdList);
				
		return commndList;
	}
	
	/**
	 * 准备dll和必须的文件
	 * 
	 * @throws Exception
	 */
	private static void prepareComDll(String jdkBinPath, RuntimeBitEnum bits) throws Exception {
		
		if(StringUtils.isBlank(jdkBinPath)) {
			jdkBinPath = System.getProperty("java.home");
			jdkBinPath = String.format("%s\\bin", StringUtils.substringBefore(jdkBinPath, "\\jre"));
		}
		JdkBinPath = jdkBinPath;
		if(StringUtils.isBlank(jdkBinPath)) {
			
			logger.error("没有找到jdk安装路径");
			throw new Exception("Filed to find path of jdk");
		}
		
		logger.info("Found jdk path:" + jdkBinPath);
		
		try {
			
			copyFiles(jdkBinPath, bits);
		} catch (Exception e) {
			
			logger.error("Failed to copy files", e);
			throw new Exception("Failed to prepare dll files");
		}
		
	}
	
	/**
	 * 生成dll注册为COM组件所需要的bat脚本
	 * 
	 * @param registerCmdList
	 * @return
	 * @throws Exception
	 */
	public static String genRegisterBat(List<String> registerCmdList) throws Exception {
		
		logger.info("Start to prepare register bat file");
		FileWriter writer = null;
		BufferedWriter bw = null;
		String batFilePath;
		try {
			
			batFilePath = String.format("%s\\%s",System.getProperty("user.home"), PropertiesUtil.BAT_FILE_NAME);
			writer = new FileWriter(batFilePath);
	        bw = new BufferedWriter(writer);
			for(String command : registerCmdList) {
				bw.append(command);
				bw.newLine(); 
			}
			bw.append("pause");
		} catch(Exception e) {
			
			logger.error("Failed to prepare register bat file", e);
			throw new Exception(e);
		} finally {
			if(bw != null) {
				bw.close();
			}
			if(writer != null) {
				writer.close();
			}
		}
		return batFilePath;
	}
	
	/**
	 * 生成gacutil命令
	 * 
	 * @param dllPaths
	 * @return
	 */
	private static List<String> genGacutilCmd(String... dllPaths) {
		
		String gacutilPath = String.format("%s\\gacutil.exe", JdkBinPath);
		List<String> gacutilShellList = new ArrayList<>();
		for(String dllPath : dllPaths) {
			
			gacutilShellList.add(String.format("\"%s\" -if \"%s\"", gacutilPath, dllPath));
		}
		
		return gacutilShellList;
	}

	/**
	 * 生成regasm命令
	 * 
	 * @param bits
	 * @param dllPath
	 * @param tlbPath
	 * @return
	 * @throws Exception
	 */
	private static List<String> genRegAsmCmd(RuntimeBitEnum bits, String dllPath, String tlbPath) throws Exception {
		
		String regAsmCLeanShell = null;
		String regAsmShell = null;
		
		File netFrameworkPath;
		if(bits.equals(RuntimeBitEnum.BITS_64)) {
			netFrameworkPath = new File(String.format("%s\\%s", PropertiesUtil.DOTNET_DEFAULT_PATH, "Framework64"));
		} else {
			netFrameworkPath = new File(String.format("%s\\%s", PropertiesUtil.DOTNET_DEFAULT_PATH, "Framework"));
		}
			
		if(!netFrameworkPath.exists()) {
			throw new Exception("Failed to find net framework");
		}
			
		File lastVersionFile = null;
		File[] files = netFrameworkPath.listFiles();
		for(File file : files) {
			if(file.isDirectory() && StringUtils.startsWithIgnoreCase(file.getName(), "v")) {
				
				if(lastVersionFile == null || lastVersionFile.getName().compareTo(file.getName()) < 0) {
					
					lastVersionFile = file;
				}
			}
		}
			
		String regAsmPath = String.format("%s\\%s", lastVersionFile.getPath(), "RegAsm.exe");
		
		regAsmCLeanShell = String.format("\"%s\" \"%s\" /u", regAsmPath, dllPath);
		regAsmShell = String.format("\"%s\" \"%s\" /tlb:\"%s\"", regAsmPath, dllPath, tlbPath);
		
		return Arrays.asList(regAsmCLeanShell, regAsmShell);
		
	}
	
	/**
	 * 检查注册结果
	 * 
	 * @throws Exception
	 */
	private static boolean checkCOMRegistered() throws Exception {
		
		logger.info("Start to check COM 'SqliteCom2Dll.DBOperation2' exist.");
		
		Process ps = Runtime.getRuntime().exec("reg query HKEY_CLASSES_ROOT\\SqliteCom2Dll.DBOperation2");
		ps.getOutputStream().close();
		InputStreamReader i = new InputStreamReader(ps.getInputStream());
		BufferedReader ir = new BufferedReader(i);
			
		boolean isRegistered = false;
		String line;
		while ((line = ir.readLine()) != null) {    
             if(StringUtils.isNotBlank(line)) {
                isRegistered = true;
                break;
             }
        }
		
		return isRegistered;
	}
	
	/**
	 * 安装NetFramwork
	 * 
	 * @throws Exception
	 */
	private static void installNetFramework() throws Exception {
		
		if(!SystemUtils.confirmDialog()) {
			return;
		}
		
		String fileName = StringUtils.substring(PropertiesUtil.DOTNET_DOWNLOAD, PropertiesUtil.DOTNET_DOWNLOAD.lastIndexOf("/") + 1, PropertiesUtil.DOTNET_DOWNLOAD.length());
		String filePath = String.format("%s\\Downloads", SystemUtils.getSystemUserPath());
		
		String fileFullPath = null;
        try {
        	
        	fileFullPath = String.format("%s\\%s", filePath, fileName);
        	
        	if(!SystemUtils.isFileExist(fileFullPath)) {
        		// 文件同步下载
            	SystemUtils.fileDownload(PropertiesUtil.DOTNET_DOWNLOAD, filePath, fileName);
        	}
        	
        } catch(Exception e) {
        	
        	logger.error("Failed to install " + fileFullPath, e);
        	throw new Exception(e);
        } finally {
        	
        	UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("宋体", Font.ITALIC, 15)));
			UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.ITALIC, 18)));
        	JOptionPane.showMessageDialog(null, ".Net Framework已经在下载中，请稍后手动安装，安装包所在目录：" + filePath);
        }
        
        
        logger.info("Success to install filePath");
	}
	
	/**
	 * 将dll等文件从jar或者目录下拷贝到jdk的bin目录下
	 * 
	 * @param destPath
	 * @param bits
	 * @throws Exception
	 */
	private static void copyFiles(String destPath, RuntimeBitEnum bits) throws Exception {
		
		String classResourceName = EnvInitialization.class.getName().replace(".", "/") + ".class";
		URL classResourceURL = EnvInitialization.class.getClassLoader().getResource(classResourceName);
		String classResourcePath = classResourceURL.getPath();
		logger.info("#### " + classResourceURL.getProtocol());
		if(classResourceURL.getProtocol().equals("file")) {
			
			logger.info("Copy file from resource directory");
			String classesDirPath = classResourcePath.substring(classResourcePath.indexOf("/") + 1, classResourcePath 
	                    .indexOf(classResourceName)); 
			copyFileFromDir(classesDirPath, bits, destPath);
		} else {
			
			logger.info("Copy file from jar");
			String jarPath = classResourcePath.substring(classResourcePath.indexOf("/"), classResourceURL.getPath() 
                    .indexOf("!"));
			jarPath = jarPath.substring(1, jarPath.length());
			copyFileFromJar(jarPath, bits, destPath);
		}
		
	}
	
	/**
	 * 从目录中拷贝
	 * 
	 * @param dirPath
	 * @param bits
	 * @param destPath
	 * @throws Exception
	 */
	private static void copyFileFromDir(String dirPath, RuntimeBitEnum bits, String destPath) throws Exception {
		
		logger.info("Start to copy from directory: " + dirPath);
		dirPath = String.format("%s\\%s", dirPath, bits.getDirName());
		File file = new File(dirPath);
		if(!file.exists()) {
			logger.error("Failed to found directory " + dirPath);
			throw new Exception("Failed to copy files");
		}
		
		String[] filePath = file.list();
		
		
		for (int i = 0; i < filePath.length; i++) {
			
            File sourceFile = new File(dirPath  + file.separator + filePath[i]);
            File destFile = new File(destPath + file.separator + filePath[i]); 
            if (sourceFile.isFile()) {
            	logger.info(String.format("Copy file from %s to %s", sourceFile.getPath(), destFile.getPath()));
            	Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
	}
	
	/**
	 * 从jar包中拷贝文件
	 * 
	 * @param jarPath
	 * @param bits
	 * @param destPath
	 * @throws Exception
	 */
	private static void copyFileFromJar(String jarPath, RuntimeBitEnum bits, String destPath) throws Exception {
		
		logger.info("Start to copy from jar: " + jarPath);
		JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, "UTF-8")); 
        Enumeration jarEntries = jarFile.entries();
        
        InputStream inputStream = null;
        OutputStream outputStream = null;
        byte[] buffer;
        int readBytes;
        
        while (jarEntries.hasMoreElements()) {
        	
            JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
            if(!jarEntry.isDirectory() && jarEntry.getName().startsWith(bits.getDirName())) {
            	
            	String fileName = StringUtils.substringAfterLast(jarEntry.getName(), "/");
            	if(StringUtils.isBlank(fileName)) {
            		
            		continue;
            	}
            	
            	String destFileName = String.format("%s\\%s", destPath, fileName);
            	
            	if(isFileExist(destFileName)) {
            		continue;
            	}
            	
            	logger.info(String.format("Copy file %s to %s", jarEntry.getName(), destFileName));
            	
            	inputStream = jarFile.getInputStream(jarEntry);
            	outputStream = new FileOutputStream(destFileName);
            	
            	buffer = new byte[1024];
            	try {
                    while ((readBytes = inputStream.read(buffer)) != -1) {
                    	
                    	outputStream.write(buffer, 0, readBytes);
                    }
                } finally {
                	
                	outputStream.close();
                	inputStream.close();
                }
            }
        }
	}
	
	/**
	 * 检查
	 * @param p
	 * @return
	 * @throws Exception
	 */
	private static boolean printAndCheck(Process p) throws Exception{
		
		InputStreamReader stdIs = null;
		InputStreamReader errorIs = null;
        BufferedReader stdbr = null;
        BufferedReader errorbr = null;
        String line;
       
        boolean isRegisterSuccess = true;
        try {
        	stdIs = new InputStreamReader(p.getInputStream(), "GB2312");
        	errorIs = new InputStreamReader(p.getErrorStream(),  "GB2312");
        	stdbr = new BufferedReader(stdIs);
        	errorbr = new BufferedReader(errorIs);
        	
        	while ((line = stdbr.readLine()) != null) {
	        	logger.info("### " + line);
	        }
        	
        	while((line = errorbr.readLine()) != null) {
        		logger.info("### " + line);
        		if(StringUtils.containsIgnoreCase(line, "error") || StringUtils.contains(line, "失败")) {
        			isRegisterSuccess = false;
        		}
        	}
        } catch (Exception e) {
        	logger.error("Failed get inputstream of process", e);
        	throw new Exception(e);
        } finally {
        	
        	if(stdIs != null) {
        		stdIs.close();
        	}
        	
        	if(errorIs != null) {
        		errorIs.close();
        	}
        	
        	if(stdbr != null) {
        		stdbr.close();
        	}
        	
        	if(errorbr != null) {
        		errorbr.close();
        	}
        }
        
        return isRegisterSuccess;
	}
	
	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	private static boolean isFileExist(String filePath)
    {
        try {
        	
        	File f=new File(filePath);
            if(!f.exists()){
                    return false;
            }
        }
        catch (Exception e){
            return false;
        }
 
        return true;
    }

}
