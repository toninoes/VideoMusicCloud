package vmc.controller.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vmc.model.Usuario;
import vmc.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping
	public String findAll(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/listado";
	}
	
	@GetMapping("/{id}")
	public String findById(Model model, @PathVariable long id) {
		Usuario usuario = usuarioService.findById(id);
		model.addAttribute("usuario", usuario);
		String[] intereses = usuarioService.findInteresesByUsuario(usuario);
		model.addAttribute("intereses", intereses);
		return "usuarios/detalle";
	}
	
	@GetMapping("/mail/{mail}")
	public String findByMail(Model model, @PathVariable String mail) {
		model.addAttribute("usuario", usuarioService.findByMail(mail));
		return "usuarios/detalle";
	}
	
	@GetMapping("/perfil")
	public String findMyVideos(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioService.findByMail(auth.getName());
		model.addAttribute("videos", usuarioService.findVideosByUsuarioId(usuario.getId()));
		model.addAttribute("usuario", usuario);
		return "usuarios/perfil";
	}
	
	@GetMapping("/fotos/{nombrefoto:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servirFoto(@PathVariable String nombrefoto) {
    	return usuarioService.descargar(nombrefoto);
    }
	
	@GetMapping("/perfil/{logueadoId}/{pinchadoId}")
	public String findPerfil(Model model, @PathVariable long logueadoId,
										  @PathVariable long pinchadoId) {
		String sigue = "";		
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		if(logueado.getId() == pinchadoId)
			sigue = "hidden";
		else if(usuarioService.findBySiguiendo(logueado, pinchado)) 
			sigue = "dejar";
		else
			sigue = "seguir";
		
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("videos", usuarioService.findVideosByUsuarioId(pinchadoId));
		model.addAttribute("sigue", sigue);
		
		String[] intereses = usuarioService.findInteresesByUsuario(pinchado);
		model.addAttribute("intereses", intereses);
		
		return "usuarios/perfil";
	}
	
	@GetMapping("/perfil/{logueadoId}/{pinchadoId}/{sigue}")
	public String cambiarBoton(Model model, @PathVariable long logueadoId, 
			                                @PathVariable long pinchadoId, 
			                                @PathVariable String sigue, 
			                                RedirectAttributes ra) {
		
		Usuario logueado = usuarioService.findById(logueadoId);
		Usuario pinchado = usuarioService.findById(pinchadoId);
		
		if(!sigue.equals("hidden") && usuarioService.findBySiguiendo(logueado, pinchado)) {
			usuarioService.dejar(logueado, pinchado);
			sigue = "seguir";
		} else if(!sigue.equals("hidden")) {
			usuarioService.seguir(logueado, pinchado);
			sigue = "dejar";
		}
		
		model.addAttribute("logueado", logueado);
		model.addAttribute("usuario", pinchado);
		model.addAttribute("videos", usuarioService.findVideosByUsuarioId(pinchadoId));
		model.addAttribute("sigue", sigue);
		
		String[] intereses = usuarioService.findInteresesByUsuario(pinchado);
		model.addAttribute("intereses", intereses);
		
		return "usuarios/perfil";
	}
	
	@PostMapping("/detalle/{id}")
	public String savePerfilById(@PathVariable long id, @RequestParam("foto") MultipartFile f,
														@RequestParam("nombre") String nombre,
														@RequestParam("apellidos") String apellidos,
														@RequestParam("mail") String mail,
														@RequestParam(required = false) boolean chksalir,
														@RequestParam(required = false) boolean chkmusica,
														@RequestParam(required = false) boolean chkdinero,
														@RequestParam(required = false) boolean chkdeporte) {
		
		String[] interesesChk = usuarioService.viewIntereses(chksalir, chkmusica, chkdinero, chkdeporte);
		usuarioService.subir(f);
		usuarioService.update(id, nombre, apellidos, mail, interesesChk);
		
		return "redirect:/usuarios/{id}";
	}
	
	@PostMapping("/perfil/{logueadoId}/{pinchadoId}")
    public String subirFoto(@RequestParam("foto") MultipartFile f, RedirectAttributes ra) {
		usuarioService.subir(f);
        ra.addFlashAttribute("mensaje", "Foto " + f.getOriginalFilename() + " subida correctamente.");

        return "redirect:/usuarios/perfil/{logueadoId}/{pinchadoId}";
    }
	
	@PutMapping("/{id}")	
	public String update(Model model, @PathVariable(value = "id") Long id, @Valid @RequestBody Usuario u) {
		model.addAttribute("usuario", usuarioService.update(id, u));
		return "usuarios/detalle";
	}
	
	@DeleteMapping("/{id}")
	public String delete(Model model, @PathVariable(value = "id") Long id) {
		model.addAttribute("usuario", usuarioService.delete(id));
        return "usuarios/listado";
	}

}
