import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import openSource.AlphanumComparator;

public class ImageInfo {
	public static class ImageData {
		private Dimension d;
		private String src;
		private String srcThumbnail;
		
		ImageData(Dimension _d, String _src) {
			d = _d;
			src = _src;
		}
		ImageData(Dimension _d, String _src, String _srcThumbnail) {
			this(_d, _src);
			srcThumbnail = _srcThumbnail;
		}
		
		public String toString() {
			String twoTabs = "\t\t";
			String result = twoTabs+ "\"src\": \"" + src + "\",\n" + 
					twoTabs + "\"msrc\": \"" + srcThumbnail + "\",\n" +
					twoTabs + "\"w\":" + this.d.width + ",\n" + 
					twoTabs + "\"h\":" + this.d.height;
			return "\t{\n" + result + "\n\t},";
		}
	}
	
	public static void main(String args[]) {
		String folder = "images/nature/";
		String folderString = folder.replace("/", "\\");
		String projectLocationString = "E:\\Docs\\HTML CSS Javascript programming\\irene-photography-bootstrap\\";
		ArrayList<File> files = getListOfFiles(new File(projectLocationString + folderString));
		
		Collections.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return new AlphanumComparator().compare(f1.getName(), f2.getName());
			}
			
		});
		
		String result = "[\n";
		for(File f : files) {
			if(isThumbnail(f)) {
				continue;
			}
			String src = folder + f.getName();
			String thumbnail = folder + findThumbnail(f, files);
			try {
				ImageData dat = new ImageData(getImageDimension(f), src, thumbnail);
				result = result + (dat) + "\n";
			}
			catch (Exception e) {
				System.out.println("error processing image dimensions");
			}
		}
		result = result.substring(0,result.lastIndexOf(","));
		result = result + "\n]";
		System.out.println(result);
	}
	
	public static boolean isThumbnail(File f) {
		Pattern p = Pattern.compile("_t\\d*.(jpg)|(jpeg)|(png)$");
		Matcher m = p.matcher(f.getName().toLowerCase());
		if(m.find()) {
			return true;
		}
		return false;
	}
	
	public static String findThumbnail(File sourceFile, ArrayList<File> files) {
		String name = sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf("."));
		for(File f : files) {
			if(isThumbnail(f) && f.getName().contains(name)) {
				return f.getName();
			}
		}
		return null;
	}
	
	public static ArrayList<File> getListOfFiles(File folder) {
		ArrayList<File> files = new ArrayList<File>();
		File[] listOfFiles = folder.listFiles();
	    for (File f : listOfFiles) {
	    	if (f.isFile()) {
		    	if (!f.getName().contains(".") || f.getName().contains(".db")) {
		    		continue;
		    	}
		    	files.add(f);
	      	}
	    }
		
		return files;
	}
	
	
	/**
	 * Gets image dimensions for given file 
	 * @param imgFile image file
	 * @return dimensions of image
	 * @throws IOException if the file is not a known image
	 */
	public static Dimension getImageDimension(File imgFile) throws IOException {
	  int pos = imgFile.getName().lastIndexOf(".");
	  if (pos == -1)
	    throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
	  String suffix = imgFile.getName().substring(pos + 1);
	  Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
	  if (iter.hasNext()) {
	    ImageReader reader = iter.next();
	    try {
	      ImageInputStream stream = new FileImageInputStream(imgFile);
	      reader.setInput(stream);
	      int width = reader.getWidth(reader.getMinIndex());
	      int height = reader.getHeight(reader.getMinIndex());
	      return new Dimension(width, height);
	    } catch (IOException e) {
	      System.out.println("Error reading: " + imgFile.getAbsolutePath());
	    } finally {
	      reader.dispose();
	    }
	  }

	  throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
	}
}
