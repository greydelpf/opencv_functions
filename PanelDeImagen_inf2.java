
		import java.awt.Color;
		import java.awt.Graphics;
		import java.awt.Image;
		import java.awt.event.MouseEvent;
		import java.awt.event.MouseListener;
		import java.io.File;

		import javax.swing.ImageIcon;
		import javax.swing.JFileChooser;
		import javax.swing.JFrame;
		import javax.swing.JLabel;
		import javax.swing.JPanel;
		import org.opencv.core.Core;
		import org.opencv.core.Mat;
		import org.opencv.core.Scalar;
		import org.opencv.core.Size;
		import org.opencv.highgui.HighGui;
		import org.opencv.imgcodecs.Imgcodecs;
		import org.opencv.imgproc.Imgproc;


		public class PanelDeImagen_inf2 extends JPanel implements MouseListener{
		static final long serialVersionUID=10000;
		public String src;

		public Image img;
		public Mat img_mat;
		int xi = 0;
		int yi = 0;
		int x=153;
		int y=153*3/4;

		// **** creador de la clase
		PanelDeImagen_inf2(int xp, int yp, int w)
		{
			setBackground(Color.white);
			this.setToolTipText("Imagen original, clic para seleccionar color de muestreo.");
			this.addMouseListener(this);
			this.img_mat = new Mat();
			JFileChooser selector = new JFileChooser();
			 
			  selector.addChoosableFileFilter(new FiltrodeArchivo("gif","Archivos Gif"));
			  String lista[] = {"jpeg","jpg"};
			  selector.addChoosableFileFilter(new FiltrodeArchivo(lista,"Archivos JPEG"));
			  selector.setDialogTitle("Abrir archivo de imagen");
			  selector.setDialogType(JFileChooser.OPEN_DIALOG);
			  selector.setCurrentDirectory(new File("c:/inventarios/fotos"));
			  
			  int resultado = selector.showOpenDialog(null);
			  
			  if(resultado == JFileChooser.APPROVE_OPTION)
			  {
			   
			   String ruta = selector.getSelectedFile().getPath();
			   this.img_mat = Imgcodecs.imread(ruta);
			   this.img_mat = resizeMat_640x480(img_mat);
				this.img = HighGui.toBufferedImage(img_mat);
				
			   } else {
				   
				   this.img=null;
				   this.img_mat = null;
			   }
			
			
			
			
		}

		// **** pintar el objeto
		public void paintComponent(Graphics g)
		{
		super.paintComponent(g);

		if (img != null)
			{
			
			if (img.getWidth(this)>this.getWidth()) {
				img = img.getScaledInstance( this.getWidth() , -1 , Image.SCALE_AREA_AVERAGING);
			}
			
			
			if (img.getHeight(this)>this.getHeight()) {
				img = img.getScaledInstance( -1 , this.getHeight() , Image.SCALE_AREA_AVERAGING);
			}
			
			xi = (this.getWidth()-img.getWidth(this))/2;
			yi = (this.getHeight()-img.getHeight(this))/2;
			
			g.drawImage(img, xi,yi, this);	
			g.setColor (new Color(0,0,255)); //azul
			g.drawLine(0, this.y, this.x-5, this.y);
			g.drawLine(this.x+5, this.y, 600, this.y);
			g.drawLine(this.x, 0, this.x, this.y-5);
			g.drawLine(this.x, this.y+5, this.x, 600);
			g.drawRect(x-5, y-5, 10, 10);
			g.setColor (new Color(255,0,0));
			g.drawRect(x-7, y-7, 14, 14);
			}
		}

		@SuppressWarnings("static-access")
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			this.x = arg0.getX();
			this.y = arg0.getY();
			this.repaint();

			//el lugar donde debo tomar la muestra en la imagen, centro de la ventana de semilla
			Integer xreal;
			Integer yreal;
			
			// la ventana para calcular el valor de semilla
			Integer xmin;
			Integer xmax;
			Integer ymin;
			Integer ymax;
			
			xreal = ((x-xi)*480/this.img.getWidth(this));
			yreal = ((y-yi)*640/this.img.getHeight(this));
			
			//definir los 4 margenes donde no puedo calcular ventana
			//izquierda
			if (xreal>2) {
				xmin = xreal - 2;
				xmax = xreal + 2;
			}else {
				xmin = 0;
				xmax = 4;
			}
			//derecha
			if (xreal < 478) {
				xmin = xreal - 2;
				xmax = xreal + 2;
			}else {
				xmin = 476;
				xmax = 480;
			}
			//arriba
			if (yreal > 2) {
				ymin = yreal - 2;
				ymax = yreal + 2;
			}else {
				ymin = 0;
				ymax = 4;
			}
			//debajo
			if (yreal < 638) {
				ymin = yreal - 2;
				ymax = yreal + 2;
			}else {
				ymin = 636;
				ymax = 640;
			}
			
			double minH=255;
			double maxH=-255;
			double minS=255;
			double maxS=-255;
			double minV=255;
			double maxV=-255;
			
			Mat frameHSV = new Mat();
			
			Imgproc.cvtColor(img_mat, frameHSV, Imgproc.COLOR_BGR2HSV);
				
			byte[] data = new byte[3];
			frameHSV.get(xreal, yreal, data);
			
			Integer H = (int) data[0];
			Integer S = (int) data[1];
			Integer V = 255+(int) data[2];
			
			// convertir src a otra src con la mascara
			
			Mat thresHSV = new Mat();
			
			Scalar desde = new Scalar(H-20,S-20,V-20); // valores inicio del rango permitido
			Scalar hasta = new Scalar(H+20,S+20,V+20); // valores fin del rango permitido
			
			Core.inRange(frameHSV, desde, hasta, thresHSV);
		   
			//System.out.print("HUE: " + H);
			//System.out.print(", SAT: " + S);
			//System.out.println(", VAL: " + V);
			
			Imgproc.blur(thresHSV, thresHSV, new Size(9, 9));
			
			Mat result = new Mat();
			this.img_mat.copyTo(result, thresHSV);
			
			probar("Mascara - resultado", result);
			

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public static void main(String[] args) {
			
			// en las tres lineas siguientes se abren las dlls
					
						
			System.load("C:\\java\\opencv\\build\\java\\x64\\" + Core.NATIVE_LIBRARY_NAME + ".dll");
			System.load("C:\\java\\opencv\\build\\bin\\" + "opencv_videoio_ffmpeg411_64.dll");
					
			JFrame ventana = new JFrame("Probar la clase");
			ventana.setBounds(10,10, 500,700);
			ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			PanelDeImagen_inf2 paneli = new PanelDeImagen_inf2(10,10,480);
			ventana.add(paneli);
			
			ventana.setVisible(true);
		}

		public static void probar(String texto, Mat imgp) {
			try {
				HighGui.destroyWindow(texto);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			JFrame prueba = new JFrame();
			prueba.setTitle(texto);
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

		public static Mat resizeMat_640x480(Mat m) {
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

		}
    