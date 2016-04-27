package edu.hendrix.ev3.local.videoview;

import java.util.EnumSet;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.concurrent.ArrayBlockingQueue;

import edu.hendrix.ev3.ai.TrainingList;
import edu.hendrix.ev3.ai.bsoc.BoundedSelfOrgCluster;
import edu.hendrix.ev3.ai.Move;
import edu.hendrix.ev3.storage.VideoStorage;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import edu.hendrix.ev3.ai.cluster.AdaptedYUYVImage;
import edu.hendrix.ev3.ai.cluster.YUYVDistanceFuncs;
import edu.hendrix.ev3.util.Duple;

public class VideoViewController {
	@FXML
	ChoiceBox<Move> moveChoice;
	
	@FXML
	ChoiceBox<String> sessionChoice;
	
	@FXML
	CheckBox useAllMoves;
	
	@FXML
	Label positionInList;
	
	@FXML
	Label currentMove;
	
	@FXML
	Button goLeft;
	
	@FXML
	Button goRight;
	
	@FXML
	Slider howFar;
	
	@FXML
	Button reset;
	
	@FXML
	Canvas canv;
	
	@FXML
	TextField messages;
	
	@FXML
	Button animate;
	
	@FXML
	Button stopAnimation;
	
	@FXML
	Slider frameRate;

	VideoStorage videos;
	private TrainingList examples;
	private int current;
	private Timeline animator;
	
	@FXML
	Canvas idealVisual;
	@FXML
	Button train;
	@FXML
	TextField numNodes;
	@FXML
	Button nextNode;
	@FXML
	TextField currentNode;
	@FXML
	Button prevNode;
	@FXML
	CheckBox showNode;
	
	@FXML
	ChoiceBox<Integer> shrink;
	
	BoundedSelfOrgCluster<AdaptedYUYVImage> trained;

	@FXML
	void initialize() {
		useAllMoves.setSelected(true);
		
		videos = VideoStorage.getPCStorage();
		setUpChoice(sessionChoice, videos.getVideoChoices());
		setupSessions();

		for (Move m: Move.values()) {
			moveChoice.getItems().add(m);
		}

		sessionChoice.getSelectionModel().selectLast();

		goLeft.setOnAction(event -> goLeft());
		goRight.setOnAction(event -> goRight());

		reset.setOnAction(event -> reset());
		
		animate.setOnAction(event -> animate());
		frameRate.valueProperty().addListener((obs,oldV,newV) -> animate());
		stopAnimation.setOnAction(event -> pause());
		
		for (int i: new int[]{1, 2, 4, 8, 10, 20}) {
			shrink.getItems().add(i);
		}
		shrink.getSelectionModel().select(0);
		
		shrink.getSelectionModel().selectedItemProperty().addListener((ov, oldv, newv) -> renderCurrent());
	}
	
	void setUpChoice(ChoiceBox<String> choices, String[] src) {
		for (String t: src) {
			choices.getItems().add(t);
		}
		choices.getSelectionModel().selectLast();
	}
	
	void setupSessions() {
		for (String session: videos.getVideoChoices()) {
			sessionChoice.getItems().add(session);
		}		
	}
	
	void reset() {
		current = 0;
		examples = new TrainingList();
		BlockingQueue<Duple<Move,AdaptedYUYVImage>> imgs = new ArrayBlockingQueue<>(1);
		startProducerThread(sessionChoice.getSelectionModel().getSelectedItem(), imgs);
		startConsumerThread(imgs, mImg -> Platform.runLater(() -> loadImage(mImg)));
	}
	
	private void loadImage(Duple<Move,AdaptedYUYVImage> mImg) {
		examples.add(mImg);
		if (examples.size() == 1) {
			renderCurrent();
		} else {
			updateCurrentMsg();
		}
	}

	void animate() {
		pause();
		animator = new Timeline(new KeyFrame(Duration.ZERO, (e -> goRight())), 
								new KeyFrame(Duration.millis(1000.0 / frameRate.getValue())));		
		animator.setCycleCount(Timeline.INDEFINITE);
		animator.play();		
	}
	
	void pause() {
		if (animator != null) {
			animator.stop();
		}
	}
	
