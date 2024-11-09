import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class App {
	private static String captureKey = "C";

	public static void main(String[] args) {
		JFrame frame = new JFrame("Color Picker");

		try (InputStream iconStream = App.class.getResourceAsStream("/img/pip-in.png")) {
			if (iconStream == null) {
				throw new IOException("Image non trouvée dans le chemin de ressources");
			}
			BufferedImage icon = ImageIO.read(iconStream);
			frame.setIconImage(icon);
		} catch (IOException e) {
			System.out.println("Erreur lors du chargement de l'icône : " + e.getMessage());
		}

		frame.setSize(350, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		JLabel colorLabel = new JLabel("Couleur:");
		JTextField colorField = new JTextField(10);
		colorField.setEditable(false);

		JLabel keyLabel = new JLabel("Appuyez sur une touche pour capturer la couleur:");
		JTextField keyField = new JTextField(captureKey, 5);
		keyField.setHorizontalAlignment(JTextField.CENTER);

		keyField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				captureKey = KeyEvent.getKeyText(e.getKeyCode());
				keyField.setText(captureKey);
			}
		});

		frame.add(colorLabel);
		frame.add(colorField);
		frame.add(keyLabel);
		frame.add(keyField);
		frame.setVisible(true);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getID() == KeyEvent.KEY_PRESSED && KeyEvent.getKeyText(e.getKeyCode()).equals(captureKey)) {
				try {
					Robot robot = new Robot();
					Point mousePos = MouseInfo.getPointerInfo().getLocation();
					Color color = robot.getPixelColor(mousePos.x, mousePos.y);
					String hexColor = "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();

					colorField.setText(hexColor);
					colorField.setBackground(color);

					StringSelection selection = new StringSelection(hexColor);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

					JOptionPane.showMessageDialog(frame, "Couleur copiée dans le presse-papier: " + hexColor);
				} catch (AWTException ex) {
					ex.printStackTrace();
				}
			}
			return false;
		});
	}
}
