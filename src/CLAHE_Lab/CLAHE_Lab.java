package CLAHE_Lab;


import java.awt.image.BufferedImage;

import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

class FileIO{
	File dir;
	
	public FileIO() {
		this.dir = new File("C:\\Users\\SMS\\Desktop\\picture");
		RnW_dir(dir);
	}
	
	
	public FileIO(File dir) {
		super();
		this.dir = dir;	
		RnW_dir(dir);
	}
	public void RnW_dir(File dir) {
		File[] input = dir.listFiles();
		for(File str : input) Clahe_apply(str);
	}
	
	public void Clahe_apply(File input) {
		double[] CL = new double[] {2.0, 3.0, 5.0, 10.0}; //ClipLimit
		double[] tSize = new double[] {2.0, 4.0, 8.0, 16.0}; //TileSize
		double cl1 = 10.0, tsize1 = 8.0;
		try {
			System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
			System.out.println(input);
			BufferedImage buffImage = ImageIO.read(input); 
			byte[] data = ((DataBufferByte) buffImage.getRaster().getDataBuffer()).getData();
			// build MAT for original image
			Mat orgImage = new Mat(buffImage.getHeight(),buffImage.getWidth(), CvType.CV_8UC3);
			orgImage.put(0, 0, data);
			// transform from to LAB
			Mat destImage = new Mat(buffImage.getHeight(), buffImage.getWidth(), CvType.CV_8UC4);
			// Opencv Reads IMG by BGR, Convert the IMG BGR to HSV
			Imgproc.cvtColor(orgImage, destImage, Imgproc.COLOR_BGR2Lab); 
			
			// apply CLAHE
			List<Mat> channels = new LinkedList<>();  // List to save HSV's channels
			List<Mat> Lchannels = new LinkedList<>(); // List of V of HSV needs to apply CLAHE
			List<Mat> ResultL = new LinkedList<>(); // Result of CLAHE applied IMG's V of HSV
			Core.split(destImage, channels);	// Split HSV's channels (H, S, V) -> we will use V
			Lchannels.add(channels.get(0)); // set the origin V channel
			
			for(double cl : CL) {
				for(double tsize : tSize) {
					// Create CLAHE with Set ClipLimit and Tile Size
					CLAHE clahe = Imgproc.createCLAHE(cl1, new Size(tsize1, tsize1));
					Mat HSVImage = new Mat(buffImage.getHeight(),buffImage.getWidth(), CvType.CV_8UC4);
					clahe.apply(Lchannels.get(0), HSVImage); // Apply Value(HSV) in IMG
					Core.split(HSVImage, ResultL);
					channels.set(0, ResultL.get(0)); // channels2's V값을 channels의 v값으로 대입
					Core.merge(channels, destImage); // channels를 다시 merge
					Imgproc.cvtColor(destImage, destImage, Imgproc.COLOR_Lab2BGR);
					Imgcodecs.imwrite(SavePath(input, (int)cl, (int)tsize), destImage);
					System.out.printf("Success CL : %d, TSize : %d\n", (int)cl, (int)tsize);
				}
			}
			
			
			
/*			// Create CLAHE with Set ClipLimit and Tile Size
			CLAHE clahe = Imgproc.createCLAHE(cl1, new Size(tsize1, tsize1));
			Mat HSVImage = new Mat(buffImage.getHeight(),buffImage.getWidth(), CvType.CV_8UC4);
			clahe.apply(channels.get(2), HSVImage); // Apply Value(HSV) in IMG
			Core.split(HSVImage, Vchannels);
			System.out.println(channels.size());
			channels.set(2, Vchannels.get(0)); // channels2's V값을 channels의 v값으로 대입
			Core.merge(channels, destImage); // channels를 다시 merge
			Imgproc.cvtColor(destImage, destImage, Imgproc.COLOR_HSV2BGR);
			Imgcodecs.imwrite(SavePath(input, (int)cl1, (int)tsize1), destImage);
			System.out.println("Success");*/
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	public String SavePath(File input, int CL, int size) {
		String img_path = String.valueOf(input);
		String[] img_split = img_path.split("\\\\");
		String[] file_name_arr = img_split[img_split.length-1].split("\\.");
		String file_name = "";
		String Save_path = "";
		String form = file_name_arr[file_name_arr.length-1];
		String name = "_CLAHE";
		   
		//make filename+_save
		for(int i=0;i<file_name_arr.length;i++){
			if(i==file_name_arr.length-1)
				file_name += (name+".");
		  	file_name += file_name_arr[i];
		}
		img_split[img_split.length-1] = file_name;
		  
		file_name = ""; // make file_name null;
		//make filename+_save
		for(int i=0;i<file_name_arr.length;i++){
			if(i==file_name_arr.length-1)
				file_name += "_CLAHE"+"_CL"+String.valueOf(CL)+"_S"+String.valueOf(size)+".";
			file_name += file_name_arr[i];
		}
		img_split[img_split.length-1] = file_name;
		for(int i=0;i<img_split.length;i++) {
			if(i != img_split.length-1) {
				Save_path += (img_split[i] + "\\");
			}
			else
				Save_path += img_split[i];
		} // Making Save_path Success
//		File save_addr = new File(Save_path);
		return Save_path;
	}
}



public class CLAHE_Lab {
	public static void main(String[] args) {
		new FileIO();
	}
}
