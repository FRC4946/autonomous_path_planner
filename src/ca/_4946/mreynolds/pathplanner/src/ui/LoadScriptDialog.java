package ca._4946.mreynolds.pathplanner.src.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ca._4946.mreynolds.pathplanner.src.PathPlanner;
import ca._4946.mreynolds.pathplanner.src.data.ScriptBundle;
import ca._4946.mreynolds.pathplanner.src.io.FileIO;

public class LoadScriptDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	ScriptBundle bundle = null;
	String src = "ll";
	FieldPanel field;

	/**
	 * Create the dialog.
	 */
	public LoadScriptDialog() {
		setSize(900, 500);
		setTitle("Load Script From File");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane();
		{

			File root = new File(FileIO.DEFAULT_DIR);
			FileSystemView fsv = new SingleRootFileSystemView(root);
			JFileChooser fc = new JFileChooser(fsv);
			fc.setFileFilter(new FileNameExtensionFilter("XML", "xml"));
			fc.setControlButtonsAreShown(false);
			fc.addActionListener(e -> {
				if (fc.getSelectedFile() == null)
					return;

				try {
					bundle = FileIO.loadScript(fc.getSelectedFile());
					updateField();
				} catch (ParserConfigurationException | SAXException | IOException e1) {
					e1.printStackTrace();
				}
			});

			field = new FieldPanel(false);

			splitPane.setLeftComponent(fc);
			splitPane.setRightComponent(field);
		}

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		{

			JPanel sourcePane = new JPanel();
			{
				ButtonGroup srcGrp = new ButtonGroup();

				JRadioButton fromLL = new JRadioButton("LL");
				JRadioButton fromLR = new JRadioButton("LR");
				JRadioButton fromRL = new JRadioButton("RL");
				JRadioButton fromRR = new JRadioButton("RR");

				srcGrp.add(fromLL);
				srcGrp.add(fromLR);
				srcGrp.add(fromRL);
				srcGrp.add(fromRR);

				fromLL.setSelected(true);

				fromLL.addActionListener(e -> setSrc("ll"));
				fromLR.addActionListener(e -> setSrc("lr"));
				fromRL.addActionListener(e -> setSrc("rl"));
				fromRR.addActionListener(e -> setSrc("rr"));

				sourcePane.add(new JLabel("Copy From: "));
				sourcePane.add(fromLL);
				sourcePane.add(fromLR);
				sourcePane.add(fromRL);
				sourcePane.add(fromRR);
			}

			JPanel destPane = new JPanel();
			{

				JButton toLL = new JButton("LL");
				JButton toLR = new JButton("LR");
				JButton toRL = new JButton("RL");
				JButton toRR = new JButton("RR");

				toLL.addActionListener(e -> copyTo("ll"));
				toLR.addActionListener(e -> copyTo("lr"));
				toRL.addActionListener(e -> copyTo("rl"));
				toRR.addActionListener(e -> copyTo("rr"));

				destPane.add(new JLabel("Copy To: "));
				destPane.add(toLL);
				destPane.add(toLR);
				destPane.add(toRL);
				destPane.add(toRR);
			}

			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(sourcePane);
			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(destPane);
			buttonPane.add(Box.createHorizontalGlue());
		}

		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}

	private void updateField() {
		field.setScript(bundle.getScript(src), src);
		field.repaint();
	}

	private void setSrc(String data) {
		src = data;
		updateField();
	}

	private void copyTo(String dest) {
		PathPlanner.main.setScript(bundle.getScript(src), dest);
	}
}
