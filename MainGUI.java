import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/******************************************
 * 
 * @author Bobby Apelian
 * This program is designed to be used for translating two port network parameters between different types.
 * It is a simple array of text fields that get filled with calculated data. 
 * @version 21.9.2
 * 
 ******************************************/

@SuppressWarnings("static-access")
public class MainGUI extends Application {

	// Declare panes of different layout types

	private Pane regularPane;

	private TextField text_s11, text_s12, text_s21, text_s22, text_z11, text_z12, text_z21, text_z22, text_y11,
			text_y12, text_y21, text_y22, text_A, text_B, text_C, text_D, text_z0;

	private TextField[] textfields;

	private Button button_s, button_z, button_y, button_abcd, button_clear, button_save, button_close, button_help,
			button_rect2polar;

	private Label label_s, label_z, label_y, label_abcd, label_z0;

	private MenuBar menuBar;
	private Menu menuFile;
	private MenuItem miAbout, miFilePath;

	private String filePath = "outputFile.csv";
	private File outputFile;

	// constructor
	public MainGUI() {
		setupMain();
	}

	private void setupMain() {
		menuFile = new Menu("File");
		miAbout = new MenuItem("About...");
		miAbout.setOnAction(e -> {
			Alert about = new Alert(AlertType.INFORMATION,
					"This calculator is intended to convert two port network parameters between different types. "
							+ "Designed by Bobby Apelian, ERAU '22. Batteries not included. Some assembly required. Designed in accordance with MIL-C-44072C.",
					ButtonType.OK);
			about.setHeaderText("Two Port Network Calculator - About");
			about.setTitle("About");
			about.showAndWait();
		});
		miFilePath = new MenuItem("Set File Path");
		miFilePath.setOnAction(e -> {
			TextInputDialog dialog = new TextInputDialog(filePath);
			dialog.setTitle("File Path");
			dialog.setHeaderText(
					"Please enter the new file path for this program to save to. \nNOTE: If no file with the name given exists at the file path, a new one will be created.");
			dialog.setContentText("New file path");
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(filePath -> setFilePath(filePath));
		});
		menuFile.getItems().addAll(miFilePath, new SeparatorMenuItem(), miAbout);
		menuBar = new MenuBar();
		menuBar.getMenus().addAll(menuFile);

		// Instantiate the panes
		regularPane = new Pane();
		
		// Run setups
		setupS();
		setupZ();
		setupY();
		setupABCD();

		textfields = new TextField[] { text_s11, text_s12, text_s21, text_s22, text_z11, text_z12, text_z21, text_z22,
				text_y11, text_y12, text_y21, text_y22, text_A, text_B, text_C, text_D };

		text_z0 = new TextField();
		text_z0.setPromptText("Z0");
		text_z0.setLayoutX(500);
		text_z0.setLayoutY(50);
		
		label_z0 = new Label("Z0");
		label_z0.setLayoutX(475);
		label_z0.setLayoutY(53);

		button_clear = new Button("Clear");
		button_clear.setLayoutX(900);
		button_clear.setLayoutY(400);
		button_clear.setTooltip(new Tooltip("Clears all fields except Z0"));
		button_clear.setOnAction(e -> {
			clearFields();
		});

		button_save = new Button("Save");
		button_save.setLayoutX(950);
		button_save.setLayoutY(400);
		button_save.setTooltip(new Tooltip("Saves data to a file at the specified file path"));
		button_save.setOnAction(e -> {
			try {
				outputFile = new File(filePath);
				FileWriter fw = new FileWriter(outputFile, true);
				fw.write("S,Z,Y,ABCD\n" + text_s11.getText() + "," + text_z11.getText() + "," + text_y11.getText() + ","
						+ text_A.getText() + ",\n" + text_s12.getText() + "," + text_z12.getText() + ","
						+ text_y12.getText() + "," + text_B.getText() + ",\n" + text_s21.getText() + ","
						+ text_z21.getText() + "," + text_y21.getText() + "," + text_C.getText() + ",\n"
						+ text_s22.getText() + "," + text_z22.getText() + "," + text_y22.getText() + ","
						+ text_D.getText() + ",\n" + text_z0.getText() + "\n\n\n");
				fw.flush();
				fw.close();
			} catch (IOException i) {
				Alert file_bad = new Alert(AlertType.ERROR,
						"There seems to be an error with file saving. Please make sure file path is valid.",
						ButtonType.OK);
				file_bad.showAndWait();
			}
		});

		button_close = new Button("Close");
		button_close.setLayoutX(1000);
		button_close.setLayoutY(400);
		button_close.setOnAction(e -> {
			Platform.exit();
		});

		button_help = new Button("Help");
		button_help.setLayoutX(50);
		button_help.setLayoutY(400);
		button_help.setTooltip(new Tooltip("Click for help"));
		button_help.setOnAction(e -> {
			Alert help = new Alert(AlertType.INFORMATION,
					"Please make sure to input all numbers in rectangular complex form, in the form 'A + jB' or 'A' or 'jB'. To convert between rectangular "
							+ "and polar coordinates, please press the 'Rect<->Polar' button. To calculate values, press the 'Calculate' button below the column of "
							+ "data that you already have.",
					ButtonType.OK);
			help.setHeaderText("Two Port Network Calculator - Help");
			help.setTitle("Help");
			help.showAndWait();
		});

		button_rect2polar = new Button("Rect<->Polar");
		button_rect2polar.setLayoutX(100);
		button_rect2polar.setLayoutY(400);
		button_rect2polar.setTooltip(new Tooltip("Opens rectangular to polar converter"));
		button_rect2polar.setOnAction(e->{
			Rect2Polar r2p = new Rect2Polar();
			try {
				r2p.start(new Stage());
			} catch (Exception e1) {
				Alert calc_bad = new Alert(AlertType.ERROR,
						"Could not launch converter. Please try again.",
						ButtonType.OK);
				calc_bad.showAndWait();
				e1.printStackTrace();
			}
		});

		regularPane.getChildren().addAll(text_z0, button_help, button_rect2polar, button_clear, button_save,
				button_close, label_z0);
	}

