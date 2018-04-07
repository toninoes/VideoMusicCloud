package vmc.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vmc.service.UsuarioService;
import vmc.service.VideoService;

@Controller
@RequestMapping("/videos")
public class VideoWebController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private VideoService videoService;
    
    @GetMapping
    public String listarTodosVideos(Model model) {
		model.addAttribute("videos", videoService.findAll());
        return "videos/listado";
    }
    
    @GetMapping("/usuario/{id}")
	public String findVideosByUsuarioId(Model model, @PathVariable long id) {
		model.addAttribute("videos", videoService.findVideosByUsuarioId(id));
		model.addAttribute("usuario", usuarioService.findById(id));
		return "videos/listadoPorUsuario";
	}
	
    @GetMapping("/{nombrevideo:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servirVideo(@PathVariable String nombrevideo) {
    	return videoService.servirVideo(nombrevideo);
    }
    
    @PostMapping
    public String subirVideo(@RequestParam("titulo") String t, @RequestParam("video") MultipartFile v, RedirectAttributes ra) {
    	videoService.subirVideo(t, v);
        ra.addFlashAttribute("mensaje", "Video " + v.getOriginalFilename() + " subido correctamente.");

        return "redirect:/videos";
    }
    
    

}
