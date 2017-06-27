package de.haw.md.gui;

import java.io.IOException;
import java.math.BigDecimal;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import de.haw.md.akka.main.ActorSystemContainer;
import de.haw.md.akka.main.Network;
import de.haw.md.akka.main.NetworkContainer;
import de.haw.md.akka.main.NetworkNode;
import de.haw.md.helper.MDHelper;
import de.haw.md.helper.StaticValues;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scala.concurrent.duration.Duration;

public class NetworkGuiMain extends Application {

	private static String CHANNEL = "NetworkTest";

	private static final double SCREEN_X = 1600;
	private static final double SCREEN_Y = 1000;

	private Stage primaryStage;
	private AnchorPane rootLayout;

	private static final long SEQUENCE_DURATION = 100;
	private static final long PACKAGE_DURATION = SEQUENCE_DURATION * 1;
	private Timeline animation;

	private TextField fieldsumMsg;
	private TextField fieldsumResp;
	private TextField fieldsumSoliMsg;
	private TextField fieldsumSoliRespMsg;

	private AreaChart<Number, Number> lineChart;
	private XYChart.Series<Number, Number> chartSeriesNetworkMsg;
	private XYChart.Series<Number, Number> chartSeriesNetworkRespMsg;
	private XYChart.Series<Number, Number> chartSeriesSoliMsg;
	private XYChart.Series<Number, Number> chartSeriesSoliRespMsg;
	private XYChart.Series<Number, Number> chartFailedMsg;
	
	private ChoiceBox<String> knotsdeakt;
	private ChoiceBox<String> knotsakt;

	private int counter = 0;

	private final static ActorSystem SYSTEM = ActorSystemContainer.getInstance().getSystem();

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Network Simulation");

		initRootLayout();

		lineChart = (AreaChart<Number, Number>) primaryStage.getScene().lookup("#lineChart");
		NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
		NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(100);
		xAxis.setTickUnit(5);
		xAxis.setLabel("Vergangene Ticks");
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(40);
		yAxis.setTickUnit(2);
		yAxis.setLabel("Anzahl Nachrichten");
		chartSeriesNetworkMsg = new XYChart.Series<>();
		chartSeriesNetworkMsg.setName("NetworkMsg");
		chartSeriesNetworkRespMsg = new XYChart.Series<>();
		chartSeriesNetworkRespMsg.setName("NetworkResponseMsg");
		chartSeriesSoliMsg = new XYChart.Series<>();
		chartSeriesSoliMsg.setName("SoliMsg");
		chartSeriesSoliRespMsg = new XYChart.Series<>();
		chartSeriesSoliRespMsg.setName("SoliRespMsg");
		chartFailedMsg = new XYChart.Series<>();
		chartFailedMsg.setName("FailedMsg");
		lineChart.getData().addAll(chartSeriesNetworkMsg, chartSeriesNetworkRespMsg, chartSeriesSoliMsg,
				chartSeriesSoliRespMsg, chartFailedMsg);
		fieldsumMsg = (TextField) primaryStage.getScene().lookup("#sumMsg");
		fieldsumResp = (TextField) primaryStage.getScene().lookup("#sumMsgResp");
		fieldsumSoliMsg = (TextField) primaryStage.getScene().lookup("#sumSoliMsg");
		fieldsumSoliRespMsg = (TextField) primaryStage.getScene().lookup("#sumSoliRespMsg");
		fieldsumMsg.setText("0");
		fieldsumResp.setText("0");
		fieldsumSoliMsg.setText("0");
		fieldsumSoliRespMsg.setText("0");
		
