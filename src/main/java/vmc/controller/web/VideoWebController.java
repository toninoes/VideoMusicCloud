package vmc.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import vmc.model.Usuario;
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
    
    @GetMapping("/misvideos")
	public String findVideosByUsuarioId(Model model) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		model.addAttribute("videos", videoService.findVideosByUsuarioId(usuario.getId()));
		model.addAttribute("usuario", usuario);
		return "videos/listadoPorUsuario";
	}
	
    @GetMapping("/{nombrevideo:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servirVideo(@PathVariable String nombrevideo) {
    	return videoService.descargar(nombrevideo);
    }
    
    @PostMapping
    public String subirVideo(@RequestParam("titulo") String t, @RequestParam("video") MultipartFile v, RedirectAttributes ra) {
    	videoService.subir(t, v);
        ra.addFlashAttribute("mensaje", "Video " + v.getOriginalFilename() + " subido correctamente.");

        return "redirect:/videos";
    }
    
    

}
