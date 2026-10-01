package ifrn.pi.eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ifrn.pi.eventos.models.Convidado;
import ifrn.pi.eventos.models.Evento;
import ifrn.pi.eventos.repositories.ConvidadoRepository;
import ifrn.pi.eventos.repositories.EventoRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/eventos")
public class EventosController {

    @Autowired
    private EventoRepository er;

    @Autowired
    private ConvidadoRepository cr;

    // FORMULÁRIO PARA ADICIONAR EVENTO
    @GetMapping("/form")
    public String form(Evento evento) {
        return "eventos/formEvento";
    }

    // SALVAR EVENTO
    @PostMapping
    public String salvar(@Valid Evento evento, BindingResult result) {
    	
    	if(result.hasErrors()) {
    		return form(evento);
    	}

        System.out.println(evento);

        er.save(evento);

        return "redirect:/eventos";
    }

    // LISTAR EVENTOS
    @GetMapping
    public ModelAndView listar() {

        List<Evento> eventos = er.findAll();

        ModelAndView mv = new ModelAndView("eventos/lista");

        mv.addObject("eventos", eventos);

        return mv;
    }

 // DETALHES DO EVENTO
    @GetMapping("/{id}")
    public ModelAndView detalhar(@PathVariable Long id) {

        ModelAndView md = new ModelAndView();

        Optional<Evento> opt = er.findById(id);

        if (opt.isEmpty()) {
            md.setViewName("redirect:/eventos");
            return md;
        }

        Evento evento = opt.get();

        List<Convidado> convidados = cr.findByEvento(evento);

        md.setViewName("eventos/detalhes");

        md.addObject("evento", evento);
        md.addObject("convidados", convidados);

        // IMPORTANTE
        md.addObject("convidado", new Convidado());

        return md;
    }
 
    @PostMapping("/{idEvento}")
    public String salvarConvidado(
            @PathVariable Long idEvento,
            @Valid Convidado convidado,
            BindingResult result,
            org.springframework.ui.Model model) {

        System.out.println("ID do evento: " + idEvento);
        System.out.println("Convidado recebido: " + convidado);

        Optional<Evento> optEvento = er.findById(idEvento);

        if (optEvento.isEmpty()) {
            return "redirect:/eventos";
        }

        Evento evento = optEvento.get();

        if (result.hasErrors()) {
            model.addAttribute("evento", evento);
            model.addAttribute("convidados", cr.findByEvento(evento));
            model.addAttribute("convidado", convidado);

            return "eventos/detalhes";
        }

        // EDITAR
        if (convidado.getId() != null) {

            Optional<Convidado> optConvidado =
                    cr.findById(convidado.getId());

            if (optConvidado.isPresent()) {

                Convidado convidadoExistente = optConvidado.get();

                if (convidadoExistente.getEvento() != null
                        && convidadoExistente.getEvento().getId().equals(idEvento)) {

                    convidadoExistente.setNome(convidado.getNome());
                    convidadoExistente.setRg(convidado.getRg());

                    cr.save(convidadoExistente);

                    return "redirect:/eventos/" + idEvento
                            + "?mensagem=Convidado atualizado com sucesso!";
                }
            }

        } else {

            // NOVO
            convidado.setEvento(evento);
            cr.save(convidado);

            return "redirect:/eventos/" + idEvento
                    + "?mensagem=Convidado adicionado com sucesso!";
        }

        return "redirect:/eventos/" + idEvento;
    }
    // EDITAR CONVIDADO
    @GetMapping("/{idEvento}/convidados/{idConvidado}/selecionar")
    public ModelAndView selecionarConvidado(
            @PathVariable Long idEvento,
            @PathVariable Long idConvidado) {

        ModelAndView md = new ModelAndView();

        Optional<Evento> optEvento = er.findById(idEvento);
        Optional<Convidado> optConvidado = cr.findById(idConvidado);

        if (optEvento.isEmpty() || optConvidado.isEmpty()) {
            md.setViewName("redirect:/eventos");
            return md;
        }

        Evento evento = optEvento.get();
        Convidado convidado = optConvidado.get();

        // CONFERE SE O CONVIDADO PERTENCE AO EVENTO
        if (convidado.getEvento() == null
                || !evento.getId().equals(convidado.getEvento().getId())) {

            md.setViewName("redirect:/eventos");
            return md;
        }

        md.setViewName("eventos/detalhes");

        md.addObject("evento", evento);
        md.addObject("convidado", convidado);
        md.addObject("convidados", cr.findByEvento(evento));

        return md;
    }

    // REMOVER EVENTO
    @GetMapping("/{id}/remover")
    public String apagarEvento(@PathVariable Long id) {

        Optional<Evento> opt = er.findById(id);

        if (opt.isPresent()) {

            Evento evento = opt.get();

            List<Convidado> convidados =
                    cr.findByEvento(evento);

            cr.deleteAll(convidados);

            er.delete(evento);
        }

        return "redirect:/eventos";
    }

    // REMOVER CONVIDADO
    @GetMapping("/{idEvento}/convidados/{idConvidado}/remover")
    public String apagarConvidado(
            @PathVariable Long idEvento,
            @PathVariable Long idConvidado) {

        Optional<Convidado> optConvidado =
                cr.findById(idConvidado);

        if (optConvidado.isPresent()) {

            Convidado convidado = optConvidado.get();

            if (convidado.getEvento() != null
                    && convidado.getEvento().getId().equals(idEvento)) {

                cr.delete(convidado);
            }
        }

        return "redirect:/eventos/" + idEvento;
    }
}