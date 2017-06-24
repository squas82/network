package de.haw.md.gui;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

	private static final long SEQUENCE_DURATION = 500;
	private Timeline animation;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Network Simulation");

		initRootLayout();

		animation = new Timeline();
		animation.getKeyFrames()
				.add(new KeyFrame(javafx.util.Duration.millis(SEQUENCE_DURATION), new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						TextField fieldsumMsg = (TextField) primaryStage.getScene().lookup("#sumMsg");
						TextField fieldsumResp = (TextField) primaryStage.getScene().lookup("#sumMsgResp");
						TextField fieldsumSoliMsg = (TextField) primaryStage.getScene().lookup("#sumSoliMsg");
						TextField fieldsumSoliRespMsg = (TextField) primaryStage.getScene().lookup("#sumSoliRespMsg");
						if (fieldsumMsg != null)
							fieldsumMsg.setText(String.valueOf(MDHelper.getInstance().getNetworkMsgModelsList().size()));
						if (fieldsumResp != null)
							fieldsumResp.setText(String.valueOf(MDHelper.getInstance().getNetworkMsgResponseModelsList().size()));
						if (fieldsumSoliMsg != null)
							fieldsumSoliMsg.setText(String.valueOf(MDHelper.getInstance().getSolicitationMsgModelsList().size()));
						if (fieldsumSoliRespMsg != null)
							fieldsumSoliRespMsg.setText(String.valueOf(MDHelper.getInstance().getSolicitationResponseMsgModelsList().size()));
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
//		ActorSystem system = ActorSystemContainer.getInstance().getSystem();
//
//		for (int i = 0; i < StaticValues.NODES.length; i++)
//			system.actorOf(Props.create(NetworkNode.class, CHANNEL, StaticValues.NODES[i]));
//
//		system.actorOf(Props.create(Network.class, CHANNEL));
//		final ActorRef publisher = NetworkContainer.getInstance().getPublisher(CHANNEL);
//
//		system.scheduler().schedule(Duration.Zero(), Duration.create(1000, TimeUnit.MILLISECONDS), publisher, "Tick",
//				system.dispatcher(), publisher);

		launch(args);
	}
}