	private void setupS() {
		// Instantiate S stuff
		label_s = new Label("S");
		label_s.setLayoutX(255);
		label_s.setLayoutY(125);
		text_s11 = new TextField();
		text_s11.setPromptText("S11");
		text_s11.setLayoutX(200);
		text_s11.setLayoutY(150);
		text_s12 = new TextField();
		text_s12.setPromptText("S12");
		text_s12.setLayoutX(200);
		text_s12.setLayoutY(200);
		text_s21 = new TextField();
		text_s21.setPromptText("S21");
		text_s21.setLayoutX(200);
		text_s21.setLayoutY(250);
		text_s22 = new TextField();
		text_s22.setPromptText("S22");
		text_s22.setLayoutX(200);
		text_s22.setLayoutY(300);
		button_s = new Button("Calculate");
		button_s.setLayoutX(240);
		button_s.setLayoutY(330);
		button_s.setOnAction(e -> {
			fromS();
		});

		regularPane.getChildren().addAll(text_s11, text_s12, text_s21, text_s22, label_s, button_s);
	}

	private void setupZ() {
		// Instantiate Z stuff
		label_z = new Label("Z");
		label_z.setLayoutX(455);
		label_z.setLayoutY(125);
		text_z11 = new TextField();
		text_z11.setPromptText("Z11");
		text_z11.setLayoutX(400);
		text_z11.setLayoutY(150);
		text_z12 = new TextField();
		text_z12.setPromptText("Z12");
		text_z12.setLayoutX(400);
		text_z12.setLayoutY(200);
		text_z21 = new TextField();
		text_z21.setPromptText("Z21");
		text_z21.setLayoutX(400);
		text_z21.setLayoutY(250);
		text_z22 = new TextField();
		text_z22.setPromptText("Z22");
		text_z22.setLayoutX(400);
		text_z22.setLayoutY(300);
		button_z = new Button("Calculate");
		button_z.setLayoutX(440);
		button_z.setLayoutY(330);
		button_z.setOnAction(e -> {
			fromZ();
		});

		regularPane.getChildren().addAll(text_z11, text_z12, text_z21, text_z22, label_z, button_z);
	}

