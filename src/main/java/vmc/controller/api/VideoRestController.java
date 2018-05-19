package vmc.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vmc.model.Video;
import vmc.service.VideoService;

@RestController
@RequestMapping("/api/videos")
public class VideoRestController {
	
	@Autowired
	private VideoService videoService;
	
	@GetMapping("/{videoId}/{visualizaciones}")
	public void vistos(@PathVariable long videoId, @PathVariable long visualizaciones) {
		
		Video video = videoService.findById(videoId);
		videoService.saveVisit(visualizaciones + 1, video);
	}
}