	void go(IntSupplier currentUpdater) {
		current = currentUpdater.getAsInt();
		renderCurrent();
	}
	
	void goLeft() {
		go(() -> (examples.size() + current - howFar()) % examples.size());
	}
	
	void goRight() {
		go(() -> (current + howFar()) % examples.size());
	}
	
	int howFar() {
		return (int)howFar.getValue();
	}
	
	void startProducerThread(String session, BlockingQueue<Duple<Move,AdaptedYUYVImage>> imgs) {
		new Thread(() -> {
			try {
				videos.threadedOpen(session, getPermittedSet(), imgs);
			} catch (Exception e) {
				reportProblem(e);
			} 
		}).start();
	}
	
	void startConsumerThread(BlockingQueue<Duple<Move,AdaptedYUYVImage>> imgs, Consumer<Duple<Move,AdaptedYUYVImage>> consumer) {
		new Thread(() -> {
			try {
				for (;;) {
					Duple<Move,AdaptedYUYVImage> mImg = imgs.take();
					if (mImg.getFirst() == Move.NONE) {
						break;
					} else {
						consumer.accept(mImg);
					}
				}
			} catch (Exception e) {
				reportProblem(e);
			}
		}).start();
	}
	
	void reportProblem(Exception e) {
		Platform.runLater(() -> messages.setText(e.getMessage()));
	}
	
	EnumSet<Move> getPermittedSet() {
		return useAllMoves.isSelected() 
				? EnumSet.allOf(Move.class) 
				: EnumSet.of(moveChoice.getSelectionModel().getSelectedItem());
	}
	
	AdaptedYUYVImage currentFrame() {
		AdaptedYUYVImage img = examples.get(current).getSecond().shrunken(shrink.getValue());
		return img;
	}
	
	Move currentMove() {
		return examples.get(current).getFirst();
	}

	@FXML
	void renderCurrent() {
		if (current < examples.size()) {
			placeOnCanvas(currentFrame(), canv);
			updateCurrentMsg();
			currentMove.setText(currentMove().toString());
			if (showNode.isSelected()) {
				goBestNode();
			}
		}
	}
	
	void updateCurrentMsg() {
		positionInList.setText(String.format("%d / %d   ", current + 1, examples.size()));		
	}
	
	public static void placeOnCanvas(AdaptedYUYVImage img, Canvas canv) {
		double cellWidth = canv.getWidth() / img.getWidth();
		double cellHeight = canv.getHeight() / img.getHeight();
		GraphicsContext g = canv.getGraphicsContext2D();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				g.setFill(img.getRGBColor(x, y));
				g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
			}
		}
	}
	
	@FXML
	void train() {
		try {
			int nodes = Integer.parseInt(numNodes.getText());
			trained = new BoundedSelfOrgCluster<>(nodes, YUYVDistanceFuncs::euclideanAllChannels);
			for (Duple<Move, AdaptedYUYVImage> img: examples) {
				trained.train(img.getSecond().shrunken(shrink.getValue()));
			}
			showTrainedNode(0);
		} catch (NumberFormatException nfe) {
			numNodes.setText(numNodes.getText() + ": not an integer");
		}
	}
	
	void showTrainedNode(int node) {
		if (trained != null && node >= 0 && node < trained.size()) {
			currentNode.setText(Integer.toString(node));
			placeOnCanvas(trained.getIdealInputFor(node), idealVisual);
		}
	}
	
	@FXML
	void handleUpdatedNode() {
		try {
			showTrainedNode(Integer.parseInt(currentNode.getText()));
		} catch (NumberFormatException nfe) {
			numNodes.setText(numNodes.getText() + ": not an integer");
		}
	}
	
	@FXML
	void goNextNode() {
		int updated = (1 + Integer.parseInt(currentNode.getText())) % trained.size();
		showTrainedNode(updated);
	}
	
	@FXML
	void goPrevNode() {
		int updated = (Integer.parseInt(currentNode.getText()) - 1 + trained.size()) % trained.size();
		showTrainedNode(updated);
	}
	
	@FXML
	void goBestNode() {
		showTrainedNode(trained.getClosestMatchFor(currentFrame()));
	}
}