	private void setupY() {
		// Instantiate Y Stuff
		label_y = new Label("Y");
		label_y.setLayoutX(655);
		label_y.setLayoutY(125);
		text_y11 = new TextField();
		text_y11.setPromptText("Y11");
		text_y11.setLayoutX(600);
		text_y11.setLayoutY(150);
		text_y12 = new TextField();
		text_y12.setPromptText("Y12");
		text_y12.setLayoutX(600);
		text_y12.setLayoutY(200);
		text_y21 = new TextField();
		text_y21.setPromptText("Y21");
		text_y21.setLayoutX(600);
		text_y21.setLayoutY(250);
		text_y22 = new TextField();
		text_y22.setPromptText("Y22");
		text_y22.setLayoutX(600);
		text_y22.setLayoutY(300);
		button_y = new Button("Calculate");
		button_y.setLayoutX(640);
		button_y.setLayoutY(330);
		button_y.setOnAction(e -> {
			fromY();
		});

		regularPane.getChildren().addAll(text_y11, text_y12, text_y21, text_y22, label_y, button_y);
	}

	private void setupABCD() {
		// Instantiate ABCD stuff
		label_abcd = new Label("ABCD");
		label_abcd.setLayoutX(855);
		label_abcd.setLayoutY(125);
		text_A = new TextField();
		text_A.setPromptText("A");
		text_A.setLayoutX(800);
		text_A.setLayoutY(150);
		text_B = new TextField();
		text_B.setPromptText("B");
		text_B.setLayoutX(800);
		text_B.setLayoutY(200);
		text_C = new TextField();
		text_C.setPromptText("C");
		text_C.setLayoutX(800);
		text_C.setLayoutY(250);
		text_D = new TextField();
		text_D.setPromptText("D");
		text_D.setLayoutX(800);
		text_D.setLayoutY(300);
		button_abcd = new Button("Calculate");
		button_abcd.setLayoutX(840);
		button_abcd.setLayoutY(330);
		button_abcd.setOnAction(e -> {
			fromABCD();
		});

		regularPane.getChildren().addAll(text_A, text_B, text_C, text_D, label_abcd, button_abcd);
	}

	private void clearFields() {
		for (TextField field : textfields) {
			field.clear();
		}
	}

	private void fromS() {
		try {
			ComplexNumber s11 = new ComplexNumber().parseComplex(text_s11.getText());
			ComplexNumber s12 = new ComplexNumber().parseComplex(text_s12.getText());
			ComplexNumber s21 = new ComplexNumber().parseComplex(text_s21.getText());
			ComplexNumber s22 = new ComplexNumber().parseComplex(text_s22.getText());

			ComplexNumber z0 = new ComplexNumber().parseComplex(text_z0.getText());

			ComplexNumber z11 = new ComplexNumber().multiply(z0,
					ComplexNumber.divide(
							ComplexNumber.add(
									ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s11),
											ComplexNumber.subtract(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21)),
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.subtract(new ComplexNumber(1, 0), s11),
											ComplexNumber.subtract(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21))));

