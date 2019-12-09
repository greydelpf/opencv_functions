import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class hsv_bkg_sust {
	
	public static Mat MatfromJPG () {
		
		// ******* permite crear una imagen matricial desde un jpg ********
		
		 Mat M = new Mat();
		
		 JFileChooser selector = new JFileChooser();
		 String lista[] = {"jpeg","jpg"};
		 selector.addChoosableFileFilter(new FiltrodeArchivo(lista,"Archivos JPEG"));
		 selector.setDialogTitle("Abrir archivo de imagen");
		 selector.setDialogType(JFileChooser.OPEN_DIALOG);
		 selector.setCurrentDirectory(new File("c:/"));
		 int resultado = selector.showOpenDialog(null);
		  
		  if(resultado == JFileChooser.APPROVE_OPTION){
		   
		   String ruta = selector.getSelectedFile().getPath();
		   M = Imgcodecs.imread(ruta);
		   
		   if (M.empty()) {
	           System.err.println("No se puede leer o procesar la imagen: " + ruta);
	           M = null;
		    }else {
		    	
		    	return M;
		    }
			
		  }else {
			  
			M = null;  
		  }
		  
		  return M;	  
	}
	
	public static Mat resizeMat_640x480(Mat m) {
		
		// convierte una imagen mat a 640x480
		
		Mat res = new Mat();
		Image imgr = HighGui.toBufferedImage(m);
		
		double nx = imgr.getWidth(null);
		double ny = imgr.getHeight(null);
		double escala;
		
		if ( nx >= ny ) {
			
			escala = 640/nx;
			
		}else {
			
			escala = 480/ny;
		}
		
		
		Imgproc.resize(m, res, new Size((int)nx*escala,(int)ny*escala), 0, 0, 0);
		
		return res;
	}

	public static Mat bgroung_by_luminosidad(Mat src, int param) {
		
		//devuelve una imagen mat sin background
		
		Mat resultado = new Mat();
		Mat frameHSV = new Mat();
	    Imgproc.cvtColor(src, frameHSV, Imgproc.COLOR_BGR2HSV);
	    
	    Integer valHmin = 0;
	    Integer valHmax = 255;
	    Integer valSmin = 0;
	    Integer valSmax = 255;
	    Integer valVmin = 0;
	    Integer valVmax =param;
	    
	    Scalar desde = new Scalar(valHmin,valSmin,valVmin); // valores inicio del rango permitido
	    Scalar hasta = new Scalar(valHmax,valSmax,valVmax); // valores fin del rango permitido
	    
	    Mat thresh = new Mat();
	    Core.inRange(frameHSV, desde, hasta, thresh);
	    
	    Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 1);
	    Imgproc.erode(thresh, thresh, new Mat(), new Point(-1, -1), 3);
	    
	    Imgproc.blur(thresh, thresh, new Size(5, 5));
	    
	    Core.bitwise_not(thresh, thresh);
	    
	    
	    src.copyTo(resultado, thresh);
		return resultado;
		
	}
	
	public static void probar(String texto, Mat imgp) {
		try {
			HighGui.destroyWindow(texto);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	    JFrame prueba = new JFrame();
	    prueba.setTitle(texto);
	    prueba.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JPanel panelp = new JPanel();
	    JLabel label1 = new JLabel();
	    ImageIcon imgi = new ImageIcon();
	    imgi.setImage(HighGui.toBufferedImage(imgp));
	    label1.setIcon(imgi);
	    panelp.add(label1);
	    prueba.getContentPane().add(panelp);
	    prueba.pack();
	    prueba.setVisible(true);
	    
	}
	
	public static void main(String[] args) {
		
		//cargar librerias para imagenes
		System.load("C:\\java\\opencv\\build\\java\\x64\\" + Core.NATIVE_LIBRARY_NAME + ".dll");
		
		// crear Mat y cargar imagen jpg desde archivo
		Mat imgMat_ini = new Mat();
		imgMat_ini = MatfromJPG();
		
		// resize a 640 x 480
		imgMat_ini = resizeMat_640x480(imgMat_ini);
		
		//procesos
		Mat img_foreground = new Mat();
		img_foreground  = bgroung_by_luminosidad(imgMat_ini,90);
		probar("Remover fondo 640x480 por valor HSV",img_foreground);
		
		
	}

}
