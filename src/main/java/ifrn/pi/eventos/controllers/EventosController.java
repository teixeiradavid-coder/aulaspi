package ifrn.pi.eventos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ifrn.pi.eventos.models.Evento;

@Controller
public class EventosController {
	 
	@RequestMapping("/eventos/form")
	public String form() {
		return "formEvento";
	}
	
	@PostMapping("/evento")
	public String cadastrarEvento(Evento evento) {
		System.out.println("Método cadastrarEvento foi chamado!");
		
		System.out.println("Nome:" + evento.getNome());
		System.out.println("Local:" + evento.getLocal());
		System.out.println("Data:" + evento.getData());
		System.out.println("Horário:" + evento.getHorario());
		
		return "home";
	}
	
	@PostMapping("/eventos")
	public String adicionar(Evento evento) {
		
		System.out.println(evento);
		
		return "evento-adicionado";
	}

}