			ComplexNumber z12 = new ComplexNumber().multiply(z0,
					ComplexNumber.divide(ComplexNumber.multiply(new ComplexNumber(2, 0), s12),
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.subtract(new ComplexNumber(1, 0), s11),
											ComplexNumber.subtract(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21))));

			ComplexNumber z21 = new ComplexNumber().multiply(z0,
					ComplexNumber.divide(ComplexNumber.multiply(new ComplexNumber(2, 0), s21),
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.subtract(new ComplexNumber(1, 0), s11),
											ComplexNumber.subtract(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21))));

			ComplexNumber z22 = new ComplexNumber().multiply(z0,
					ComplexNumber.divide(
							ComplexNumber.add(
									ComplexNumber.multiply(ComplexNumber.subtract(new ComplexNumber(1, 0), s11),
											ComplexNumber.add(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21)),
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.subtract(new ComplexNumber(1, 0), s11),
											ComplexNumber.subtract(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21))));

			text_z11.setText(z11.toString());
			text_z12.setText(z12.toString());
			text_z21.setText(z21.toString());
			text_z22.setText(z22.toString());

			ComplexNumber y0 = z0.inverse();

			ComplexNumber y11 = new ComplexNumber().multiply(y0,
					ComplexNumber.divide(
							ComplexNumber.add(
									ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s11),
											ComplexNumber.subtract(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21)),
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s11),
											ComplexNumber.add(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21))));

			ComplexNumber y12 = new ComplexNumber().multiply(y0,
					ComplexNumber.divide(ComplexNumber.multiply(new ComplexNumber(-2, 0), s12),
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s11),
											ComplexNumber.add(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21))));

			ComplexNumber y21 = new ComplexNumber().multiply(y0,
					ComplexNumber.divide(ComplexNumber.multiply(new ComplexNumber(-2, 0), s21),
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s11),
											ComplexNumber.add(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21))));

			ComplexNumber y22 = new ComplexNumber().multiply(y0,
					ComplexNumber.divide(
							ComplexNumber.add(
									ComplexNumber.multiply(ComplexNumber.subtract(new ComplexNumber(1, 0), s11),
											ComplexNumber.add(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21)),
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s11),
											ComplexNumber.add(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21))));

			text_y11.setText(y11.toString());
			text_y12.setText(y12.toString());
			text_y21.setText(y21.toString());
			text_y22.setText(y22.toString());

			ComplexNumber a = ComplexNumber
					.divide(ComplexNumber.add(
							ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s11),
									ComplexNumber.subtract(new ComplexNumber(1, 0), s22)),
							ComplexNumber.multiply(s12, s21)), ComplexNumber.multiply(new ComplexNumber(2, 0), s21));

			ComplexNumber b = ComplexNumber.multiply(z0,
					ComplexNumber.divide(
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s11),
											ComplexNumber.add(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21)),
							ComplexNumber.multiply(new ComplexNumber(2, 0), s21)));

			ComplexNumber c = ComplexNumber.multiply(y0,
					ComplexNumber.divide(
							ComplexNumber.subtract(
									ComplexNumber.multiply(ComplexNumber.subtract(new ComplexNumber(1, 0), s11),
											ComplexNumber.subtract(new ComplexNumber(1, 0), s22)),
									ComplexNumber.multiply(s12, s21)),
							ComplexNumber.multiply(new ComplexNumber(2, 0), s21)));

			ComplexNumber d = ComplexNumber
					.divide(ComplexNumber.add(
							ComplexNumber.multiply(ComplexNumber.add(new ComplexNumber(1, 0), s22),
									ComplexNumber.subtract(new ComplexNumber(1, 0), s11)),
							ComplexNumber.multiply(s12, s21)), ComplexNumber.multiply(new ComplexNumber(2, 0), s21));

			text_A.setText(a.toString());
			text_B.setText(b.toString());
			text_C.setText(c.toString());
			text_D.setText(d.toString());

		} catch (NumberFormatException e) {
			Alert s_bad = new Alert(AlertType.ERROR,
					"Please make sure all boxes above contain valid numbers. See help for more info.", ButtonType.OK);
			s_bad.showAndWait();
		} catch (Exception e) {
			Alert s_bad = new Alert(AlertType.ERROR, "Uh-oh", ButtonType.OK);
			s_bad.showAndWait();
			e.printStackTrace();
		}
	}

	private void fromZ() {
		try {
			ComplexNumber z11 = new ComplexNumber().parseComplex(text_z11.getText());
			ComplexNumber z12 = new ComplexNumber().parseComplex(text_z12.getText());
			ComplexNumber z21 = new ComplexNumber().parseComplex(text_z21.getText());
			ComplexNumber z22 = new ComplexNumber().parseComplex(text_z22.getText());

			ComplexNumber z0 = new ComplexNumber().parseComplex(text_z0.getText());
			ComplexNumber delt_Z = ComplexNumber.subtract(
					ComplexNumber.multiply(ComplexNumber.add(z11, z0), ComplexNumber.add(z22, z0)),
					ComplexNumber.multiply(z12, z21));
			ComplexNumber mag_Z = ComplexNumber.subtract(ComplexNumber.multiply(z11, z22),
					ComplexNumber.multiply(z12, z21));

			ComplexNumber s11 = ComplexNumber.divide(ComplexNumber.subtract(
					ComplexNumber.multiply(ComplexNumber.subtract(z11, z0), ComplexNumber.add(z22, z0)),
					ComplexNumber.multiply(z12, z21)), delt_Z);
			ComplexNumber s12 = ComplexNumber
					.divide(ComplexNumber.multiply(new ComplexNumber(2, 0), ComplexNumber.multiply(z12, z0)), delt_Z);
			ComplexNumber s21 = ComplexNumber
					.divide(ComplexNumber.multiply(new ComplexNumber(2, 0), ComplexNumber.multiply(z21, z0)), delt_Z);
			ComplexNumber s22 = ComplexNumber.divide(ComplexNumber.subtract(
					ComplexNumber.multiply(ComplexNumber.add(z11, z0), ComplexNumber.subtract(z22, z0)),
					ComplexNumber.multiply(z12, z21)), delt_Z);

			text_s11.setText(s11.toString());
			text_s12.setText(s12.toString());
			text_s21.setText(s21.toString());
			text_s22.setText(s22.toString());

			ComplexNumber y11 = ComplexNumber.divide(z22, mag_Z);
			ComplexNumber y12 = ComplexNumber.divide(ComplexNumber.subtract(new ComplexNumber(0, 0), z12), mag_Z);
			ComplexNumber y21 = ComplexNumber.divide(ComplexNumber.subtract(new ComplexNumber(0, 0), z21), mag_Z);
			ComplexNumber y22 = ComplexNumber.divide(z11, mag_Z);

			text_y11.setText(y11.toString());
			text_y12.setText(y12.toString());
			text_y21.setText(y21.toString());
			text_y22.setText(y22.toString());

			ComplexNumber a = ComplexNumber.divide(z11, z21);
			ComplexNumber b = ComplexNumber.divide(mag_Z, z21);
			ComplexNumber c = z21.inverse();
			ComplexNumber d = ComplexNumber.divide(z22, z21);

			text_A.setText(a.toString());
			text_B.setText(b.toString());
			text_C.setText(c.toString());
			text_D.setText(d.toString());

		} catch (NumberFormatException e) {
			Alert s_bad = new Alert(AlertType.ERROR,
					"Please make sure all boxes above contain valid numbers. See help for more info.", ButtonType.OK);
			s_bad.showAndWait();
		} catch (Exception e) {
			Alert s_bad = new Alert(AlertType.ERROR, "Uh-oh", ButtonType.OK);
			s_bad.showAndWait();
			e.printStackTrace();
		}

	}

	private void fromY() {
		try {
			ComplexNumber y11 = new ComplexNumber().parseComplex(text_y11.getText());
			ComplexNumber y12 = new ComplexNumber().parseComplex(text_y12.getText());
			ComplexNumber y21 = new ComplexNumber().parseComplex(text_y21.getText());
			ComplexNumber y22 = new ComplexNumber().parseComplex(text_y22.getText());

			ComplexNumber z0 = new ComplexNumber().parseComplex(text_z0.getText());
			ComplexNumber y0 = z0.inverse();
			ComplexNumber delt_Y = ComplexNumber.subtract(
					ComplexNumber.multiply(ComplexNumber.add(y11, y0), ComplexNumber.add(y22, y0)),
					ComplexNumber.multiply(y12, y21));
			ComplexNumber mag_Y = ComplexNumber.subtract(ComplexNumber.multiply(y11, y22),
					ComplexNumber.multiply(y12, y21));

			ComplexNumber s11 = ComplexNumber.divide(ComplexNumber.add(
					ComplexNumber.multiply(ComplexNumber.subtract(y0, y11), ComplexNumber.add(y0, y22)),
					ComplexNumber.multiply(y12, y21)), delt_Y);
			ComplexNumber s12 = ComplexNumber
					.divide(ComplexNumber.multiply(new ComplexNumber(-2, 0), ComplexNumber.multiply(y12, y0)), delt_Y);
			ComplexNumber s21 = ComplexNumber
					.divide(ComplexNumber.multiply(new ComplexNumber(-2, 0), ComplexNumber.multiply(y21, y0)), delt_Y);
			ComplexNumber s22 = ComplexNumber.divide(ComplexNumber.add(
					ComplexNumber.multiply(ComplexNumber.add(y11, y0), ComplexNumber.subtract(y0, y22)),
					ComplexNumber.multiply(y12, y21)), delt_Y);

			text_s11.setText(s11.toString());
			text_s12.setText(s12.toString());
			text_s21.setText(s21.toString());
			text_s22.setText(s22.toString());

			ComplexNumber z11 = ComplexNumber.divide(y22, mag_Y);
			ComplexNumber z12 = ComplexNumber.divide(ComplexNumber.subtract(new ComplexNumber(0, 0), y12), mag_Y);
			ComplexNumber z21 = ComplexNumber.divide(ComplexNumber.subtract(new ComplexNumber(0, 0), y21), mag_Y);
			ComplexNumber z22 = ComplexNumber.divide(y11, mag_Y);

			text_z11.setText(z11.toString());
			text_z12.setText(z12.toString());
			text_z21.setText(z21.toString());
			text_z22.setText(z22.toString());

			ComplexNumber a = ComplexNumber.divide(ComplexNumber.subtract(new ComplexNumber(0, 0), y22), y21);
			ComplexNumber b = ComplexNumber.multiply(new ComplexNumber(-1, 0), y21.inverse());
			ComplexNumber c = ComplexNumber.multiply(new ComplexNumber(-1, 0), ComplexNumber.divide(mag_Y, y21));
			ComplexNumber d = ComplexNumber.divide(ComplexNumber.subtract(new ComplexNumber(0, 0), y11), y21);

			text_A.setText(a.toString());
			text_B.setText(b.toString());
			text_C.setText(c.toString());
			text_D.setText(d.toString());

		} catch (NumberFormatException e) {
			Alert s_bad = new Alert(AlertType.ERROR,
					"Please make sure all boxes above contain valid numbers. See help for more info.", ButtonType.OK);
			s_bad.showAndWait();
		} catch (Exception e) {
			Alert s_bad = new Alert(AlertType.ERROR, "Uh-oh", ButtonType.OK);
			s_bad.showAndWait();
			e.printStackTrace();
		}

	}

	private void fromABCD() {
		try {
			ComplexNumber a = ComplexNumber.parseComplex(text_A.getText());
			ComplexNumber b = ComplexNumber.parseComplex(text_B.getText());
			ComplexNumber c = ComplexNumber.parseComplex(text_C.getText());
			ComplexNumber d = ComplexNumber.parseComplex(text_D.getText());

			ComplexNumber z0 = ComplexNumber.parseComplex(text_z0.getText());

			ComplexNumber denom = ComplexNumber.add(a, ComplexNumber.add(ComplexNumber.divide(b, z0),
					ComplexNumber.add(ComplexNumber.multiply(c, z0), d)));

			ComplexNumber s11 = ComplexNumber.divide(ComplexNumber.add(a, ComplexNumber
					.subtract(ComplexNumber.divide(b, z0), ComplexNumber.add(ComplexNumber.multiply(c, z0), d))),
					denom);
			ComplexNumber s12 = ComplexNumber.divide(ComplexNumber.multiply(new ComplexNumber(2, 0),
					ComplexNumber.subtract(ComplexNumber.multiply(a, d), ComplexNumber.multiply(b, c))), denom);
			ComplexNumber s21 = ComplexNumber.divide(new ComplexNumber(2, 0), denom);
			ComplexNumber s22 = ComplexNumber.divide(ComplexNumber.add(d, ComplexNumber
					.subtract(ComplexNumber.divide(b, z0), ComplexNumber.add(ComplexNumber.multiply(c, z0), a))),
					denom);

			text_s11.setText(s11.toString());
			text_s12.setText(s12.toString());
			text_s21.setText(s21.toString());
			text_s22.setText(s22.toString());

			ComplexNumber z11 = ComplexNumber.divide(a, c);
			ComplexNumber z12 = ComplexNumber
					.divide(ComplexNumber.subtract(ComplexNumber.multiply(a, d), ComplexNumber.multiply(b, c)), c);
			ComplexNumber z21 = c.inverse();
			ComplexNumber z22 = ComplexNumber.divide(d, c);

			text_z11.setText(z11.toString());
			text_z12.setText(z12.toString());
			text_z21.setText(z21.toString());
			text_z22.setText(z22.toString());

			ComplexNumber y11 = ComplexNumber.divide(d, b);
			ComplexNumber y12 = ComplexNumber
					.divide(ComplexNumber.subtract(ComplexNumber.multiply(b, c), ComplexNumber.multiply(a, d)), b);
			ComplexNumber y21 = ComplexNumber.multiply(new ComplexNumber(-1, 0), b.inverse());
			ComplexNumber y22 = ComplexNumber.divide(a, b);

			text_y11.setText(y11.toString());
			text_y12.setText(y12.toString());
			text_y21.setText(y21.toString());
			text_y22.setText(y22.toString());

		} catch (NumberFormatException e) {
			Alert s_bad = new Alert(AlertType.ERROR,
					"Please make sure all boxes above contain valid numbers. See help for more info.", ButtonType.OK);
			s_bad.showAndWait();
		} catch (Exception e) {
			Alert s_bad = new Alert(AlertType.ERROR, "Uh-oh", ButtonType.OK);
			s_bad.showAndWait();
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Platform.runLater(() -> regularPane.requestFocus());
		BorderPane temp = new BorderPane();
		temp.setTop(menuBar);
		temp.setCenter(regularPane);

		Scene scene = new Scene(temp, 1200, 475);
		primaryStage.setScene(scene);
		primaryStage.setTitle("JavaFX Two Port Network Calculator");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}