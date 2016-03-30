package edu.hendrix.ev3.ai;

import java.io.FileNotFoundException;
import java.util.EnumSet;

import edu.hendrix.ev3.storage.VideoStorage;

public class VideoEvalRobotLearner {
	
	public static TrainingList retrieveVideos(String id) throws FileNotFoundException {
		VideoStorage videos = VideoStorage.getPCStorage();
		TrainingList result = videos.open(id, EnumSet.allOf(Move.class));
		return result;
	}
}
