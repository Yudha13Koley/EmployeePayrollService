package com.capgemini.fileoperationsdemo;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class FileOperationDemoTest {
	private static String Home=System.getProperty("user.home");
	private static String PLAY_WITH_NIO="TempDemo";

	@Test
	public void GivenPathWhenCheckedReturnConfirm() throws IOException{
	//Checks if path exists
	Path homepath=Paths.get(Home);
	Assert.assertTrue(Files.exists(homepath));
	
	//Delete File and Check File Not Exist
	Path playPath=Paths.get(Home+"/"+PLAY_WITH_NIO);
	if(Files.exists(playPath))FileUtils.deleteFiles(playPath.toFile());
	Assert.assertTrue(Files.notExists(playPath));
	
	//Create Files Or Directory
	Files.createDirectory(playPath);
	Assert.assertTrue(Files.exists(playPath));
	
	//Create File
	IntStream.range(1, 10).forEach(a->{
		Path temp=Paths.get(playPath+"/temp"+a);
		Assert.assertTrue(Files.notExists(temp));
		try {
			Files.createFile(temp);
		}
		catch(IOException e) {
		}
		Assert.assertTrue(Files.exists(temp));
	});
	
	//List The Files
	Files.list(playPath).filter(Files :: isRegularFile).forEach(System.out::println);
	Files.newDirectoryStream(playPath).forEach(System.out::println);
	Files.newDirectoryStream(playPath,path->path.toFile().isFile()&&
			path.toString().startsWith("t")).forEach(System.out::println);
	}

}
