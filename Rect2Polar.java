import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Rect2Polar extends Application {

	private Pane pane;
	private TextField text_x, text_y, text_r, text_theta;
	private TextField[] fields;
	private Button convert, clear;
	private Label rectLabel, polarLabel, polarLabel2, mainLabel;

	public Rect2Polar() {
		pane = new Pane();
		
		mainLabel = new Label("Rectangular to Polar Converter");
		mainLabel.setLayoutX(68);
		mainLabel.setLayoutY(30);

		text_x = new TextField();
		text_x.setLayoutX(60);
		text_x.setLayoutY(60);
		text_x.setPromptText("X");
		text_x.setPrefWidth(60);

		text_y = new TextField();
		text_y.setLayoutX(180);
		text_y.setLayoutY(60);
		text_y.setPromptText("Y");
		text_y.setPrefWidth(60);

		text_r = new TextField();
		text_r.setLayoutX(60);
		text_r.setLayoutY(180);
		text_r.setPromptText("R");
		text_r.setPrefWidth(60);

		text_theta = new TextField();
		text_theta.setLayoutX(180);
		text_theta.setLayoutY(180);
		text_theta.setPromptText("θ");
		text_theta.setPrefWidth(60);

		fields = new TextField[] { text_x, text_y, text_r, text_theta };

		clear = new Button("Clear");
		clear.setLayoutX(130);
		clear.setLayoutY(240);
		clear.setOnAction(e -> {
			for (TextField field : fields) {
				field.clear();
			}
		});

		convert = new Button("^ Convert v");
		convert.setLayoutX(110);
		convert.setLayoutY(120);
		Platform.runLater(()-> convert.requestFocus());
		convert.setOnAction(e -> {
			tryConvert();
		});

		rectLabel = new Label("+/- \t   j");
		rectLabel.setLayoutX(130);
		rectLabel.setLayoutY(65);

		polarLabel = new Label("∠");
		polarLabel.setLayoutX(140);
		polarLabel.setLayoutY(185);

		polarLabel2 = new Label("°");
		polarLabel2.setLayoutX(245);
		polarLabel2.setLayoutY(185);

		pane.getChildren().addAll(text_x, text_y, text_r, text_theta, convert, clear, rectLabel, polarLabel,
				polarLabel2, mainLabel);
	}

	private void tryConvert() {
		try {
			if (text_x.getText().equalsIgnoreCase("") && text_y.getText().equalsIgnoreCase("")
					&& !text_r.getText().equalsIgnoreCase("") && !text_theta.getText().equalsIgnoreCase("")) {
				double[] rect = polar2rect(new double[] { Double.parseDouble(text_r.getText()),
						Double.parseDouble(text_theta.getText()) });
				text_x.setText(String.format("%.3f", rect[0]));
				text_y.setText(String.format("%.3f", rect[1]));
			} else if (!text_x.getText().equalsIgnoreCase("") && !text_y.getText().equalsIgnoreCase("")
					&& text_r.getText().equalsIgnoreCase("") && text_theta.getText().equalsIgnoreCase("")) {
				double[] polar = rect2polar(
						new double[] { Double.parseDouble(text_x.getText()), Double.parseDouble(text_y.getText()) });
				text_r.setText(String.format("%.3f", polar[0]));
				text_theta.setText(String.format("%.3f", Math.toDegrees(polar[1])));
			} else {
				Alert fields_bad = new Alert(AlertType.ERROR,
						"Please make sure only one set of boxes is filled, and that entered values are valid.",
						ButtonType.OK);
				fields_bad.showAndWait();
			}
		} catch (Exception e) {
			Alert fields_bad = new Alert(AlertType.ERROR,
					"Please make sure only one set of boxes is filled, and that entered values are valid.",
					ButtonType.OK);
			fields_bad.showAndWait();
			e.printStackTrace();
		}

	}

	private double[] rect2polar(double[] rect) {
		double r = new ComplexNumber(rect[0], rect[1]).mod();
		double theta = new ComplexNumber(rect[0], rect[1]).getArg();
		return new double[] { r, theta };
	}

	private double[] polar2rect(double[] polar) {
		double x = polar[0] * Math.cos(Math.toRadians(polar[1]));
		double y = polar[0] * Math.sin(Math.toRadians(polar[1]));
		return new double[] { x, y };
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Scene scene = new Scene(pane, 300, 300);
		primaryStage.setScene(scene);
		primaryStage.setTitle("JavaFX Rectangular-Polar Converter");

		// Display the GUI
		primaryStage.show();

	}

}