		Button btnknotsdeakt = (Button) primaryStage.getScene().lookup("#btnknotsdeakt");
		btnknotsdeakt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				String selectedNodeID = knotsdeakt.getSelectionModel().getSelectedItem();
				NetworkContainer.getInstance().getNetwork().sendActorController(selectedNodeID, false);
			}
		});

		Button btnknotsakt = (Button) primaryStage.getScene().lookup("#btnknotsakt");
		btnknotsakt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				String selectedNodeID = knotsakt.getSelectionModel().getSelectedItem();
				NetworkContainer.getInstance().getNetwork().sendActorController(selectedNodeID, true);
			}
		});
		
		animation = new Timeline();
		animation.getKeyFrames()
				.add(new KeyFrame(javafx.util.Duration.millis(SEQUENCE_DURATION), new EventHandler<ActionEvent>() {

					private long timer = 0;

					@Override
					public void handle(ActionEvent arg0) {
						knotsakt = (ChoiceBox<String>) primaryStage.getScene().lookup("#knotsakt");
						knotsdeakt = (ChoiceBox<String>) primaryStage.getScene().lookup("#knotsdeakt");
						final ActorRef publisher = NetworkContainer.getInstance().getPublisher(CHANNEL);
						if (timer == 0 || System.currentTimeMillis() > timer) {
							SYSTEM.scheduler().scheduleOnce(Duration.Zero(), publisher, "Tick", SYSTEM.dispatcher(),
									publisher);
							timer = System.currentTimeMillis() + PACKAGE_DURATION;
						}
						BigDecimal networkMsgModelsList = MDHelper.getInstance().getNetworkMsgModelsList();
						final ObservableList<Data<Number, Number>> seriesNM = chartSeriesNetworkMsg.getData();
						if (networkMsgModelsList.compareTo(BigDecimal.ZERO) != 0)
							seriesNM.add(new XYChart.Data<Number, Number>(counter, networkMsgModelsList));
						BigDecimal networkMsgResponseModelsList = MDHelper.getInstance()
								.getNetworkMsgResponseModelsList();
						final ObservableList<Data<Number, Number>> seriesNRM = chartSeriesNetworkRespMsg.getData();
						if (networkMsgResponseModelsList.compareTo(BigDecimal.ZERO) != 0)
							seriesNRM.add(new XYChart.Data<Number, Number>(counter, networkMsgResponseModelsList));
						BigDecimal solicitationMsgModelsList = MDHelper.getInstance().getSolicitationMsgModelsList();
						final ObservableList<Data<Number, Number>> seriesSM = chartSeriesSoliMsg.getData();
						seriesSM.add(new XYChart.Data<Number, Number>(counter, solicitationMsgModelsList));
						BigDecimal solicitationResponseMsgModelsList = MDHelper.getInstance()
								.getSolicitationResponseMsgModelsList();
						final ObservableList<Data<Number, Number>> seriesSRM = chartSeriesSoliRespMsg.getData();
						seriesSRM.add(new XYChart.Data<Number, Number>(counter, solicitationResponseMsgModelsList));
						BigDecimal failedMsg = MDHelper.getInstance().getFailedNetworkMsgModelsList();
						final ObservableList<Data<Number, Number>> seriesFM = chartFailedMsg.getData();
						seriesFM.add(new XYChart.Data<Number, Number>(counter, failedMsg));
						counter++;
						NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
						if (counter + 10 >= xAxis.getUpperBound()) {
							xAxis.setUpperBound(xAxis.getUpperBound() + 1);
							xAxis.setLowerBound(xAxis.getLowerBound() + 1);
						}
						fieldsumMsg.setText(new BigDecimal(fieldsumMsg.getText()).add(networkMsgModelsList).toString());
						fieldsumResp.setText(new BigDecimal(fieldsumResp.getText()).add(networkMsgResponseModelsList).toString());
						fieldsumSoliMsg.setText(new BigDecimal(fieldsumSoliMsg.getText()).add(solicitationMsgModelsList).toString());
						fieldsumSoliRespMsg.setText(new BigDecimal(fieldsumSoliRespMsg.getText()).add(solicitationResponseMsgModelsList).toString());
					}

				}));
		animation.setCycleCount(Animation.INDEFINITE);
		animation.play();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(NetworkGuiMain.class.getResource("Overview.fxml"));
			rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout, SCREEN_X, SCREEN_Y);
			primaryStage.setScene(scene);
			primaryStage.resizableProperty().set(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {

		for (int i = 0; i < StaticValues.NODES.length; i++)
			SYSTEM.actorOf(Props.create(NetworkNode.class, CHANNEL, StaticValues.NODES[i]));

		SYSTEM.actorOf(Props.create(Network.class, CHANNEL));
		// final ActorRef publisher =
		// NetworkContainer.getInstance().getPublisher(CHANNEL);
		// SYSTEM.scheduler().schedule(Duration.Zero(), Duration.create(1000,
		// TimeUnit.MILLISECONDS), publisher, "Tick",
		// SYSTEM.dispatcher(), publisher);

		launch(args);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void stop() {
		System.out.println("Window Closed");
		ActorSystemContainer.getInstance().getSystem().shutdown();
	}
}
