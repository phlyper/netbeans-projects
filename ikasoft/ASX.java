 package ikasoft;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * ASX.java
 * 
 * @author PHLYPER
 */
public class ASX extends JFrame implements ActionListener, ChangeListener {

	class Equalizer extends JPanel {

		private static final long serialVersionUID = 1L;
		public final static int MMax = 24;
		public final static int NMax = 15;
		public final static int TILE_WIDTH = 16;
		public final static int TILE_HEIGHT = 9;
		private Random random = new Random();

		private BufferedImage bufferedImage;
		private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
		private Date date = new Date();

		public Equalizer() {
			this.setSize(600, 300);
			this.setBackground(Color.WHITE);
			this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		}

		private int getRand() {
			return this.random.nextInt(NMax + 1);
		}

		@Override
		public void paint(Graphics g) {
			
			int[] equalizerValues = new int[MMax];
			
			for (int i = 0; i < MMax; i++) {
				equalizerValues[i] = getRand();
			}

			g = draw(g, equalizerValues);

			// Constructs a BufferedImage of one of the predefined image types.
			bufferedImage = new BufferedImage(getPreferredSize().width, getPreferredSize().height,
					BufferedImage.TYPE_INT_RGB);

			Graphics2D g2d = bufferedImage.createGraphics();

			g2d = (Graphics2D) draw(g2d, equalizerValues);

			g2d.dispose();

			date = new Date();
			String filename = String.format("%s %s %s %s", ASX.class.getPackage() != null ? ASX.class.getPackage().getName() : "thispackage", ASX.class.getSimpleName(), formatter.format(date), date.getTime());

			File screenshotDir = new File("screenshot");
			// if the directory does not exist, create it
			if (!screenshotDir.exists()) {
				try {
					screenshotDir.mkdir();
				} catch(SecurityException e) {
					e.printStackTrace();
				}
			}
			
			// Save as PNG
			File file = new File("screenshot/"+filename + ".png");
			try {
				ImageIO.write(bufferedImage, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(filename);
		}

		private Graphics draw(Graphics g, final int[] equalizerValues) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 600, 300);
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(0, 0, 599, 299);

			int L = 0, R = 0;
			String values = "";
			for (int i = 0; i < MMax; i++) {
				int x = equalizerValues[i];

				for (int j = 0; j < NMax; j++) {

					if (j < 5) {
						g.setColor(Color.RED);
					} else if (j >= 5 && j < 10) {
						g.setColor(Color.ORANGE);
					} else if (j >= 10) {
						g.setColor(Color.GREEN);
					}

					if (j < x) {
						g.drawRect(10 + (i >= MMax*0.5f ? 3 : 0) + (i * 19), 10 + (j * 12), TILE_WIDTH - 1, TILE_HEIGHT - 1);
					} else {
						g.fillRect(10 + (i >= MMax*0.5f ? 3 : 0) + (i * 19), 10 + (j * 12), TILE_WIDTH, TILE_HEIGHT);
					}
				}

				if (i < (MMax * 0.5f)) {
					L = L + (NMax - x);
				} else {
					R = R + (NMax - x);
				}

				values += String.format(Locale.getDefault(), "%s%02d", (values.length() > 0 ? " " : ""), (NMax - x));
				values += i == (MMax * 0.5f) - 1 ? " |" : "";
			}

			float ratio = MMax * 0.5f;
			float L_ratio = (float) L / (float) ratio;
			float R_ratio = (float) R / (float) ratio;

			g.setFont(new Font("DS-digital", Font.CENTER_BASELINE, 14));
			g.drawString(values, 40, 240);

			String s = String.format(Locale.getDefault(), "L = %03d ==> %06.3f \t|\t R = %03d ==> %06.3f", L, L_ratio,
					R, R_ratio);
			g.setFont(new Font("DS-digital", Font.CENTER_BASELINE, 20));
			g.drawString(s, 40, 260);

			System.out.println(values);
			System.out.println(s);

			for (int i = 0; i < MMax; i++) {

				if ((0 <= i && i < 3) || (19 < i && i <= 23)) {
					g.setColor(Color.RED);
				} else if ((4 <= i && i < 7) || (15 < i && i <= 19)) {
					g.setColor(Color.ORANGE);
				} else if (8 <= i && i <= 14) {
					g.setColor(Color.GREEN);
				}

				if (i >= ((MMax * 0.5f) - L_ratio) && i < ((MMax * 0.5f) + R_ratio)) {
					g.fillRect(10 + (i >= MMax*0.5f ? 3 : 0) + (i * 19), 200, TILE_WIDTH, TILE_HEIGHT);
				} else {
					g.drawRect(10 + (i >= MMax*0.5f ? 3 : 0) + (i * 19), 200, TILE_WIDTH - 1, TILE_HEIGHT - 1);
				}
			}
			return g;
		}
	}

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		System.out.println(Locale.getDefault());
		Locale.setDefault(Locale.US);
		System.out.println(Locale.getDefault());
		new ASX();
	}

	private Equalizer equalizer = new Equalizer();
	private Timer t = null;
	private JSlider s = new JSlider(0, 5000, 1000);
	private JLabel l = new JLabel("Vitesse : " + s.getValue());
	private JButton b1 = new JButton("Strat"), b2 = new JButton("Stop");

	public ASX() {
		this.setTitle("| ..:: PHLYPER _ AQ 6483 _ Equalizer ::.. |");
		this.setSize(850, 500);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setLayout(new FlowLayout());

		this.getContentPane().add(equalizer);
		this.getContentPane().add(s);
		this.getContentPane().add(b1);
		this.getContentPane().add(b2);
		this.getContentPane().add(l);

		Font f = new Font("DS-digital", Font.CENTER_BASELINE, 14);
		l.setFont(f);

		equalizer.setPreferredSize(new Dimension(600, 300));
		equalizer.setForeground(Color.BLACK);
		equalizer.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

		s.setPaintTicks(true);
		s.setPaintLabels(true);
		s.setPaintTrack(true);
		s.setMinorTickSpacing(100);
		s.setMajorTickSpacing(500);
		s.setValueIsAdjusting(true);
		s.setSnapToTicks(true);
		s.setPreferredSize(new Dimension(800, 100));

		s.addChangeListener(this);
		b1.addActionListener(this);
		b2.addActionListener(this);
		t = new Timer(s.getValue(), this);
		// t.start();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof Timer) {
			if (e.getSource() == t) {
				equalizer.repaint();
			}
		}

		if (e.getSource() instanceof JButton) {
			if (e.getSource() == b1) {
				System.out.println("start");
				t.start();
			}
			if (e.getSource() == b2) {
				System.out.println("stop");
				t.stop();
			}
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JSlider) {
			if (e.getSource() == s) {
				t.setDelay(s.getValue());
				l.setText("Vitesse : " + s.getValue());
			}
		}
	}
}
